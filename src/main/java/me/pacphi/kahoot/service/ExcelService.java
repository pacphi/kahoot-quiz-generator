package me.pacphi.kahoot.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class ExcelService {

    private final ResourceLoader resourceLoader;

    public ExcelService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Resource generateKahootTemplate(List<KahootQuestion> questions) throws IOException {
        // Read in the Kahoot Quiz template from the classpath
        Resource template = resourceLoader.getResource("classpath:/templates/Kahoot-Quiz-Spreadsheet-Template.xlsx");
        // Write a time-stamped copy of the template to the /tmp directory
        String tmpDir = System.getProperty("java.io.tmpdir");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.ss"));
        String quizFilePath = String.format("%s/kahoot-quiz.%s.xlsx", tmpDir, timestamp);
        File targetFile = new File(quizFilePath);
        FileOutputStream outputStream = new FileOutputStream(targetFile);
        StreamUtils.copy(template.getInputStream(), outputStream);
        outputStream.close();
        // Read in the time-stamped quiz file; this is where we'll add questions and answers
        Resource quizQuestions = resourceLoader.getResource(quizFilePath);
        try (Workbook workbook = WorkbookFactory.create(quizQuestions.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 9; // Start after header

            for (KahootQuestion question : questions) {
                Row row = sheet.createRow(rowNum++);

                // Question
                row.createCell(0).setCellValue(question.question());

                // Time limit (default 15 seconds)
                row.createCell(5).setCellValue(15);

                // Answer options
                for (int i = 0; i < question.choices().size(); i++) {
                    KahootQuestion.Choice choice = question.choices().get(i);
                    row.createCell(1 + i).setCellValue(choice.answerText());
                    if (choice.isCorrect()) {
                        row.createCell(6).setCellValue(i + 1); // Correct answer (1-based)
                    }
                }
            }
            // Write quiz file's question-and-answer additions to file-system
            ByteArrayOutputStream quizFileOutputStream = new ByteArrayOutputStream();
            workbook.write(quizFileOutputStream);
            quizFileOutputStream.close();

            // Stream the contents of the file
            return new InputStreamResource(new FileInputStream(targetFile));
        }
    }
}
