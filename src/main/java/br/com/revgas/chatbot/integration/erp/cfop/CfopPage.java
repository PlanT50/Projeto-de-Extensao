package br.com.revgas.chatbot.integration.erp.cfop;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CfopPage(
        @JsonAlias({"data", "dados"}) List<Cfop> data,
        @JsonAlias({"pagination", "paginação", "paginacao"}) Pagination pagination
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Pagination(
            @JsonAlias({"page", "página", "pagina"}) Integer page,
            @JsonAlias({"page_size", "pageSize", "tamanho_da_página", "tamanho_da_pagina"}) Integer pageSize,
            @JsonAlias({"count", "contagem"}) Integer count,
            @JsonAlias({"total_count", "totalCount", "contagem_total"}) Integer totalCount,
            @JsonAlias({"has_next", "hasNext"}) Boolean hasNext
    ) {}
}
