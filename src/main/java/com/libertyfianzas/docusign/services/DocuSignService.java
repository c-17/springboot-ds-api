package com.libertyfianzas.docusign.services;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.model.*;

import com.libertyfianzas.docusign.requests.CreateEnvelopeRequest;
import com.libertyfianzas.docusign.requests.CreateEnvelopeSignatureRequest;
import com.libertyfianzas.docusign.requests.CreateEnvelopeSignerRequest;
import com.libertyfianzas.docusign.requests.WebhookEventRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.IntStream;

@Service
public class DocuSignService {
    @Value("${docusign.integrationKey}")
    private String integrationKey;

    @Value("${docusign.userId}")
    private String userId;

    @Value("${docusign.accountId}")
    private String accountId;

    private ApiClient createApiClient() throws IOException, ApiException {
        ApiClient apiClient = new ApiClient("https://demo.docusign.net/restapi");

        apiClient.setOAuthBasePath("account-d.docusign.com");

        ArrayList scopes = new ArrayList<String>(Arrays.asList("signature", "impersonation"));

        Resource resource = new ClassPathResource("private.key");
        FileInputStream file = new FileInputStream(resource.getFile());

        OAuth.OAuthToken oAuthToken = apiClient.requestJWTUserToken(integrationKey, userId, scopes, file.readAllBytes(), 900);

        apiClient.addDefaultHeader("Authorization", "Bearer " + oAuthToken.getAccessToken());

        return apiClient;
    }

    public String getEnvelope(String envelopeID) throws IOException, ApiException {
        EnvelopesApi envelopesApi = new EnvelopesApi(createApiClient());

        Envelope envelope = envelopesApi.getEnvelope(accountId, envelopeID);

        byte[] data = envelopesApi.getDocument(accountId, envelopeID, "1");

        return Base64.getEncoder().encodeToString(data);
    }

    public String createEnvelope(CreateEnvelopeRequest envelopeRequest) throws IOException, ApiException {
        EnvelopesApi envelopesApi = new EnvelopesApi(createApiClient());

        EnvelopeDefinition envelope = new EnvelopeDefinition();

        envelope.setEmailSubject(envelopeRequest.emailSubject);

        envelope.setStatus("sent");

        Document document = new Document();
        document.setDocumentBase64(envelopeRequest.documentBase64);
        document.setName(envelopeRequest.documentName);
        document.setFileExtension("pdf");
        document.setDocumentId("1");

        envelope.setDocuments(Arrays.asList(document));

        ArrayList<Signer> signers = new ArrayList<>();

        for (int i = 0; i < envelopeRequest.signersRequest.length; i++) {
            CreateEnvelopeSignerRequest signerRequest = envelopeRequest.signersRequest[i];

            ArrayList<SignHere> signatures = new ArrayList<>();

            for (CreateEnvelopeSignatureRequest signatureRequest : signerRequest.signaturesRequest) {
                SignHere signHere = new SignHere();
                signHere.setDocumentId("1");
                signHere.setPageNumber(signatureRequest.page);
                signHere.setXPosition(signatureRequest.x);
                signHere.setYPosition(signatureRequest.y);
                signHere.scaleValue("2.0");

                signatures.add(signHere);
            }

            Tabs tabs = new Tabs();
            tabs.setSignHereTabs(signatures);

            Signer signer = new Signer();
            signer.setEmail(signerRequest.email);
            signer.setName(signerRequest.fullName);
            signer.recipientId(Integer.toString(i + 1));
            signer.setTabs(tabs);

            signers.add(signer);
        }

        Recipients recipients = new Recipients();
        recipients.setSigners(signers);

        envelope.setRecipients(recipients);

        return envelopesApi.createEnvelope(accountId, envelope).getEnvelopeId();
    }

    public void webhookEventHandle(WebhookEventRequest webhookEventRequest) {
        System.out.println("Event: " + webhookEventRequest.getEvent() + " envelopeId: " + webhookEventRequest.getWebhookEventDataRequest().getEnvelopeId());
    }
}
