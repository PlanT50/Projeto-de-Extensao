package br.com.revgas.chatbot.integration.erp.cfop;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

/** Read-only fiscal classification returned by the RevGas CFOP endpoint. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Cfop(
        Long id,
        @JsonAlias({"created_at", "criado_em"}) OffsetDateTime createdAt,
        @JsonAlias({"updated_at", "atualizado_em"}) OffsetDateTime updatedAt,
        @JsonAlias({"code", "código", "codigo"}) String code,
        @JsonAlias({"description", "descrição", "descricao"}) String description,
        @JsonAlias({"fuel", "combustível", "combustivel"}) Boolean fuel,
        @JsonAlias({"refund", "return", "devolução", "devolucao"}) Boolean refund,
        @JsonAlias({"fiscal_movement", "movimento_fiscal"}) Boolean fiscalMovement
) {}
