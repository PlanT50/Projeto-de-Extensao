package br.com.revgas.chatbot.integration.erp.cfop;

/** Optional filters accepted by the supplied RevGas CFOP client sample. */
public record CfopFilter(
        Integer page,
        Integer pageSize,
        String code,
        String description,
        Boolean departure,
        Boolean incoming,
        Boolean refund,
        Boolean fuel
) {
    public CfopFilter {
        if (page != null && page < 1) throw new IllegalArgumentException("page deve ser maior que zero");
        if (pageSize != null && pageSize < 1) throw new IllegalArgumentException("pageSize deve ser maior que zero");
        if (departure != null && incoming != null) {
            throw new IllegalArgumentException("departure e incoming não podem ser usados juntos");
        }
    }

    public CfopFilter withPage(int value) {
        return new CfopFilter(value, pageSize, code, description, departure, incoming, refund, fuel);
    }
}
