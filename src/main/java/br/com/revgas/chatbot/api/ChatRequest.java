package br.com.revgas.chatbot.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @Size(max = 100) String conversationId,
        @NotBlank @Size(max = 2000) String message
) {}
