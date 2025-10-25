package me.pacphi.kahoot.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.pacphi.kahoot.service.KahootQuestion;

import java.util.List;

/**
 * Request payload for converting parsed CSV questions into a Kahoot quiz file.
 * Contains the validated questions and configuration options for the final quiz.
 *
 * @param questions         List of Kahoot questions to include in the quiz
 * @param shuffleQuestions  Whether to randomize the order of questions
 * @param shuffleAnswers    Whether to randomize the order of answer choices
 * @param defaultTimeLimit  Default time limit for questions in seconds (5-240)
 */
public record CsvConversionRequest(
    @NotNull(message = "Questions list is required")
    @Valid
    List<KahootQuestion> questions,

    boolean shuffleQuestions,

    boolean shuffleAnswers,

    @Min(value = 5, message = "Time limit must be at least 5 seconds")
    @Max(value = 240, message = "Time limit must not exceed 240 seconds")
    int defaultTimeLimit) {

  /**
   * Validates the conversion request data.
   */
  public CsvConversionRequest {
    if (questions == null) {
      throw new IllegalArgumentException("Questions list cannot be null");
    }

    if (questions.isEmpty()) {
      throw new IllegalArgumentException("Questions list cannot be empty");
    }

    if (defaultTimeLimit < 5 || defaultTimeLimit > 240) {
      throw new IllegalArgumentException("Default time limit must be between 5 and 240 seconds");
    }
  }

  /**
   * Creates a conversion request with default options.
   *
   * @param questions List of questions to convert
   * @return A new CsvConversionRequest with default settings
   */
  public static CsvConversionRequest withDefaults(List<KahootQuestion> questions) {
    return new CsvConversionRequest(questions, false, false, 30);
  }
}
