package com.ypeoomes.utils.gpt;

import com.ypeoomes.utils.logging.Log;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class OllamaAPI {
    private static final String SYSTEM = "OllamaAPI";
    private URL apiServer;
    private final String model;

    public OllamaAPI(String model) {
        this(model, "http://localhost:11434/api/generate");
    }

    public OllamaAPI(String model, String remoteIP) {
        this.model = model;
        try {
            URI serverAddress = new URI(remoteIP);
            apiServer = serverAddress.toURL();
        } catch (URISyntaxException e) {
            // Handles malformed structure of the string itself
            Log.error("Error creating URI: " + e.getMessage(), SYSTEM);
            e.printStackTrace();
        } catch (java.net.MalformedURLException e) {
            Log.error("Error creating URL: " + e.getMessage(), SYSTEM);
            e.printStackTrace();
        }
    }

    /**
     * Method for prompting a GPT running on the IP that is passed in the constructor, otherwise uses a localhost address with Ollama's default port
     * If no prompt is specified: returns a default message.
     * If there was a problem with the connection or the GPT: returns a default error message
     *
     * @param prompt The prompt that will be sent to the Ollama server at the given IP
     *
     * @return Response from the GPT running on the Ollama server or default message about no prompt or error message
     */
    public String prompt(String prompt) {
        return prompt(prompt, "");
    }

    /**
     * Method for prompting a GPT running on the IP that is passed in the constructor, otherwise uses a localhost address with Ollama's default port
     * If no prompt is specified: returns a default message.
     * If there was a problem with the connection or the GPT: returns a default error message
     *
     * @param prompt The prompt that will be sent to the Ollama server at the given IP
     * @param systemPrompt The system prompt that will be sent to the Ollama server at the given IP
     *
     * @return Response from the GPT running on the Ollama server or default message about no prompt or error message
     */
    public String prompt(String prompt, String systemPrompt) {
        if (prompt.trim().isEmpty()) {
            return "No prompt provided.";
        }

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("model", model);
        jsonPayload.put("prompt", prompt);
        if (!systemPrompt.trim().isEmpty()) { jsonPayload.put("system", systemPrompt); }
        jsonPayload.put("stream", false);
        String jsonInput = jsonPayload.toString();

        try {
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) apiServer.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Close connection
            connection.disconnect();
            return new JSONObject(response.toString()).getString("response");
        } catch (IOException e) {
            Log.warn("Something went wrong trying to prompt the Ollama model: " + e.getMessage(), "OllamaAPI");
            e.printStackTrace();
            return "Something went wrong trying to contact the server, no connection or there was an internal error.";
        }
    }
}