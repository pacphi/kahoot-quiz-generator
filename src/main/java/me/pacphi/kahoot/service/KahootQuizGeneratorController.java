package me.pacphi.kahoot.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import me.pacphi.kahoot.model.CsvConversionRequest;
import me.pacphi.kahoot.model.CsvPreviewResponse;
import me.pacphi.kahoot.model.ValidationError;

@RestController
public class KahootQuizGeneratorController {

    private static final Logger log = LoggerFactory.getLogger(KahootQuizGeneratorController.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final KahootService kahootService;
    private final ExcelService excelService;
    private final CsvParserService csvParserService;

    public KahootQuizGeneratorController(
            KahootService kahootService,
            ExcelService excelService,
            CsvParserService csvParserService) {
        this.kahootService = kahootService;
        this.excelService = excelService;
        this.csvParserService = csvParserService;
    }

    @PostMapping("/api/quiz/generate")
    public ResponseEntity<Resource> generateQuiz(
        @RequestParam("topic") String topic,
        @RequestParam("numQuestions") int numQuestions
    ) throws IOException {
        log.info("Generating quiz for topic: {}, questions: {}", topic, numQuestions);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"kahoot-quiz.xlsx\"")
                .body(
                    excelService.generateKahootTemplate(
                        kahootService.generateQuestions(topic, numQuestions)
                    )
                );
    }

    /**
     * Upload and preview CSV file with validation
     *
     * @param file CSV file to upload (max 5MB)
     * @return CsvPreviewResponse with parsed questions and validation errors
     * @throws IOException if file reading fails
     */
    @PostMapping(value = "/api/quiz/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvPreviewResponse> uploadCsv(
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("Received CSV upload request: filename={}, size={} bytes",
                file.getOriginalFilename(), file.getSize());

        try {
            // Validate file size
            if (file.getSize() > MAX_FILE_SIZE) {
                log.warn("File size {} exceeds maximum {}", file.getSize(), MAX_FILE_SIZE);
                return ResponseEntity
                        .status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body(new CsvPreviewResponse(
                                List.of(),
                                List.of(new ValidationError(
                                        ValidationError.ErrorType.EMPTY_FIELD,
                                        null,
                                        null,
                                        String.format("File size exceeds maximum allowed size of %d MB",
                                                MAX_FILE_SIZE / 1024 / 1024))),
                                Map.of(),
                                0,
                                0));
            }

            // Validate file type
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
                log.warn("Invalid file type: {}", filename);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new CsvPreviewResponse(
                                List.of(),
                                List.of(new ValidationError(
                                        ValidationError.ErrorType.EMPTY_FIELD,
                                        null,
                                        null,
                                        "File must be a CSV file with .csv extension")),
                                Map.of(),
                                0,
                                0));
            }

            // Parse CSV file
            CsvPreviewResponse response = csvParserService.parseCsv(file.getInputStream());
            log.info("CSV parsed successfully: {} questions, {} errors",
                    response.validRows(), response.validationErrors().size());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid CSV file: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CsvPreviewResponse(
                            List.of(),
                            List.of(new ValidationError(
                                    ValidationError.ErrorType.EMPTY_FIELD,
                                    null,
                                    null,
                                    e.getMessage())),
                            Map.of(),
                            0,
                            0));
        } catch (IOException e) {
            log.error("Error processing CSV file", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CsvPreviewResponse(
                            List.of(),
                            List.of(new ValidationError(
                                    ValidationError.ErrorType.EMPTY_FIELD,
                                    null,
                                    null,
                                    "Error processing file: " + e.getMessage())),
                            Map.of(),
                            0,
                            0));
        }
    }

    /**
     * Convert CSV data to Kahoot Excel template
     *
     * @param request conversion request with questions and options
     * @return downloadable Excel file
     * @throws IOException if Excel generation fails
     */
    @PostMapping(value = "/api/quiz/convert", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> convertCsvToKahoot(
            @RequestBody CsvConversionRequest request) throws IOException {

        log.info("Converting {} CSV questions to Kahoot format", request.questions().size());

        try {
            // Generate Excel file using the questions from the request
            // The questions are already in KahootQuestion format
            Resource excelFile = excelService.generateKahootTemplateWithOptions(
                    request.questions(),
                    request.shuffleQuestions(),
                    request.shuffleAnswers(),
                    request.defaultTimeLimit()
            );

            log.info("Successfully converted CSV to Kahoot template");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"kahoot-quiz-from-csv.xlsx\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelFile);

        } catch (IllegalArgumentException e) {
            log.error("Validation error during conversion: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        } catch (IOException e) {
            log.error("Error generating Excel file", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
