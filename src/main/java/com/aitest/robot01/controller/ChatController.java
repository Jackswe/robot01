package com.aitest.robot01.controller;

import com.aitest.robot01.model.ChatMessage;
import com.aitest.robot01.model.ChatRequest;
import com.aitest.robot01.model.Conversation;
import com.aitest.robot01.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody ChatRequest request) {
        String conversationId = request.getConversationId();
        Conversation conversation;

        if (conversationId == null || conversationId.isEmpty()) {
            String title = request.getMessage() != null && request.getMessage().length() > 20
                    ? request.getMessage().substring(0, 20) + "..."
                    : request.getMessage();
            conversation = chatService.createConversation(title != null ? title : "新对话");
        } else {
            conversation = chatService.getConversation(conversationId);
            if (conversation == null) {
                conversation = chatService.createConversation("新对话");
            }
        }

        ChatMessage response = chatService.sendMessage(
                conversation.getId(),
                request.getMessage()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("message", response);
        result.put("conversationId", conversation.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> listConversations() {
        return ResponseEntity.ok(chatService.listConversations());
    }

    @PostMapping("/conversations")
    public ResponseEntity<Conversation> createConversation(@RequestBody Map<String, String> body) {
        String title = body.getOrDefault("title", "New Chat");
        return ResponseEntity.ok(chatService.createConversation(title));
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<Conversation> getConversation(@PathVariable String id) {
        Conversation conversation = chatService.getConversation(id);
        if (conversation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(conversation);
    }

    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable String id) {
        chatService.deleteConversation(id);
        return ResponseEntity.ok().build();
    }
}
