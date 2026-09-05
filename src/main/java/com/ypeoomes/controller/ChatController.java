package com.ypeoomes.controller;

import com.ypeoomes.service.OllamaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {
    private final OllamaService ollamaService;

    public ChatController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {
        String response = ollamaService.prompt(request.getPrompt(), request.getSystemPrompt());
        return ResponseEntity.ok(Map.of("response", response));
    }

    @GetMapping("/models")
    public ResponseEntity<Map<String, String>> getModelInfo() {
        return ResponseEntity.ok(Map.of(
            "defaultModel", ollamaService.getDefaultModel(),
            "ollamaUrl", ollamaService.getOllamaUrl()
        ));
    }

    public static class ChatRequest {
        private String prompt;
        private String systemPrompt;

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        public String getSystemPrompt() {
            return systemPrompt;
        }

        public void setSystemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
        }
    }
}