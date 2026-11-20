package br.com.revgas.chatbot.infrastructure;

import br.com.revgas.chatbot.domain.Conversation;
import br.com.revgas.chatbot.domain.ConversationRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryConversationRepository implements ConversationRepository {
    private final ConcurrentMap<String, Conversation> conversations = new ConcurrentHashMap<>();

    @Override
    public Optional<Conversation> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(conversations.get(id));
    }

    @Override
    public Conversation save(Conversation conversation) {
        conversations.put(conversation.getId(), conversation);
        return conversation;
    }
}
