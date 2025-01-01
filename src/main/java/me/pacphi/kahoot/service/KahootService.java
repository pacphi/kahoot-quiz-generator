package me.pacphi.kahoot.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class KahootService {

    private final ChatClient chatClient;

    public KahootService(ChatModel model) {
        this.chatClient = ChatClient.builder(model)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public List<KahootQuestion> generateQuestions(String prompt, int numberOfQuestions) {
        SystemMessage systemMessage = new SystemMessage(String.format("""
            You are a Kahoot quiz generator. Generate engaging and age-appropriate questions.
            Each response should be a valid JSON array of %d question objects.
            Each question object should have:
            - question (string)
            - choices (array of 4 objects with answerText and isCorrect)
            - Only one choice should have isCorrect=true
            """, numberOfQuestions));
        UserMessage userMessage = new UserMessage("Generate quiz questions for: " + prompt);
        var chatPrompt = new Prompt(List.of(systemMessage, userMessage));

        var chatResponse = chatClient.prompt(chatPrompt).call();
        return chatResponse.entity(new ParameterizedTypeReference<List<KahootQuestion>>() {});
    }

}
