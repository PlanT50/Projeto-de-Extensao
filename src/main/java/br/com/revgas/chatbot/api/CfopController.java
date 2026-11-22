package br.com.revgas.chatbot.api;

import br.com.revgas.chatbot.integration.erp.cfop.CfopApiClient;
import br.com.revgas.chatbot.integration.erp.cfop.Cfop;
import br.com.revgas.chatbot.integration.erp.cfop.CfopFilter;
import br.com.revgas.chatbot.integration.erp.cfop.CfopPage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/erp/cfops")
public class CfopController {
    private final CfopApiClient client;

    public CfopController(CfopApiClient client) {
        this.client = client;
    }

    @GetMapping
    public CfopPage list(
            @RequestParam(required = false) Integer page,
            @RequestParam(name = "page_size", required = false) Integer pageSize,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean departure,
            @RequestParam(required = false) Boolean incoming,
            @RequestParam(name = "return", required = false) Boolean refund,
            @RequestParam(required = false) Boolean fuel
    ) {
        return client.list(toFilter(page, pageSize, code, description, departure, incoming, refund, fuel));
    }

    @GetMapping("/all")
    public List<Cfop> listAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(name = "page_size", required = false) Integer pageSize,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean departure,
            @RequestParam(required = false) Boolean incoming,
            @RequestParam(name = "return", required = false) Boolean refund,
            @RequestParam(required = false) Boolean fuel
    ) {
        return client.listAll(toFilter(page, pageSize, code, description, departure, incoming, refund, fuel));
    }

    private CfopFilter toFilter(Integer page, Integer pageSize, String code, String description,
                                Boolean departure, Boolean incoming, Boolean refund, Boolean fuel) {
        if ((page != null && page < 1) || (pageSize != null && pageSize < 1)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page e page_size devem ser maiores que zero.");
        }
        if (departure != null && incoming != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "departure e incoming não podem ser usados juntos.");
        }
        return new CfopFilter(page, pageSize, code, description, departure, incoming, refund, fuel);
    }
}
