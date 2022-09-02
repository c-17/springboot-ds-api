package com.libertyfianzas.docusign.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateEnvelopeSignerRequest {
    @NotEmpty(message = "the email field from signer must not be empty")
    @Email(message = "the email field from signer must be valid email")
    @JsonAlias("email")
    public String email;

    @NotNull(message = "the fullName field from signer must not be null")
    @NotEmpty(message = "the fullName field from signer must not be empty")
    @JsonAlias("fullName")
    public String fullName;

    @Valid
    @NotNull(message = "the signatures field from signer must not be null")
    @NotEmpty(message = "the signatures field from signer must not be empty")
    @JsonAlias("signatures")
    public CreateEnvelopeSignatureRequest[] signaturesRequest;
}
