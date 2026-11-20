package br.com.revgas.chatbot.domain;

import java.time.Instant;
import java.util.UUID;

public class Conversation {
    private final String id;
    private final OrderDraft order = new OrderDraft();
    private ConversationStage stage = ConversationStage.COLLECTING_NAME;
    private final Instant createdAt = Instant.now();

    public Conversation(String id) {
        this.id = id == null || id.isBlank() ? UUID.randomUUID().toString() : id;
    }

    public String getId() { return id; }
    public OrderDraft getOrder() { return order; }
    public ConversationStage getStage() { return stage; }
    public void setStage(ConversationStage stage) { this.stage = stage; }
    public Instant getCreatedAt() { return createdAt; }
}
