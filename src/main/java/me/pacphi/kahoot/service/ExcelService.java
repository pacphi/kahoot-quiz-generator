package me.pacphi.kahoot.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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
    private static final int DEFAULT_TIME_LIMIT = 15;

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
        try (InputStream is = Files.newInputStream(filePath);
                Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = STARTING_ROW;

            for (KahootQuestion question : questions) {
                updateQuestionRow(sheet, rowNum++, question);
            }

            try (OutputStream os = Files.newOutputStream(filePath)) {
                workbook.write(os);
            }
            log.info("Successfully populated {} questions", questions.size());
        }
    }

    private void updateQuestionRow(Sheet sheet, int rowNum, KahootQuestion question) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }

        row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(question.question());
        row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(DEFAULT_TIME_LIMIT);

        for (int i = 0; i < question.choices().size(); i++) {
            KahootQuestion.Choice choice = question.choices().get(i);
            row.getCell(2 + i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(choice.answerText());
            if (choice.isCorrect()) {
                row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(2 + i);
            }
        }
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