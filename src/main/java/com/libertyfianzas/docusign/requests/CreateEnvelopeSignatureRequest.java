package com.libertyfianzas.docusign.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Data
public class CreateEnvelopeSignatureRequest {
    @NotEmpty(message = "the page field from signature must not be empty")
    @JsonAlias("page")
    public String page;

    @NotEmpty(message = "the x field from signature must not be empty")
    @JsonAlias("x")
    public String x;

    @NotEmpty(message = "the y field from signature must not be empty")
    @JsonAlias("y")
    public String y;
}
