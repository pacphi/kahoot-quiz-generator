package me.pacphi.kahoot.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class ExcelService {

    private static final Logger log = LoggerFactory.getLogger(ExcelService.class);
    private static final String TEMPLATE_PATH = "classpath:/templates/Kahoot-Quiz-Spreadsheet-Template.xlsx";
    private static final int STARTING_ROW = 8;
    private static final int DEFAULT_TIME_LIMIT = 20;

    private final ResourceLoader resourceLoader;

    public ExcelService(ResourceLoader resourceLoader) {
        this.resourceLoader = Objects.requireNonNull(resourceLoader, "ResourceLoader must not be null");
    }

    public Resource generateKahootTemplate(List<KahootQuestion> questions) throws IOException {
        validateQuestions(questions);

        Path tempFile = createTemporaryFile();
        log.info("Created temporary file: {}", tempFile);

        try {
            copyTemplate(tempFile);
            populateQuestions(questions, tempFile);
            return new InputStreamResource(Files.newInputStream(tempFile));
        } catch (Exception e) {
            cleanupFile(tempFile);
            throw new IOException("Failed to generate Kahoot template", e);
        }
    }

    /**
     * Generates a Kahoot template with optional question and answer shuffling.
     *
     * @param questions        List of questions to include
     * @param shuffleQuestions Whether to randomize question order
     * @param shuffleAnswers   Whether to randomize answer positions
     * @param defaultTimeLimit Time limit in seconds (uses DEFAULT_TIME_LIMIT if 0)
     * @return Resource containing the generated Excel file
     * @throws IOException if file operations fail
     */
    public Resource generateKahootTemplateWithOptions(
            List<KahootQuestion> questions,
            boolean shuffleQuestions,
            boolean shuffleAnswers,
            int defaultTimeLimit) throws IOException {
        validateQuestions(questions);

        List<KahootQuestion> processedQuestions = questions;

        if (shuffleQuestions) {
            processedQuestions = shuffleQuestions(processedQuestions);
        }

        if (shuffleAnswers) {
            processedQuestions = processedQuestions.stream()
                    .map(this::shuffleAnswerPositions)
                    .collect(Collectors.toList());
        }

        int timeLimit = defaultTimeLimit > 0 ? defaultTimeLimit : DEFAULT_TIME_LIMIT;
        log.info("Generating template with {} questions, timeLimit={}s, shuffleQuestions={}, shuffleAnswers={}",
                processedQuestions.size(), timeLimit, shuffleQuestions, shuffleAnswers);

        Path tempFile = createTemporaryFile();
        log.info("Created temporary file: {}", tempFile);

        try {
            copyTemplate(tempFile);
            populateQuestionsWithTimeLimit(processedQuestions, tempFile, timeLimit);
            return new InputStreamResource(Files.newInputStream(tempFile));
        } catch (Exception e) {
            cleanupFile(tempFile);
            throw new IOException("Failed to generate Kahoot template with options", e);
        }
    }

    private void validateQuestions(List<KahootQuestion> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Questions list cannot be null or empty");
        }

        questions.forEach(question -> {
            if (question.choices() == null || question.choices().isEmpty()) {
                throw new IllegalArgumentException("Question must have choices: " + question.question());
            }
            if (question.choices().stream().noneMatch(KahootQuestion.Choice::isCorrect)) {
                throw new IllegalArgumentException(
                        "Question must have at least one correct answer: " + question.question());
            }
        });
    }

    private Path createTemporaryFile() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss"));
        return Files.createTempFile("kahoot-quiz-" + timestamp + "-", ".xlsx");
    }

    private void copyTemplate(Path targetPath) throws IOException {
        Resource template = resourceLoader.getResource(TEMPLATE_PATH);
        if (!template.exists()) {
            throw new FileNotFoundException("Template file not found: " + TEMPLATE_PATH);
        }

        try (InputStream is = template.getInputStream();
                OutputStream os = Files.newOutputStream(targetPath)) {
            StreamUtils.copy(is, os);
        }
        log.debug("Template copied to: {}", targetPath);
    }

    private void populateQuestions(List<KahootQuestion> questions, Path filePath) throws IOException {
        populateQuestionsWithTimeLimit(questions, filePath, DEFAULT_TIME_LIMIT);
    }

    private void populateQuestionsWithTimeLimit(List<KahootQuestion> questions, Path filePath, int timeLimit)
            throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
                Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = STARTING_ROW;

            for (KahootQuestion question : questions) {
                updateQuestionRow(sheet, rowNum++, question, timeLimit);
            }

            try (OutputStream os = Files.newOutputStream(filePath)) {
                workbook.write(os);
            }
            log.info("Successfully populated {} questions with {}s time limit", questions.size(), timeLimit);
        }
    }

    private void updateQuestionRow(Sheet sheet, int rowNum, KahootQuestion question) {
        updateQuestionRow(sheet, rowNum, question, DEFAULT_TIME_LIMIT);
    }

    private void updateQuestionRow(Sheet sheet, int rowNum, KahootQuestion question, int timeLimit) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }

        row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(question.question());
        row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(timeLimit);

        for (int i = 0; i < question.choices().size(); i++) {
            KahootQuestion.Choice choice = question.choices().get(i);
            row.getCell(2 + i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(choice.answerText());
            if (choice.isCorrect()) {
                row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(1 + i);
            }
        }
    }

    /**
     * Shuffles the order of questions randomly.
     *
     * @param questions Original list of questions
     * @return New list with questions in random order
     */
    private List<KahootQuestion> shuffleQuestions(List<KahootQuestion> questions) {
        if (questions == null || questions.isEmpty()) {
            log.warn("Attempted to shuffle null or empty questions list");
            return questions;
        }

        List<KahootQuestion> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled, new Random());
        log.info("Shuffled {} questions", shuffled.size());
        return shuffled;
    }

    /**
     * Shuffles answer positions while maintaining correct answer mapping.
     *
     * @param question Original question with answers
     * @return New question with shuffled answer positions
     */
    private KahootQuestion shuffleAnswerPositions(KahootQuestion question) {
        if (question == null || question.choices() == null || question.choices().isEmpty()) {
            log.warn("Attempted to shuffle answers for invalid question");
            return question;
        }

        List<Integer> indices = IntStream.range(0, question.choices().size())
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(indices, new Random());

        List<KahootQuestion.Choice> shuffledChoices = indices.stream()
                .map(i -> question.choices().get(i))
                .collect(Collectors.toList());

        log.debug("Shuffled answer positions for question: {}", question.question());
        return new KahootQuestion(question.question(), shuffledChoices, question.timeLimit());
    }

    private void cleanupFile(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
            log.debug("Cleaned up temporary file: {}", filePath);
        } catch (IOException e) {
            log.warn("Failed to cleanup temporary file: {}", filePath, e);
        }
    }

}