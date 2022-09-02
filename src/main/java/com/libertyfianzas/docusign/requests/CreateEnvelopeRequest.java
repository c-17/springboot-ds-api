package com.libertyfianzas.docusign.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Data
public class CreateEnvelopeRequest {
    @NotEmpty(message = "the emailSubject field must not be empty")
    @JsonAlias("emailSubject")
    public String emailSubject;

    @NotEmpty(message = "the documentName field must not be empty")
    @JsonAlias("documentName")
    public String documentName;

    @NotEmpty(message = "the documentBase64 field must not be empty")
    @JsonAlias("documentBase64")
    public String documentBase64;

    @Valid
    @NotNull(message = "the signers field must not be null")
    @NotEmpty(message = "the signers field must not be empty")
    @JsonAlias("signers")
    public CreateEnvelopeSignerRequest[] signersRequest;
}
