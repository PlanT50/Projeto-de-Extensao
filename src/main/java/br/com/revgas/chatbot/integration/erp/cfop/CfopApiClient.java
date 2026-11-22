package br.com.revgas.chatbot.integration.erp.cfop;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;

/** Read-only adapter for GET /cfops. It does not create or change ERP data. */
@Component
public class CfopApiClient {
    private static final int MAX_PAGES = 100;
    private final String baseUrl;
    private final String token;
    private final HttpClient http;
    private final ObjectMapper mapper;

    public CfopApiClient(
            @Value("${revgas.api.base-url:https://api.revgas.com/v1}") String baseUrl,
            @Value("${revgas.api.token:}") String token,
            ObjectMapper mapper
    ) {
        this.baseUrl = baseUrl == null ? "" : baseUrl.replaceAll("/+$", "");
        this.token = token;
        this.mapper = mapper;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public CfopPage list(CfopFilter filter) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Configure ERP_API_TOKEN para consultar CFOPs.");
        }
        if (baseUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Configure ERP_API_BASE_URL para consultar CFOPs.");
        }

        URI uri = buildUri(filter == null ? new CfopFilter(null, null, null, null, null, null, null, null) : filter);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                // The supplied sample documents a raw token, without a Bearer prefix.
                .header("Authorization", token)
                .GET()
                .build();
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "A API RevGas respondeu com HTTP " + response.statusCode() + ".");
            }
            return mapper.readValue(response.body(), CfopPage.class);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível ler a resposta da API RevGas.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT,
                    "A consulta à API RevGas foi interrompida.");
        }
    }

    /** Fetches all pages, with a safety limit in case the upstream pagination is malformed. */
    public List<Cfop> listAll(CfopFilter filter) {
        CfopFilter base = filter == null
                ? new CfopFilter(null, null, null, null, null, null, null, null)
                : filter;
        List<Cfop> all = new ArrayList<>();
        int page = base.page() == null ? 1 : base.page();
        for (int fetched = 0; fetched < MAX_PAGES; fetched++, page++) {
            CfopPage result = list(base.withPage(page));
            if (result.data() != null) all.addAll(result.data());
            if (result.pagination() == null || !Boolean.TRUE.equals(result.pagination().hasNext())) {
                return all;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "A paginação da API RevGas excedeu o limite de segurança.");
    }

    private URI buildUri(CfopFilter f) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/cfops");
        add(builder, "page", f.page());
        add(builder, "page_size", f.pageSize());
        add(builder, "code", f.code());
        add(builder, "description", f.description());
        add(builder, "departure", f.departure());
        add(builder, "incoming", f.incoming());
        // The supplied archive marks these two parameter names as uncertain.
        add(builder, "return", f.refund());
        add(builder, "fuel", f.fuel());
        return builder.build().encode().toUri();
    }

    private void add(UriComponentsBuilder builder, String name, Object value) {
        if (value != null) builder.queryParam(name, value);
    }
}
