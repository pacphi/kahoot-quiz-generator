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
 * @param totalRows        Total number of data rows in the CSV (excluding header)
 * @param validRows        Number of rows that were successfully parsed without errors
 */
public record CsvPreviewResponse(
    List<KahootQuestion> questions,
    List<ValidationError> validationErrors,
    Map<String, String> columnMapping,
    int totalRows,
    int validRows) {

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

    if (totalRows < 0) {
      throw new IllegalArgumentException("Total rows cannot be negative");
    }

    if (validRows < 0) {
      throw new IllegalArgumentException("Valid rows cannot be negative");
    }

    if (validRows > totalRows) {
      throw new IllegalArgumentException("Valid rows cannot exceed total rows");
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
   * Checks if all rows in the CSV were successfully parsed.
   *
   * @return true if all rows are valid, false otherwise
   */
  public boolean isFullyValid() {
    return validRows == totalRows && !hasErrors();
  }
}
