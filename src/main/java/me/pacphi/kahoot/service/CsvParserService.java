package me.pacphi.kahoot.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import me.pacphi.kahoot.model.CsvPreviewResponse;
import me.pacphi.kahoot.model.ValidationError;
import me.pacphi.kahoot.model.ValidationError.ErrorType;

/**
 * Service for parsing CSV files containing quiz questions and converting them
 * to KahootQuestion objects with validation.
 *
 * <p>Supports flexible column detection (case-insensitive) and provides
 * detailed validation errors for problematic rows.
 */
@Service
public class CsvParserService {

    private static final Logger log = LoggerFactory.getLogger(CsvParserService.class);
    private static final int MAX_QUESTIONS = 100;
    private static final int MIN_TIME_LIMIT = 5;
    private static final int MAX_TIME_LIMIT = 240;

    // Column name patterns (case-insensitive)
    private static final List<String> QUESTION_PATTERNS = Arrays.asList("question", "q", "question_text", "question text");
    private static final List<List<String>> OPTION_PATTERNS = Arrays.asList(
        Arrays.asList("option 1", "option1", "answer 1", "choice 1", "a"),
        Arrays.asList("option 2", "option2", "answer 2", "choice 2", "b"),
        Arrays.asList("option 3", "option3", "answer 3", "choice 3", "c"),
        Arrays.asList("option 4", "option4", "answer 4", "choice 4", "d")
    );
    private static final List<String> CORRECT_ANSWER_PATTERNS = Arrays.asList("correct answer", "correct", "answer", "solution");
    private static final List<String> TIME_LIMIT_PATTERNS = Arrays.asList("time limit", "time", "seconds", "duration");

    /**
     * Parses a CSV file and returns preview data with validation results.
     *
     * @param csvStream input stream containing CSV data
     * @return CsvPreviewResponse with parsed questions and any validation errors
     * @throws IOException if unable to read the CSV file
     */
    public CsvPreviewResponse parseCsv(InputStream csvStream) throws IOException {
        List<KahootQuestion> questions = new ArrayList<>();
        List<ValidationError> validationErrors = new ArrayList<>();
        int totalRows = 0;
        int validRows = 0;

        try (Reader reader = new InputStreamReader(csvStream);
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> allRows = csvReader.readAll();

            if (allRows.isEmpty()) {
                validationErrors.add(new ValidationError(ErrorType.EMPTY_FIELD, null, null, "CSV file is empty"));
                return new CsvPreviewResponse(questions, validationErrors, Map.of(), 0, 0);
            }

            String[] headers = allRows.get(0);
            Map<String, Integer> columnMap = detectColumns(headers);
            Map<String, String> columnMapping = createColumnMapping(headers, columnMap);

            // Validate required columns
            List<ValidationError> columnErrors = validateRequiredColumns(columnMap);
            if (!columnErrors.isEmpty()) {
                validationErrors.addAll(columnErrors);
                return new CsvPreviewResponse(questions, validationErrors, columnMapping, 0, 0);
            }

            // Parse data rows
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                totalRows++;

                if (totalRows > MAX_QUESTIONS) {
                    validationErrors.add(new ValidationError(ErrorType.INVALID_ANSWER, i + 1, null,
                        String.format("Maximum %d questions allowed. Remaining rows will be ignored.", MAX_QUESTIONS)));
                    break;
                }

                List<ValidationError> rowErrors = validateRow(row, i + 1, columnMap);
                if (!rowErrors.isEmpty()) {
                    validationErrors.addAll(rowErrors);
                } else {
                    try {
                        KahootQuestion question = parseQuestion(row, columnMap);
                        questions.add(question);
                        validRows++;
                    } catch (Exception e) {
                        log.warn("Failed to parse row {}: {}", i + 1, e.getMessage());
                        validationErrors.add(new ValidationError(ErrorType.INVALID_ANSWER, i + 1, null,
                            "Failed to parse row: " + e.getMessage()));
                    }
                }
            }

            log.info("Parsed CSV: {} total rows, {} valid questions, {} errors",
                totalRows, validRows, validationErrors.size());

            return new CsvPreviewResponse(questions, validationErrors, columnMapping, totalRows, validRows);

        } catch (CsvException e) {
            throw new IOException("Failed to parse CSV file", e);
        }
    }

    /**
     * Detects column positions by matching header names against known patterns.
     *
     * @param headers array of header strings from CSV
     * @return map of column names to their positions
     */
    Map<String, Integer> detectColumns(String[] headers) {
        Map<String, Integer> columnMap = new HashMap<>();

        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim().toLowerCase();

            if (matchesPattern(header, QUESTION_PATTERNS)) {
                columnMap.put("question", i);
            } else if (matchesPattern(header, CORRECT_ANSWER_PATTERNS)) {
                columnMap.put("correctAnswer", i);
            } else if (matchesPattern(header, TIME_LIMIT_PATTERNS)) {
                columnMap.put("timeLimit", i);
            } else {
                for (int optionNum = 0; optionNum < 4; optionNum++) {
                    if (matchesPattern(header, OPTION_PATTERNS.get(optionNum))) {
                        columnMap.put("option" + (optionNum + 1), i);
                        break;
                    }
                }
            }
        }

        log.debug("Detected columns: {}", columnMap);
        return columnMap;
    }

    /**
     * Validates a single row of data.
     *
     * @param row array of cell values
     * @param rowNumber row number (1-indexed for user display)
     * @param columnMap map of column names to positions
     * @return list of validation errors (empty if valid)
     */
    List<ValidationError> validateRow(String[] row, int rowNumber, Map<String, Integer> columnMap) {
        List<ValidationError> errors = new ArrayList<>();

        // Check for empty question
        String question = getCellValue(row, columnMap.get("question"));
        if (question == null || question.trim().isEmpty()) {
            errors.add(new ValidationError(ErrorType.EMPTY_FIELD, rowNumber, "Question", "Question text is empty"));
        }

        // Check for empty options
        List<String> options = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            String option = getCellValue(row, columnMap.get("option" + i));
            if (option == null || option.trim().isEmpty()) {
                errors.add(new ValidationError(ErrorType.EMPTY_FIELD, rowNumber, "Option " + i, "Option " + i + " is empty"));
            } else {
                options.add(option.trim());
            }
        }

        // Validate correct answer matches one of the options
        String correctAnswer = getCellValue(row, columnMap.get("correctAnswer"));
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            errors.add(new ValidationError(ErrorType.EMPTY_FIELD, rowNumber, "Correct Answer", "Correct answer is empty"));
        } else if (options.size() == 4 && !options.contains(correctAnswer.trim())) {
            errors.add(new ValidationError(ErrorType.INVALID_ANSWER, rowNumber, "Correct Answer",
                "Correct answer must exactly match one of the four options (case-sensitive)"));
        }

        // Validate time limit if present
        Integer timeLimitCol = columnMap.get("timeLimit");
        if (timeLimitCol != null) {
            String timeLimitStr = getCellValue(row, timeLimitCol);
            if (timeLimitStr != null && !timeLimitStr.trim().isEmpty()) {
                try {
                    int timeLimit = Integer.parseInt(timeLimitStr.trim());
                    if (timeLimit < MIN_TIME_LIMIT || timeLimit > MAX_TIME_LIMIT) {
                        errors.add(new ValidationError(ErrorType.INVALID_TIME_LIMIT, rowNumber, "Time Limit",
                            String.format("Time limit must be between %d and %d seconds", MIN_TIME_LIMIT, MAX_TIME_LIMIT)));
                    }
                } catch (NumberFormatException e) {
                    errors.add(new ValidationError(ErrorType.INVALID_TIME_LIMIT, rowNumber, "Time Limit",
                        "Time limit must be a valid number"));
                }
            }
        }

        return errors;
    }

    /**
     * Parses a row into a KahootQuestion object.
     *
     * @param row array of cell values
     * @param columnMap map of column names to positions
     * @return KahootQuestion instance
     */
    KahootQuestion parseQuestion(String[] row, Map<String, Integer> columnMap) {
        String question = getCellValue(row, columnMap.get("question")).trim();
        String correctAnswer = getCellValue(row, columnMap.get("correctAnswer")).trim();

        // Get time limit or use default
        int timeLimit = 20; // default
        Integer timeLimitCol = columnMap.get("timeLimit");
        if (timeLimitCol != null) {
            String timeLimitStr = getCellValue(row, timeLimitCol);
            if (timeLimitStr != null && !timeLimitStr.trim().isEmpty()) {
                try {
                    timeLimit = Integer.parseInt(timeLimitStr.trim());
                } catch (NumberFormatException e) {
                    // Use default if parsing fails
                    log.warn("Invalid time limit value, using default: {}", timeLimitStr);
                }
            }
        }

        List<KahootQuestion.Choice> choices = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            String optionText = getCellValue(row, columnMap.get("option" + i)).trim();
            boolean isCorrect = optionText.equals(correctAnswer);
            choices.add(new KahootQuestion.Choice(optionText, isCorrect));
        }

        return new KahootQuestion(question, choices, timeLimit);
    }

    /**
     * Validates that all required columns are present.
     */
    private List<ValidationError> validateRequiredColumns(Map<String, Integer> columnMap) {
        List<ValidationError> errors = new ArrayList<>();

        if (!columnMap.containsKey("question")) {
            errors.add(new ValidationError(ErrorType.MISSING_COLUMN, null, "Question", "Question column not found"));
        }
        for (int i = 1; i <= 4; i++) {
            if (!columnMap.containsKey("option" + i)) {
                errors.add(new ValidationError(ErrorType.MISSING_COLUMN, null, "Option " + i, "Option " + i + " column not found"));
            }
        }
        if (!columnMap.containsKey("correctAnswer")) {
            errors.add(new ValidationError(ErrorType.MISSING_COLUMN, null, "Correct Answer", "Correct Answer column not found"));
        }

        return errors;
    }

    /**
     * Creates a mapping of detected columns to their original header names.
     */
    private Map<String, String> createColumnMapping(String[] headers, Map<String, Integer> columnMap) {
        Map<String, String> mapping = new HashMap<>();
        for (Map.Entry<String, Integer> entry : columnMap.entrySet()) {
            mapping.put(entry.getKey(), headers[entry.getValue()]);
        }
        return mapping;
    }

    /**
     * Safely gets a cell value from a row.
     */
    private String getCellValue(String[] row, Integer columnIndex) {
        if (columnIndex == null || columnIndex >= row.length) {
            return null;
        }
        return row[columnIndex];
    }

    /**
     * Checks if a header matches any of the given patterns.
     */
    private boolean matchesPattern(String header, List<String> patterns) {
        return patterns.stream().anyMatch(pattern -> header.equals(pattern.toLowerCase()));
    }
}
