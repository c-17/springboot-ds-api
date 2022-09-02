package com.libertyfianzas.docusign.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class WebhookEventDataRequest {
    @JsonAlias("accountId")
    private String accountId;

    @JsonAlias("userId")
    private String userId;

    @JsonAlias("envelopeId")
    private String envelopeId;
}
