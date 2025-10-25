package me.pacphi.kahoot.service;

import me.pacphi.kahoot.model.CsvPreviewResponse;
import me.pacphi.kahoot.model.ValidationError;
import me.pacphi.kahoot.model.ValidationError.ErrorType;
import me.pacphi.kahoot.service.KahootQuestion.Choice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for CsvParserService.
 * Tests CSV parsing, column detection, validation, and error handling.
 */
@DisplayName("CsvParserService Tests")
class CsvParserServiceTest {

  private CsvParserService csvParserService;

  // Test CSV data constants
  private static final String VALID_CSV = """
      Question,Option 1,Option 2,Option 3,Option 4,Correct Answer,Time Limit
      What is 2+2?,3,4,5,6,4,20
      What color is the sky?,Red,Blue,Green,Yellow,Blue,30
      """;

  private static final String CASE_INSENSITIVE_CSV = """
      QUESTION,OPTION 1,option 2,Option 3,OPTION 4,Correct Answer
      What is 2+2?,3,4,5,6,4
      """;

  private static final String ALTERNATIVE_COLUMN_NAMES_CSV = """
      q,a,choice 2,option 3,answer 4,correct
      What is 2+2?,3,4,5,6,4
      """;

  private static final String MISSING_QUESTION_COLUMN_CSV = """
      Option 1,Option 2,Option 3,Option 4,Correct Answer
      3,4,5,6,4
      """;

  private static final String EMPTY_QUESTION_CSV = """
      Question,Option 1,Option 2,Option 3,Option 4,Correct Answer
      ,3,4,5,6,4
      """;

  private static final String CORRECT_ANSWER_MISMATCH_CSV = """
      Question,Option 1,Option 2,Option 3,Option 4,Correct Answer
      What is 2+2?,3,4,5,6,7
      """;

  private static final String VALID_TIME_LIMIT_CSV = """
      Question,Option 1,Option 2,Option 3,Option 4,Correct Answer,Time Limit
      What is 2+2?,3,4,5,6,4,20
      """;

  private static final String INVALID_TIME_LIMIT_CSV = """
      Question,Option 1,Option 2,Option 3,Option 4,Correct Answer,Time Limit
      What is 2+2?,3,4,5,6,4,300
      """;

  private static final String EMPTY_CSV = "";

  private static final String HEADER_ONLY_CSV = """
      Question,Option 1,Option 2,Option 3,Option 4,Correct Answer
      """;

  private static final String OPTIONAL_TIME_LIMIT_CSV = """
      Question,Option 1,Option 2,Option 3,Option 4,Correct Answer
      What is 2+2?,3,4,5,6,4
      What is 3+3?,5,6,7,8,6
      """;

  @BeforeEach
  void setUp() {
    csvParserService = new CsvParserService();
  }

  // ==================== parseCsv Tests ====================

  @Test
  @DisplayName("Should parse valid CSV with standard column names and return questions")
  void testParseCsv_ValidCsv_ReturnsQuestions() throws IOException {
    // Arrange
    InputStream csvStream = createInputStream(VALID_CSV);

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).hasSize(2);
    assertThat(response.validationErrors()).isEmpty();
    assertThat(response.totalRows()).isEqualTo(2);
    assertThat(response.validRows()).isEqualTo(2);

    // Verify first question
    KahootQuestion firstQuestion = response.questions().get(0);
    assertThat(firstQuestion.question()).isEqualTo("What is 2+2?");
    assertThat(firstQuestion.choices()).hasSize(4);
    assertThat(firstQuestion.timeLimit()).isEqualTo(20);
    assertThat(firstQuestion.choices().get(1).answerText()).isEqualTo("4");
    assertThat(firstQuestion.choices().get(1).isCorrect()).isTrue();

    // Verify second question
    KahootQuestion secondQuestion = response.questions().get(1);
    assertThat(secondQuestion.question()).isEqualTo("What color is the sky?");
    assertThat(secondQuestion.timeLimit()).isEqualTo(30);
    assertThat(secondQuestion.choices().get(1).answerText()).isEqualTo("Blue");
    assertThat(secondQuestion.choices().get(1).isCorrect()).isTrue();
  }

  @Test
  @DisplayName("Should parse CSV with case-insensitive column names")
  void testParseCsv_CaseInsensitiveColumns() throws IOException {
    // Arrange
    InputStream csvStream = createInputStream(CASE_INSENSITIVE_CSV);

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).hasSize(1);
    assertThat(response.validationErrors()).isEmpty();
    assertThat(response.validRows()).isEqualTo(1);

    KahootQuestion question = response.questions().get(0);
    assertThat(question.question()).isEqualTo("What is 2+2?");
    assertThat(question.choices()).hasSize(4);
    assertThat(question.choices().get(1).answerText()).isEqualTo("4");
    assertThat(question.choices().get(1).isCorrect()).isTrue();
  }

  @Test
  @DisplayName("Should parse CSV with alternative column names")
  void testParseCsv_AlternativeColumnNames() throws IOException {
    // Arrange
    InputStream csvStream = createInputStream(ALTERNATIVE_COLUMN_NAMES_CSV);

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).hasSize(1);
    assertThat(response.validationErrors()).isEmpty();

    KahootQuestion question = response.questions().get(0);
    assertThat(question.question()).isEqualTo("What is 2+2?");
    assertThat(question.choices()).hasSize(4);
  }

  @Test
  @DisplayName("Should detect missing required Question column and return error")
  void testParseCsv_MissingRequiredColumn() throws IOException {
    // Arrange
    InputStream csvStream = createInputStream(MISSING_QUESTION_COLUMN_CSV);

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).isEmpty();
    assertThat(response.validationErrors()).isNotEmpty();
    assertThat(response.validationErrors())
        .anyMatch(error -> error.errorType() == ErrorType.MISSING_COLUMN
            && error.columnName().equals("Question"));
  }

  @Test
  @DisplayName("Should validate empty question field and return error")
  void testParseCsv_EmptyQuestion() throws IOException {
    // Arrange
    InputStream csvStream = createInputStream(EMPTY_QUESTION_CSV);

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).isEmpty();
    assertThat(response.validationErrors()).isNotEmpty();
    assertThat(response.validationErrors())
        .anyMatch(error -> error.errorType() == ErrorType.EMPTY_FIELD
            && error.columnName().equals("Question")
            && error.rowNumber() == 2);
  }

  @Test
  @DisplayName("Should detect when correct answer doesn't match any option")
  void testParseCsv_CorrectAnswerMismatch() throws IOException {
    // Arrange
    InputStream csvStream = createInputStream(CORRECT_ANSWER_MISMATCH_CSV);

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).isEmpty();
    assertThat(response.validationErrors()).isNotEmpty();
    assertThat(response.validationErrors())
        .anyMatch(error -> error.errorType() == ErrorType.INVALID_ANSWER
            && error.columnName().equals("Correct Answer")
            && error.message().contains("must exactly match one of the four options"));
  }

  @Test
  @DisplayName("Should parse CSV with valid time limit within range (5-240 seconds)")
  void testParseCsv_ValidTimeLimit() throws IOException {
    // Arrange
    InputStream csvStream = createInputStream(VALID_TIME_LIMIT_CSV);

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).hasSize(1);
    assertThat(response.validationErrors()).isEmpty();

    KahootQuestion question = response.questions().get(0);
    assertThat(question.timeLimit()).isEqualTo(20);
  }

  @Test
  @DisplayName("Should detect invalid time limit (out of 5-240 range) and return error")
  void testParseCsv_InvalidTimeLimit() throws IOException {
    // Arrange
    InputStream csvStream = createInputStream(INVALID_TIME_LIMIT_CSV);

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).isEmpty();
    assertThat(response.validationErrors()).isNotEmpty();
    assertThat(response.validationErrors())
        .anyMatch(error -> error.errorType() == ErrorType.INVALID_TIME_LIMIT
            && error.columnName().equals("Time Limit")
            && error.message().contains("must be between 5 and 240 seconds"));
  }

  @Test
  @DisplayName("Should detect empty CSV file and return error")
  void testParseCsv_EmptyFile() throws IOException {
    // Arrange
    InputStream csvStream = createInputStream(EMPTY_CSV);

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).isEmpty();
    assertThat(response.validationErrors()).isNotEmpty();
    assertThat(response.validationErrors())
        .anyMatch(error -> error.message().contains("CSV file is empty"));
    assertThat(response.totalRows()).isEqualTo(0);
    assertThat(response.validRows()).isEqualTo(0);
  }

  @Test
  @DisplayName("Should limit to maximum 100 questions and return error for excess")
  void testParseCsv_TooManyQuestions() throws IOException {
    // Arrange
    StringBuilder csvBuilder = new StringBuilder("Question,Option 1,Option 2,Option 3,Option 4,Correct Answer\n");
    for (int i = 1; i <= 105; i++) {
      csvBuilder.append(String.format("Question %d?,A,B,C,D,B\n", i));
    }
    InputStream csvStream = createInputStream(csvBuilder.toString());

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).hasSize(100);
    assertThat(response.totalRows()).isEqualTo(101); // Service increments totalRows before checking max
    assertThat(response.validationErrors()).isNotEmpty();
    assertThat(response.validationErrors())
        .anyMatch(error -> error.message().contains("Maximum 100 questions allowed"));
  }

  @Test
  @DisplayName("Should parse CSV without time limit column using default value")
  void testParseCsv_WithOptionalTimeLimit() throws IOException {
    // Arrange
    InputStream csvStream = createInputStream(OPTIONAL_TIME_LIMIT_CSV);

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).hasSize(2);
    assertThat(response.validationErrors()).isEmpty();

    // Verify default time limit is used (20 seconds)
    assertThat(response.questions().get(0).timeLimit()).isEqualTo(20);
    assertThat(response.questions().get(1).timeLimit()).isEqualTo(20);
  }

  @Test
  @DisplayName("Should handle CSV with header only (no data rows)")
  void testParseCsv_HeaderOnly() throws IOException {
    // Arrange
    InputStream csvStream = createInputStream(HEADER_ONLY_CSV);

    // Act
    CsvPreviewResponse response = csvParserService.parseCsv(csvStream);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.questions()).isEmpty();
    assertThat(response.totalRows()).isEqualTo(0);
    assertThat(response.validRows()).isEqualTo(0);
    assertThat(response.validationErrors()).isEmpty();
  }

  // ==================== detectColumns Tests ====================

  @Test
  @DisplayName("Should detect standard column names correctly")
  void testDetectColumns_StandardNames() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer", "Time Limit"};

    // Act
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);

    // Assert
    assertThat(columnMap).isNotNull();
    assertThat(columnMap).containsEntry("question", 0);
    assertThat(columnMap).containsEntry("option1", 1);
    assertThat(columnMap).containsEntry("option2", 2);
    assertThat(columnMap).containsEntry("option3", 3);
    assertThat(columnMap).containsEntry("option4", 4);
    assertThat(columnMap).containsEntry("correctAnswer", 5);
    assertThat(columnMap).containsEntry("timeLimit", 6);
  }

  @Test
  @DisplayName("Should detect columns regardless of case")
  void testDetectColumns_CaseInsensitive() {
    // Arrange
    String[] headers = {"QUESTION", "option 1", "OPTION 2", "Option 3", "oPtIoN 4", "CORRECT ANSWER"};

    // Act
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);

    // Assert
    assertThat(columnMap).isNotNull();
    assertThat(columnMap).containsEntry("question", 0);
    assertThat(columnMap).containsEntry("option1", 1);
    assertThat(columnMap).containsEntry("option2", 2);
    assertThat(columnMap).containsEntry("option3", 3);
    assertThat(columnMap).containsEntry("option4", 4);
    assertThat(columnMap).containsEntry("correctAnswer", 5);
  }

  @Test
  @DisplayName("Should detect alternative column names (q, choice 1, etc.)")
  void testDetectColumns_AlternativeNames() {
    // Arrange
    String[] headers = {"q", "a", "choice 2", "option 3", "answer 4", "correct", "time"};

    // Act
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);

    // Assert
    assertThat(columnMap).isNotNull();
    assertThat(columnMap).containsEntry("question", 0);
    assertThat(columnMap).containsEntry("option1", 1);
    assertThat(columnMap).containsEntry("option2", 2);
    assertThat(columnMap).containsEntry("option3", 3);
    assertThat(columnMap).containsEntry("option4", 4);
    assertThat(columnMap).containsEntry("correctAnswer", 5);
    assertThat(columnMap).containsEntry("timeLimit", 6);
  }

  // ==================== validateRow Tests ====================

  @Test
  @DisplayName("Should return empty error list for valid row with all fields")
  void testValidateRow_AllValid() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer", "Time Limit"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"What is 2+2?", "3", "4", "5", "6", "4", "20"};

    // Act
    List<ValidationError> errors = csvParserService.validateRow(row, 2, columnMap);

    // Assert
    assertThat(errors).isEmpty();
  }

  @Test
  @DisplayName("Should return error when correct answer doesn't match any option")
  void testValidateRow_CorrectAnswerNotMatching() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"What is 2+2?", "3", "4", "5", "6", "7"};

    // Act
    List<ValidationError> errors = csvParserService.validateRow(row, 2, columnMap);

    // Assert
    assertThat(errors).isNotEmpty();
    assertThat(errors)
        .anyMatch(error -> error.errorType() == ErrorType.INVALID_ANSWER
            && error.columnName().equals("Correct Answer"));
  }

  @Test
  @DisplayName("Should return error for empty question field")
  void testValidateRow_EmptyQuestion() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"", "3", "4", "5", "6", "4"};

    // Act
    List<ValidationError> errors = csvParserService.validateRow(row, 2, columnMap);

    // Assert
    assertThat(errors).isNotEmpty();
    assertThat(errors)
        .anyMatch(error -> error.errorType() == ErrorType.EMPTY_FIELD
            && error.columnName().equals("Question"));
  }

  @Test
  @DisplayName("Should return errors for empty options")
  void testValidateRow_EmptyOptions() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"What is 2+2?", "", "4", "", "6", "4"};

    // Act
    List<ValidationError> errors = csvParserService.validateRow(row, 2, columnMap);

    // Assert
    assertThat(errors).hasSize(2);
    assertThat(errors)
        .anyMatch(error -> error.errorType() == ErrorType.EMPTY_FIELD
            && error.columnName().equals("Option 1"));
    assertThat(errors)
        .anyMatch(error -> error.errorType() == ErrorType.EMPTY_FIELD
            && error.columnName().equals("Option 3"));
  }

  @Test
  @DisplayName("Should return error for time limit below minimum (5 seconds)")
  void testValidateRow_TimeLimitTooLow() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer", "Time Limit"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"What is 2+2?", "3", "4", "5", "6", "4", "3"};

    // Act
    List<ValidationError> errors = csvParserService.validateRow(row, 2, columnMap);

    // Assert
    assertThat(errors).isNotEmpty();
    assertThat(errors)
        .anyMatch(error -> error.errorType() == ErrorType.INVALID_TIME_LIMIT
            && error.columnName().equals("Time Limit"));
  }

  @Test
  @DisplayName("Should return error for time limit above maximum (240 seconds)")
  void testValidateRow_TimeLimitTooHigh() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer", "Time Limit"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"What is 2+2?", "3", "4", "5", "6", "4", "300"};

    // Act
    List<ValidationError> errors = csvParserService.validateRow(row, 2, columnMap);

    // Assert
    assertThat(errors).isNotEmpty();
    assertThat(errors)
        .anyMatch(error -> error.errorType() == ErrorType.INVALID_TIME_LIMIT
            && error.message().contains("must be between 5 and 240 seconds"));
  }

  @Test
  @DisplayName("Should return error for non-numeric time limit")
  void testValidateRow_InvalidTimeLimitFormat() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer", "Time Limit"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"What is 2+2?", "3", "4", "5", "6", "4", "abc"};

    // Act
    List<ValidationError> errors = csvParserService.validateRow(row, 2, columnMap);

    // Assert
    assertThat(errors).isNotEmpty();
    assertThat(errors)
        .anyMatch(error -> error.errorType() == ErrorType.INVALID_TIME_LIMIT
            && error.message().contains("must be a valid number"));
  }

  @Test
  @DisplayName("Should pass validation when time limit column is absent")
  void testValidateRow_NoTimeLimitColumn() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"What is 2+2?", "3", "4", "5", "6", "4"};

    // Act
    List<ValidationError> errors = csvParserService.validateRow(row, 2, columnMap);

    // Assert
    assertThat(errors).isEmpty();
  }

  // ==================== parseQuestion Tests ====================

  @Test
  @DisplayName("Should parse question with all fields correctly")
  void testParseQuestion_CompleteQuestion() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer", "Time Limit"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"What is 2+2?", "3", "4", "5", "6", "4", "25"};

    // Act
    KahootQuestion question = csvParserService.parseQuestion(row, columnMap);

    // Assert
    assertThat(question).isNotNull();
    assertThat(question.question()).isEqualTo("What is 2+2?");
    assertThat(question.timeLimit()).isEqualTo(25);
    assertThat(question.choices()).hasSize(4);

    assertThat(question.choices().get(0).answerText()).isEqualTo("3");
    assertThat(question.choices().get(0).isCorrect()).isFalse();

    assertThat(question.choices().get(1).answerText()).isEqualTo("4");
    assertThat(question.choices().get(1).isCorrect()).isTrue();

    assertThat(question.choices().get(2).answerText()).isEqualTo("5");
    assertThat(question.choices().get(2).isCorrect()).isFalse();

    assertThat(question.choices().get(3).answerText()).isEqualTo("6");
    assertThat(question.choices().get(3).isCorrect()).isFalse();
  }

  @Test
  @DisplayName("Should use default time limit (20s) when time limit column is missing")
  void testParseQuestion_DefaultTimeLimit() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"What is 2+2?", "3", "4", "5", "6", "4"};

    // Act
    KahootQuestion question = csvParserService.parseQuestion(row, columnMap);

    // Assert
    assertThat(question).isNotNull();
    assertThat(question.timeLimit()).isEqualTo(20);
  }

  @Test
  @DisplayName("Should use default time limit when time limit value is empty")
  void testParseQuestion_EmptyTimeLimitUsesDefault() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer", "Time Limit"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"What is 2+2?", "3", "4", "5", "6", "4", ""};

    // Act
    KahootQuestion question = csvParserService.parseQuestion(row, columnMap);

    // Assert
    assertThat(question).isNotNull();
    assertThat(question.timeLimit()).isEqualTo(20);
  }

  @Test
  @DisplayName("Should mark only the correct answer as correct")
  void testParseQuestion_CorrectAnswerMarking() {
    // Arrange
    String[] headers = {"Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer"};
    Map<String, Integer> columnMap = csvParserService.detectColumns(headers);
    String[] row = {"What is the capital of France?", "London", "Paris", "Berlin", "Madrid", "Paris"};

    // Act
    KahootQuestion question = csvParserService.parseQuestion(row, columnMap);

    // Assert
    assertThat(question).isNotNull();
    assertThat(question.choices().stream().filter(Choice::isCorrect).count()).isEqualTo(1);
    assertThat(question.choices().get(1).answerText()).isEqualTo("Paris");
    assertThat(question.choices().get(1).isCorrect()).isTrue();
  }

  // ==================== Helper Methods ====================

  /**
   * Creates an InputStream from a CSV string for testing.
   */
  private InputStream createInputStream(String csvContent) {
    return new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
  }
}
