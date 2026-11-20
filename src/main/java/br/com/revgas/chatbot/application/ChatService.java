package br.com.revgas.chatbot.application;

import br.com.revgas.chatbot.api.ChatRequest;
import br.com.revgas.chatbot.api.ChatResponse;
import br.com.revgas.chatbot.domain.Conversation;
import br.com.revgas.chatbot.domain.ConversationRepository;
import br.com.revgas.chatbot.domain.ConversationStage;
import br.com.revgas.chatbot.integration.erp.ErpIntegrationStatus;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ChatService {
    private final ConversationRepository conversations;
    private final ErpIntegrationStatus erpStatus;

    public ChatService(ConversationRepository conversations, ErpIntegrationStatus erpStatus) {
        this.conversations = conversations;
        this.erpStatus = erpStatus;
    }

    public ChatResponse handle(ChatRequest request) {
        var existing = conversations.findById(request.conversationId());
        if (existing.isEmpty()) {
            Conversation started = conversations.save(new Conversation(request.conversationId()));
            return response(started, "Olá! Posso ajudar com seu pedido. Qual é seu nome?", false);
        }
        Conversation conversation = existing.get();
        String message = request.message().trim();
        String normalized = message.toLowerCase(Locale.ROOT);

        if (asksForPerson(normalized)) {
            conversation.setStage(ConversationStage.HANDED_OFF);
            conversations.save(conversation);
            return response(conversation, "Vou encaminhar sua conversa para um atendente. O atendimento humano ainda precisa ser conectado à plataforma da Flow.", true);
        }

        if (conversation.getStage() == ConversationStage.HANDED_OFF) {
            return response(conversation, "Sua conversa está marcada para atendimento humano. Aguarde o contato da equipe.", true);
        }

        String reply = switch (conversation.getStage()) {
            case COLLECTING_NAME -> {
                conversation.getOrder().setCustomerName(message);
                conversation.setStage(ConversationStage.COLLECTING_PRODUCT);
                yield "Obrigado, " + message + ". Qual produto você gostaria de pedir?";
            }
            case COLLECTING_PRODUCT -> {
                conversation.getOrder().setProduct(message);
                conversation.setStage(ConversationStage.COLLECTING_QUANTITY);
                yield "Qual quantidade de " + message + " você precisa?";
            }
            case COLLECTING_QUANTITY -> collectQuantity(conversation, message);
            case COLLECTING_ADDRESS -> {
                conversation.getOrder().setDeliveryAddress(message);
                conversation.setStage(ConversationStage.COLLECTING_PAYMENT);
                yield "Qual forma de pagamento prefere?";
            }
            case COLLECTING_PAYMENT -> {
                conversation.getOrder().setPaymentMethod(message);
                conversation.setStage(ConversationStage.READY_FOR_CONFIRMATION);
                yield orderSummary(conversation) + " Confirma que esses dados estão corretos? Ainda não enviarei pedido ao ERP.";
            }
            case READY_FOR_CONFIRMATION -> confirmOrder(conversation, normalized);
            case HANDED_OFF -> "Sua conversa está marcada para atendimento humano. Aguarde o contato da equipe.";
        };

        conversations.save(conversation);
        return response(conversation, reply, false);
    }

    private String collectQuantity(Conversation conversation, String message) {
        try {
            int quantity = Integer.parseInt(message.replaceAll("[^0-9]", ""));
            if (quantity < 1 || quantity > 1000) return "Informe uma quantidade entre 1 e 1000 unidades.";
            conversation.getOrder().setQuantity(quantity);
            conversation.setStage(ConversationStage.COLLECTING_ADDRESS);
            return "Para qual endereço devemos entregar?";
        } catch (NumberFormatException exception) {
            return "Não consegui identificar a quantidade. Informe apenas o número de unidades.";
        }
    }

    private String confirmOrder(Conversation conversation, String normalized) {
        if (normalized.matches(".*\\b(sim|confirmo|correto|confirmar)\\b.*")) {
            return erpStatus.isConfigured()
                    ? "Confirmação recebida. A integração de criação de pedido ainda precisa ser implementada."
                    : "Dados anotados, mas a API do ERP ainda não está conectada. Nenhum pedido foi criado. A equipe pode continuar o atendimento.";
        }
        if (normalized.matches(".*\\b(nao|não|corrigir|alterar)\\b.*")) {
            conversation.setStage(ConversationStage.COLLECTING_NAME);
            return "Sem problema. Vamos revisar desde o início. Qual é seu nome?";
        }
        return "Responda 'sim' para confirmar os dados ou 'não' para revisar.";
    }

    private String orderSummary(Conversation conversation) {
        var order = conversation.getOrder();
        return "Resumo: cliente " + order.getCustomerName() + ", produto " + order.getProduct()
                + ", quantidade " + order.getQuantity() + ", endereço " + order.getDeliveryAddress()
                + ", pagamento " + order.getPaymentMethod() + ".";
    }

    private boolean asksForPerson(String normalized) {
        return normalized.contains("atendente") || normalized.contains("humano")
                || normalized.contains("pessoa") || normalized.contains("vendedor");
    }

    private ChatResponse response(Conversation conversation, String reply, boolean handoff) {
        return new ChatResponse(conversation.getId(), reply, conversation.getStage(), handoff);
    }
}
