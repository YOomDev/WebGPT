package com.ypeoomes.service;

import com.ypeoomes.config.SettingsProperties;
import com.ypeoomes.utils.gpt.OllamaAPI;
import com.ypeoomes.utils.logging.Log;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {
    private final OllamaAPI ollamaAPI;
    private final SettingsProperties settings;

    public OllamaService(SettingsProperties settings) {
        this.settings = settings;
        this.ollamaAPI = new OllamaAPI(
            settings.getDefaultModel(),
            settings.getOllamaUrl() + "/api/generate"
        );
    }

    public String prompt(String prompt) {
        return ollamaAPI.prompt(prompt);
    }

    public String prompt(String prompt, String systemPrompt) {
        return ollamaAPI.prompt(prompt, systemPrompt);
    }

    public String getDefaultModel() {
        return settings.getDefaultModel();
    }

    public String getOllamaUrl() {
        return settings.getOllamaUrl();
    }
}