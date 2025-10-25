package me.pacphi.kahoot.model;

/**
 * Represents a validation error encountered during CSV processing.
 * Used to report issues with uploaded CSV data to clients.
 *
 * @param errorType   The category of validation error
 * @param rowNumber   The row number in the CSV where the error occurred (1-based, null for header errors)
 * @param columnName  The name of the column where the error occurred
 * @param message     A human-readable description of the error
 */
public record ValidationError(
    ErrorType errorType,
    Integer rowNumber,
    String columnName,
    String message) {

  /**
   * Validates that required fields are populated and consistent.
   */
  public ValidationError {
    if (errorType == null) {
      throw new IllegalArgumentException("Error type cannot be null");
    }

    if (message == null || message.trim().isEmpty()) {
      throw new IllegalArgumentException("Error message cannot be empty");
    }

    if (rowNumber != null && rowNumber < 1) {
      throw new IllegalArgumentException("Row number must be positive");
    }
  }

  /**
   * Enumeration of possible CSV validation error types.
   */
  public enum ErrorType {
    /**
     * A required column is missing from the CSV header.
     */
    MISSING_COLUMN,

    /**
     * An invalid answer configuration (e.g., no correct answer, multiple correct answers).
     */
    INVALID_ANSWER,

    /**
     * Time limit value is outside the valid range (5-240 seconds).
     */
    INVALID_TIME_LIMIT,

    /**
     * A required field contains no value.
     */
    EMPTY_FIELD
  }
}
