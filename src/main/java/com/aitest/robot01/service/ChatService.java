package com.aitest.robot01.service;

import com.aitest.robot01.model.ChatMessage;
import com.aitest.robot01.model.Conversation;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public Conversation createConversation(String title) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Conversation conversation = new Conversation(id, title);
        conversations.put(id, conversation);
        return conversation;
    }

    public List<Conversation> listConversations() {
        return new ArrayList<>(conversations.values());
    }

    public Conversation getConversation(String id) {
        return conversations.get(id);
    }

    public ChatMessage sendMessage(String conversationId, String userMessage) {
        Conversation conversation = conversations.get(conversationId);
        if (conversation == null) {
            conversation = createConversation(userMessage != null && userMessage.length() > 20
                    ? userMessage.substring(0, 20) + "..."
                    : "新对话");
        }

        ChatMessage userMsg = new ChatMessage("user", userMessage);
        conversation.getMessages().add(userMsg);

        StringBuilder promptBuilder = new StringBuilder();
        for (ChatMessage msg : conversation.getMessages()) {
            promptBuilder.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
        }
        promptBuilder.append("assistant: ");

        String aiResponse;
        try {
            aiResponse = chatClient.prompt()
                    .user(promptBuilder.toString())
                    .call()
                    .content();
        } catch (Exception e) {
            aiResponse = "抱歉，AI 服务调用失败：" + e.getMessage();
        }

        if (aiResponse == null) {
            aiResponse = "抱歉，AI 未返回有效回复。";
        }

        ChatMessage assistantMsg = new ChatMessage("assistant", aiResponse);
        conversation.getMessages().add(assistantMsg);

        return assistantMsg;
    }

    public void deleteConversation(String id) {
        conversations.remove(id);
    }
}
