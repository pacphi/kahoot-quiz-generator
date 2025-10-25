package me.pacphi.kahoot.model;

import me.pacphi.kahoot.service.KahootQuestion;

import java.util.List;
import java.util.Map;

/**
 * Response containing the preview of CSV parsing results.
 * Provides clients with parsed questions, validation errors, and statistics
 * about the uploaded CSV file before final conversion.
 *
 * @param questions        List of successfully parsed Kahoot questions
 * @param validationErrors List of validation errors encountered during parsing
 * @param columnMapping    Mapping from detected CSV column names to standard field names
 * @param totalQuestions   Total number of data rows in the CSV (excluding header)
 * @param validQuestions   Number of rows that were successfully parsed without errors
 */
public record CsvPreviewResponse(
    List<KahootQuestion> questions,
    List<ValidationError> validationErrors,
    Map<String, String> columnMapping,
    int totalQuestions,
    int validQuestions) {

  /**
   * Validates that the response data is consistent and non-null.
   */
  public CsvPreviewResponse {
    if (questions == null) {
      throw new IllegalArgumentException("Questions list cannot be null");
    }

    if (validationErrors == null) {
      throw new IllegalArgumentException("Validation errors list cannot be null");
    }

    if (columnMapping == null) {
      throw new IllegalArgumentException("Column mapping cannot be null");
    }

    if (totalQuestions < 0) {
      throw new IllegalArgumentException("Total questions cannot be negative");
    }

    if (validQuestions < 0) {
      throw new IllegalArgumentException("Valid questions cannot be negative");
    }

    if (validQuestions > totalQuestions) {
      throw new IllegalArgumentException("Valid questions cannot exceed total questions");
    }
  }

  /**
   * Checks if the CSV preview contains any validation errors.
   *
   * @return true if there are validation errors, false otherwise
   */
  public boolean hasErrors() {
    return !validationErrors.isEmpty();
  }

  /**
   * Checks if all questions in the CSV were successfully parsed.
   *
   * @return true if all questions are valid, false otherwise
   */
  public boolean isFullyValid() {
    return validQuestions == totalQuestions && !hasErrors();
  }
}
