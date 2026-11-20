package br.com.revgas.chatbot.api;

import br.com.revgas.chatbot.domain.ConversationStage;

public record ChatResponse(
        String conversationId,
        String reply,
        ConversationStage stage,
        boolean humanHandoff
) {}
