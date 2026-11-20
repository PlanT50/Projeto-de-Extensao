package br.com.revgas.chatbot.domain;

import java.util.Optional;

public interface ConversationRepository {
    Optional<Conversation> findById(String id);
    Conversation save(Conversation conversation);
}
