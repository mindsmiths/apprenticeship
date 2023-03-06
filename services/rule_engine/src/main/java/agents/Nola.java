package agents;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.*;

import com.mindsmiths.gpt3.chatCompletion.ChatCompletionModel;
import com.mindsmiths.gpt3.chatCompletion.ChatMessage;
import com.mindsmiths.gpt3.chatCompletion.ChatCues;
import com.mindsmiths.gpt3.GPT3AdapterAPI;
import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.ruleEngine.util.Log;
import com.mindsmiths.telegramAdapter.TelegramAdapterAPI;
import com.mindsmiths.telegramAdapter.api.MediaData;
import com.mindsmiths.telegramAdapter.api.MediaType;

import utils.Utils;


@Getter
@Setter
public class Nola extends Agent {

    String personality = "You're an AI system called Nola Brzina talking to a user. " +
            "You want to have an engaging and fun conversation with them. " +
            "You are friendly, creative and innovative. " +
            "Finish your messages with punctuation marks.";

    private List<ChatMessage> memory = new ArrayList<>();
    private int MAX_MEMORY = 4;
    private LocalDateTime lastInteractionTime;
    private boolean pinged;

    public Nola() {
    }

    // Memory

    public void addMessageToMemory(String sender, String text) {
        memory.add(new ChatMessage(sender, text));
        trimMemory();
    }

    private void trimMemory() {
        if (memory.size() > MAX_MEMORY + 1)
            memory = memory.subList(memory.size() - 1 - MAX_MEMORY, memory.size());
    }

    // Chat communication

    public void sendMessage(String text) {
        String message = Utils.trimText(text);
        addMessageToMemory(ChatCues.ASSISTANT.label, message);
        String chatId = getConnection("telegram");
        TelegramAdapterAPI.sendMessage(chatId, message);
    }

    public void sendWaitingMessage() {
        String message = com.mindsmiths.sdk.utils.Utils.randomChoice(waitingForDallETexts);
        String chatId = getConnection("telegram");
        TelegramAdapterAPI.sendMessage(chatId, message);
    }

    public void sendImage(String url) {
        String chatId = getConnection("telegram");
        addMessageToMemory(ChatCues.ASSISTANT.label, "Here is an image of it: https://demo.blob.core.windows.net/private/org-cmqScogXMe");
        List<MediaData> mediaList = new ArrayList<>();
        mediaList.add(new MediaData(MediaType.photo, url));
        TelegramAdapterAPI.sendMessage(chatId, mediaList);
    }

    // OpenAI models

    public void askGPT3(String message) {
        List<ChatMessage> prompt = new ArrayList<>();
        prompt.add(new ChatMessage(ChatCues.SYSTEM.label, personality));
        this.addMessageToMemory(ChatCues.USER.label, message);
        prompt.addAll(memory);

        simpleChatRequest(prompt);
    }

    public void simpleChatRequest(List<ChatMessage> prompt) {
        Log.info("Prompt for GPT:\n" + prompt);
        GPT3AdapterAPI.chatComplete(
                prompt, // input prompt
                ChatCompletionModel.gpt_turbo, // model
                150, // max tokens
                0.8, // temperature
                1.0, // topP
                1, // N
                List.of(ChatCues.USER.label, ChatCues.ASSISTANT.label), // STOP words
                0.6, // presence penalty
                0.0, // frequency penalty
                null // logit bias
        );
    }

    public void askDallE(String text) {
        addMessageToMemory(ChatCues.USER.label, text);
        sendWaitingMessage();

        String prompt = text.split("[iI]magine")[1];
        prompt = prompt.replaceAll("\\s*\\p{Punct}+\\s*$", "").trim();

        simpleDallERequest(prompt);
    }

    public void simpleDallERequest(String prompt) {
        Log.info("Prompt for DallE: " + prompt);
        GPT3AdapterAPI.generateImage(prompt);
    }

    public static List<String> waitingForDallETexts = List.of(
            "Hmm, sure! Hang on a sec.",
            "Let me think.",
            "Wait, I'm on it.",
            "Got it, wait a bit.",
            "That's an interesting one, let me give it a shot."
    );
}