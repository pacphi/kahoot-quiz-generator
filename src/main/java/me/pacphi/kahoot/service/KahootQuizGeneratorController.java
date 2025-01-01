package me.pacphi.kahoot.service;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KahootQuizGeneratorController {

    private final KahootService kahootService;
    private final ExcelService excelService;

    public KahootQuizGeneratorController(KahootService kahootService, ExcelService excelService) {
        this.kahootService = kahootService;
        this.excelService = excelService;
    }

    @PostMapping("/api/quiz/generate")
    public ResponseEntity<Resource> generateQuiz(
        @RequestParam("topic") String topic,
        @RequestParam("numQuestions") int numQuestions
    ) throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"kahoot-quiz.xlsx\"")
                .body(
                    excelService.generateKahootTemplate(
                        kahootService.generateQuestions(topic, numQuestions)
                    )
                );
    }
}
