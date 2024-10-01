package model.chatGPT;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class Prompt {
    private ChatLanguageModel model;
    private String prompt;

    private String helpPrompt;

    private String text;

    public Prompt() {
        model = OpenAiChatModel.withApiKey(System.getenv("api"));
        helpPrompt = "\nHär kommer min text: ";
    }

    public String sendPrompt(String prompt) {
        return model.generate(prompt + helpPrompt + text);
    }

    public void setText(String text) {
        this.text = text;
    }

}
