package com.libertyfianzas.docusign.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class WebhookEventRequest {
    @JsonAlias("event")
    private String event;

    @JsonAlias("apiVersion")
    private String apiVersion;

    @JsonAlias("url")
    private String uri;

    @JsonAlias("retryCount")
    private float retryCount;

    @JsonAlias("configurationId")
    private float configurationId;

    @JsonAlias("generatedDateTime")
    private String generatedDateTime;

    @JsonAlias("data")
    private WebhookEventDataRequest webhookEventDataRequest;
}
