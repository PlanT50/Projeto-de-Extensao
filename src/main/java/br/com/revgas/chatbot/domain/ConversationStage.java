package br.com.revgas.chatbot.domain;

public enum ConversationStage {
    COLLECTING_NAME,
    COLLECTING_PRODUCT,
    COLLECTING_QUANTITY,
    COLLECTING_ADDRESS,
    COLLECTING_PAYMENT,
    READY_FOR_CONFIRMATION,
    HANDED_OFF
}
