package me.pacphi.kahoot.service;

import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ExcelService shuffling functionality.
 * Tests the public API method generateKahootTemplateWithOptions() with various
 * shuffle combinations and parameters.
 */
@SpringBootTest
@ActiveProfiles("test")
class ExcelServiceShuffleTest {

  @Autowired
  private ExcelService excelService;

  private List<KahootQuestion> testQuestions;

  @BeforeEach
  void setUp() {
    testQuestions = createTestQuestions();
  }

  /**
   * Tests that when shuffleQuestions is enabled, the Excel file is generated successfully
   * and questions may appear in different order. Since shuffling is random, we verify
   * that the file is valid and contains all questions.
   */
  @Test
  void testGenerateWithShuffleQuestions_True() throws IOException {
    // When: Generate template with question shuffling enabled
    Resource resource = excelService.generateKahootTemplateWithOptions(
      testQuestions,
      true,  // shuffleQuestions
      false, // shuffleAnswers
      20     // defaultTimeLimit
    );

    // Then: File is generated successfully
    assertThat(resource).isNotNull();
    assertThat(resource.exists()).isTrue();

    // Read the resource once and verify all data
    try (InputStream is = resource.getInputStream();
         Workbook workbook = WorkbookFactory.create(is)) {

      Sheet sheet = workbook.getSheetAt(0);

      // Extract all questions
      List<String> generatedQuestions = new ArrayList<>();
      Map<String, String> questionToCorrectAnswer = new HashMap<>();

      int rowNum = 8; // STARTING_ROW
      Row row;
      while ((row = sheet.getRow(rowNum)) != null) {
        String question = getCellValueAsString(row.getCell(1));
        if (question.isEmpty()) {
          break;
        }
        generatedQuestions.add(question);

        // Get correct answer
        int correctAnswerIndex = (int) row.getCell(7).getNumericCellValue();
        String correctAnswer = getCellValueAsString(row.getCell(1 + correctAnswerIndex));
        questionToCorrectAnswer.put(question, correctAnswer);

        rowNum++;
      }

      // And: All questions are present (order may vary)
      assertThat(generatedQuestions)
        .hasSize(testQuestions.size())
        .containsExactlyInAnyOrderElementsOf(
          testQuestions.stream()
            .map(KahootQuestion::question)
            .collect(Collectors.toList())
        );

      // And: All correct answers are preserved
      assertThat(questionToCorrectAnswer).hasSize(testQuestions.size());

      // Verify each question has its correct answer preserved
      testQuestions.forEach(q -> {
        String correctAnswer = q.choices().stream()
          .filter(KahootQuestion.Choice::isCorrect)
          .map(KahootQuestion.Choice::answerText)
          .findFirst()
          .orElseThrow();
        assertThat(questionToCorrectAnswer).containsEntry(q.question(), correctAnswer);
      });
    }
  }

  /**
   * Tests that when shuffleQuestions is disabled, questions maintain their original order.
   */
  @Test
  void testGenerateWithShuffleQuestions_False() throws IOException {
    // When: Generate template without question shuffling
    Resource resource = excelService.generateKahootTemplateWithOptions(
      testQuestions,
      false, // shuffleQuestions
      false, // shuffleAnswers
      20     // defaultTimeLimit
    );

    // Then: Questions appear in original order
    List<String> generatedQuestions = extractQuestions(resource);
    List<String> expectedQuestions = testQuestions.stream()
      .map(KahootQuestion::question)
      .collect(Collectors.toList());

    assertThat(generatedQuestions).containsExactlyElementsOf(expectedQuestions);
  }

  /**
   * Tests that when shuffleAnswers is enabled, all answers are present and
   * exactly one remains marked as correct.
   */
  @Test
  void testGenerateWithShuffleAnswers_True() throws IOException {
    // When: Generate template with answer shuffling enabled
    Resource resource = excelService.generateKahootTemplateWithOptions(
      testQuestions,
      false, // shuffleQuestions
      true,  // shuffleAnswers
      20     // defaultTimeLimit
    );

    // Then: All answers are present for each question
    try (InputStream is = resource.getInputStream();
         Workbook workbook = WorkbookFactory.create(is)) {

      Sheet sheet = workbook.getSheetAt(0);

      for (int i = 0; i < testQuestions.size(); i++) {
        KahootQuestion originalQuestion = testQuestions.get(i);
        Row row = sheet.getRow(8 + i); // STARTING_ROW = 8

        // Extract all answers from the row (columns 2-5)
        Set<String> generatedAnswers = IntStream.range(2, 6)
          .mapToObj(col -> getCellValueAsString(row.getCell(col)))
          .filter(s -> !s.isEmpty())
          .collect(Collectors.toSet());

        Set<String> originalAnswers = originalQuestion.choices().stream()
          .map(KahootQuestion.Choice::answerText)
          .collect(Collectors.toSet());

        // All answers should be present (order may vary)
        assertThat(generatedAnswers).containsExactlyInAnyOrderElementsOf(originalAnswers);

        // Exactly one answer should be marked as correct (column 7)
        int correctAnswerIndex = (int) row.getCell(7).getNumericCellValue();
        assertThat(correctAnswerIndex).isBetween(1, 4);

        // The correct answer should match one of the original correct answers
        String correctAnswer = getCellValueAsString(row.getCell(1 + correctAnswerIndex));
        String originalCorrectAnswer = originalQuestion.choices().stream()
          .filter(KahootQuestion.Choice::isCorrect)
          .map(KahootQuestion.Choice::answerText)
          .findFirst()
          .orElseThrow();

        assertThat(correctAnswer).isEqualTo(originalCorrectAnswer);
      }
    }
  }

  /**
   * Tests that when shuffleAnswers is disabled, answers maintain their original order.
   */
  @Test
  void testGenerateWithShuffleAnswers_False() throws IOException {
    // When: Generate template without answer shuffling
    Resource resource = excelService.generateKahootTemplateWithOptions(
      testQuestions,
      false, // shuffleQuestions
      false, // shuffleAnswers
      20     // defaultTimeLimit
    );

    // Then: Answers appear in original order
    try (InputStream is = resource.getInputStream();
         Workbook workbook = WorkbookFactory.create(is)) {

      Sheet sheet = workbook.getSheetAt(0);

      for (int i = 0; i < testQuestions.size(); i++) {
        KahootQuestion originalQuestion = testQuestions.get(i);
        Row row = sheet.getRow(8 + i);

        // Verify answers are in original order
        for (int j = 0; j < originalQuestion.choices().size(); j++) {
          String expectedAnswer = originalQuestion.choices().get(j).answerText();
          String actualAnswer = getCellValueAsString(row.getCell(2 + j));
          assertThat(actualAnswer).isEqualTo(expectedAnswer);
        }
      }
    }
  }

  /**
   * Tests that both shuffle options can be enabled simultaneously and
   * the template is generated successfully.
   */
  @Test
  void testGenerateWithBothShuffles() throws IOException {
    // When: Generate template with both shuffles enabled
    Resource resource = excelService.generateKahootTemplateWithOptions(
      testQuestions,
      true, // shuffleQuestions
      true, // shuffleAnswers
      20    // defaultTimeLimit
    );

    // Then: File is generated successfully
    assertThat(resource).isNotNull();
    assertThat(resource.exists()).isTrue();

    // And: Each question has exactly 4 answers with one correct
    try (InputStream is = resource.getInputStream();
         Workbook workbook = WorkbookFactory.create(is)) {

      Sheet sheet = workbook.getSheetAt(0);

      // Extract all questions and verify count
      List<String> generatedQuestions = new ArrayList<>();
      int rowNum = 8;
      Row row;
      while ((row = sheet.getRow(rowNum)) != null) {
        String question = getCellValueAsString(row.getCell(1));
        if (question.isEmpty()) {
          break;
        }
        generatedQuestions.add(question);

        // Count non-empty answers (make row effectively final for lambda)
        final Row currentRow = row;
        long answerCount = IntStream.range(2, 6)
          .mapToObj(col -> getCellValueAsString(currentRow.getCell(col)))
          .filter(s -> !s.isEmpty())
          .count();

        assertThat(answerCount).isEqualTo(4);

        // Verify correct answer index is valid
        int correctAnswerIndex = (int) row.getCell(7).getNumericCellValue();
        assertThat(correctAnswerIndex).isBetween(1, 4);

        rowNum++;
      }

      // Verify question count
      assertThat(generatedQuestions).hasSize(testQuestions.size());
    }
  }

  /**
   * Tests that a custom time limit is correctly applied to all questions.
   */
  @Test
  void testGenerateWithCustomTimeLimit() throws IOException {
    // Given: Custom time limit of 30 seconds
    int customTimeLimit = 30;

    // When: Generate template with custom time limit
    Resource resource = excelService.generateKahootTemplateWithOptions(
      testQuestions,
      false, // shuffleQuestions
      false, // shuffleAnswers
      customTimeLimit
    );

    // Then: All questions have the custom time limit
    try (InputStream is = resource.getInputStream();
         Workbook workbook = WorkbookFactory.create(is)) {

      Sheet sheet = workbook.getSheetAt(0);

      for (int i = 0; i < testQuestions.size(); i++) {
        Row row = sheet.getRow(8 + i);
        int actualTimeLimit = (int) row.getCell(6).getNumericCellValue();
        assertThat(actualTimeLimit).isEqualTo(customTimeLimit);
      }
    }
  }

  /**
   * Tests that when time limit is 0, the default time limit (20 seconds) is used.
   */
  @Test
  void testGenerateWithDefaultTimeLimit() throws IOException {
    // Given: Time limit of 0 (should use default)
    int zeroTimeLimit = 0;
    int expectedDefaultTimeLimit = 20;

    // When: Generate template with zero time limit
    Resource resource = excelService.generateKahootTemplateWithOptions(
      testQuestions,
      false, // shuffleQuestions
      false, // shuffleAnswers
      zeroTimeLimit
    );

    // Then: All questions have the default time limit
    try (InputStream is = resource.getInputStream();
         Workbook workbook = WorkbookFactory.create(is)) {

      Sheet sheet = workbook.getSheetAt(0);

      for (int i = 0; i < testQuestions.size(); i++) {
        Row row = sheet.getRow(8 + i);
        int actualTimeLimit = (int) row.getCell(6).getNumericCellValue();
        assertThat(actualTimeLimit).isEqualTo(expectedDefaultTimeLimit);
      }
    }
  }

  /**
   * Tests that after shuffling answers, exactly one choice remains marked as correct
   * and it's the same answer that was correct before shuffling.
   */
  @Test
  void testShufflePreservesCorrectAnswer() throws IOException {
    // When: Generate template with answer shuffling
    Resource resource = excelService.generateKahootTemplateWithOptions(
      testQuestions,
      false, // shuffleQuestions
      true,  // shuffleAnswers
      20     // defaultTimeLimit
    );

    // Then: Each question has exactly one correct answer preserved
    try (InputStream is = resource.getInputStream();
         Workbook workbook = WorkbookFactory.create(is)) {

      Sheet sheet = workbook.getSheetAt(0);

      for (int i = 0; i < testQuestions.size(); i++) {
        KahootQuestion originalQuestion = testQuestions.get(i);
        Row row = sheet.getRow(8 + i);

        // Get the correct answer index from the Excel file
        int correctAnswerIndex = (int) row.getCell(7).getNumericCellValue();
        assertThat(correctAnswerIndex).isBetween(1, 4);

        // Get the actual correct answer text from the shuffled row
        String actualCorrectAnswer = getCellValueAsString(row.getCell(1 + correctAnswerIndex));

        // Get the expected correct answer from the original question
        String expectedCorrectAnswer = originalQuestion.choices().stream()
          .filter(KahootQuestion.Choice::isCorrect)
          .map(KahootQuestion.Choice::answerText)
          .findFirst()
          .orElseThrow();

        // The correct answer text should match
        assertThat(actualCorrectAnswer).isEqualTo(expectedCorrectAnswer);
      }
    }
  }

  /**
   * Tests that after shuffling, all answer texts are preserved (no answers lost or duplicated).
   */
  @Test
  void testShufflePreservesAllAnswers() throws IOException {
    // When: Generate template with answer shuffling
    Resource resource = excelService.generateKahootTemplateWithOptions(
      testQuestions,
      false, // shuffleQuestions
      true,  // shuffleAnswers
      20     // defaultTimeLimit
    );

    // Then: All original answers are present in the shuffled output
    try (InputStream is = resource.getInputStream();
         Workbook workbook = WorkbookFactory.create(is)) {

      Sheet sheet = workbook.getSheetAt(0);

      for (int i = 0; i < testQuestions.size(); i++) {
        KahootQuestion originalQuestion = testQuestions.get(i);
        Row row = sheet.getRow(8 + i);

        // Collect all answer texts from the Excel row
        Set<String> actualAnswers = IntStream.range(2, 6)
          .mapToObj(col -> getCellValueAsString(row.getCell(col)))
          .filter(s -> !s.isEmpty())
          .collect(Collectors.toSet());

        // Collect all answer texts from the original question
        Set<String> expectedAnswers = originalQuestion.choices().stream()
          .map(KahootQuestion.Choice::answerText)
          .collect(Collectors.toSet());

        // All answers should be preserved (order doesn't matter)
        assertThat(actualAnswers)
          .hasSize(4)
          .containsExactlyInAnyOrderElementsOf(expectedAnswers);
      }
    }
  }

  /**
   * Tests that multiple consecutive shuffles produce different results (statistical test).
   * This verifies that shuffling is actually random and not deterministic.
   */
  @Test
  void testMultipleShufflesProduceDifferentResults() throws IOException {
    // Given: A question set with predictable order
    List<KahootQuestion> orderedQuestions = List.of(
      createQuestion("Question 1", "A1", "B1", "C1", "D1", 0),
      createQuestion("Question 2", "A2", "B2", "C2", "D2", 0),
      createQuestion("Question 3", "A3", "B3", "C3", "D3", 0),
      createQuestion("Question 4", "A4", "B4", "C4", "D4", 0)
    );

    // When: Generate template 10 times with question shuffling
    Set<String> observedOrders = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      Resource resource = excelService.generateKahootTemplateWithOptions(
        orderedQuestions,
        true,  // shuffleQuestions
        false, // shuffleAnswers
        20
      );

      List<String> questionOrder = extractQuestions(resource);
      observedOrders.add(String.join(",", questionOrder));
    }

    // Then: We should observe at least 2 different orderings
    // (with 10 trials of 4 items, probability of all same order is ~0.000001%)
    assertThat(observedOrders.size()).isGreaterThan(1);
  }

  // ==================== Helper Methods ====================

  /**
   * Creates a list of test questions for testing.
   */
  private List<KahootQuestion> createTestQuestions() {
    return List.of(
      createQuestion("What is 2+2?", "3", "4", "5", "6", 1),
      createQuestion("What is the capital of France?", "London", "Berlin", "Paris", "Madrid", 2),
      createQuestion("Which planet is closest to the sun?", "Venus", "Mercury", "Mars", "Earth", 1)
    );
  }

  /**
   * Helper to create a KahootQuestion with specified answers.
   *
   * @param questionText The question text
   * @param ans1 First answer
   * @param ans2 Second answer
   * @param ans3 Third answer
   * @param ans4 Fourth answer
   * @param correctIndex Index of correct answer (0-3)
   * @return Constructed KahootQuestion
   */
  private KahootQuestion createQuestion(
      String questionText,
      String ans1,
      String ans2,
      String ans3,
      String ans4,
      int correctIndex
  ) {
    List<KahootQuestion.Choice> choices = List.of(
      new KahootQuestion.Choice(ans1, correctIndex == 0),
      new KahootQuestion.Choice(ans2, correctIndex == 1),
      new KahootQuestion.Choice(ans3, correctIndex == 2),
      new KahootQuestion.Choice(ans4, correctIndex == 3)
    );
    return new KahootQuestion(questionText, choices, 20);
  }

  /**
   * Extracts all question texts from the generated Excel file.
   */
  private List<String> extractQuestions(Resource resource) throws IOException {
    try (InputStream is = resource.getInputStream();
         Workbook workbook = WorkbookFactory.create(is)) {

      Sheet sheet = workbook.getSheetAt(0);
      List<String> questions = new ArrayList<>();

      int rowNum = 8; // STARTING_ROW
      Row row;
      while ((row = sheet.getRow(rowNum)) != null) {
        String question = getCellValueAsString(row.getCell(1));
        if (question.isEmpty()) {
          break;
        }
        questions.add(question);
        rowNum++;
      }

      return questions;
    }
  }

  /**
   * Extracts questions with their correct answers from the Excel file.
   */
  private Map<String, String> extractQuestionsWithCorrectAnswers(Resource resource) throws IOException {
    try (InputStream is = resource.getInputStream();
         Workbook workbook = WorkbookFactory.create(is)) {

      Sheet sheet = workbook.getSheetAt(0);
      Map<String, String> result = new HashMap<>();

      int rowNum = 8; // STARTING_ROW
      Row row;
      while ((row = sheet.getRow(rowNum)) != null) {
        String question = getCellValueAsString(row.getCell(1));
        if (question.isEmpty()) {
          break;
        }

        int correctAnswerIndex = (int) row.getCell(7).getNumericCellValue();
        String correctAnswer = getCellValueAsString(row.getCell(1 + correctAnswerIndex));

        result.put(question, correctAnswer);
        rowNum++;
      }

      return result;
    }
  }

  /**
   * Safely extracts cell value as string, handling null cells.
   */
  private String getCellValueAsString(Cell cell) {
    if (cell == null) {
      return "";
    }

    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
      case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
      case BLANK -> "";
      default -> "";
    };
  }
}
