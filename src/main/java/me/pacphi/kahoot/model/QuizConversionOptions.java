package me.pacphi.kahoot.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Configuration options for converting questions into a Kahoot quiz.
 * Provides sensible defaults for quiz generation settings.
 *
 * @param shuffleQuestions  Whether to randomize the order of questions in the quiz
 * @param shuffleAnswers    Whether to randomize the order of answer choices for each question
 * @param defaultTimeLimit  Default time limit per question in seconds (5-240)
 */
public record QuizConversionOptions(
    boolean shuffleQuestions,
    boolean shuffleAnswers,

    @Min(value = 5, message = "Time limit must be at least 5 seconds")
    @Max(value = 240, message = "Time limit must not exceed 240 seconds")
    int defaultTimeLimit) {

  /**
   * Default constructor with sensible defaults:
   * - No shuffling of questions or answers
   * - 30 second default time limit
   */
  public QuizConversionOptions() {
    this(false, false, 30);
  }

  /**
   * Validates that time limit is within acceptable range.
   */
  public QuizConversionOptions {
    if (defaultTimeLimit < 5 || defaultTimeLimit > 240) {
      throw new IllegalArgumentException("Default time limit must be between 5 and 240 seconds");
    }
  }

  /**
   * Creates options with shuffling enabled for both questions and answers.
   *
   * @param defaultTimeLimit Time limit in seconds
   * @return New options with full shuffling enabled
   */
  public static QuizConversionOptions withShuffling(int defaultTimeLimit) {
    return new QuizConversionOptions(true, true, defaultTimeLimit);
  }

  /**
   * Creates options with only question shuffling enabled.
   *
   * @param defaultTimeLimit Time limit in seconds
   * @return New options with question shuffling enabled
   */
  public static QuizConversionOptions withQuestionShuffling(int defaultTimeLimit) {
    return new QuizConversionOptions(true, false, defaultTimeLimit);
  }

  /**
   * Creates options with only answer shuffling enabled.
   *
   * @param defaultTimeLimit Time limit in seconds
   * @return New options with answer shuffling enabled
   */
  public static QuizConversionOptions withAnswerShuffling(int defaultTimeLimit) {
    return new QuizConversionOptions(false, true, defaultTimeLimit);
  }
}
