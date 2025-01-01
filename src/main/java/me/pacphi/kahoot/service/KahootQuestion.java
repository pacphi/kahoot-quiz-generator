package me.pacphi.kahoot.service;

import java.util.List;

/**
 * Represents a single Kahoot quiz question with multiple choice answers.
 *
 * @param question The text of the question
 * @param choices  List of possible answers, where exactly one must be correct
 */
public record KahootQuestion(
        String question,
        List<Choice> choices) {
    /**
     * Represents a single answer choice for a Kahoot question.
     *
     * @param answerText The text of the answer choice
     * @param isCorrect  Whether this is the correct answer
     */
    public record Choice(
            String answerText,
            boolean isCorrect) {
        /**
         * Validates that the choice has non-empty answer text.
         */
        public Choice
        {
            if (answerText == null || answerText.trim().isEmpty()) {
                throw new IllegalArgumentException("Answer text cannot be empty");
            }
        }
    }

    /**
     * Validates that the question is properly formed:
     * - Question text is not empty
     * - Has exactly 4 choices
     * - Exactly one choice is marked as correct
     */
    public KahootQuestion

    {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }

        if (choices == null || choices.size() != 4) {
            throw new IllegalArgumentException("Must have exactly 4 choices");
        }

        long correctCount = choices.stream()
                .filter(Choice::isCorrect)
                .count();

        if (correctCount != 1) {
            throw new IllegalArgumentException("Must have exactly one correct answer");
        }
    }
}
