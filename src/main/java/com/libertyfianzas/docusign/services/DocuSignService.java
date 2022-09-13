package com.libertyfianzas.docusign.services;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.SignatureApi;
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
            signer.recipientId((i + 1) + "");
            signer.setTabs(tabs);

            signers.add(signer);
        }

        Recipients recipients = new Recipients();
        recipients.setSigners(signers);

        envelope.setRecipients(recipients);

        EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envelope);

        String envelopeId = envelopeSummary.getEnvelopeId();
        /*ReturnUrlRequest viewRequest = new ReturnUrlRequest();
        viewRequest.setReturnUrl("http://localhost:8080");

        ViewUrl viewUrl = envelopesApi.createSenderView(accountId, envelopeId, viewRequest);

        return viewUrl.getUrl();*/

        RecipientViewRequest viewRequest = new RecipientViewRequest();
        viewRequest.setReturnUrl("http://localhost:8080/state=123");
        viewRequest.setAuthenticationMethod("none");
        //viewRequest.setEmail("Christian.Castellanos@LibertyFianzas.com");
        //viewRequest.setUserName("Chris Cardona");
        viewRequest.setEmail(signers.get(0).getEmail());
        viewRequest.setUserName(signers.get(0).getName());
        //viewRequest.setClientUserId("1000");
        ViewUrl viewUrl = envelopesApi.createRecipientView(accountId, envelopeId, viewRequest);

        /*String pingFrequency = "600";
        viewRequest.setPingFrequency(pingFrequency); // seconds
        viewRequest.setPingUrl(config.getDsPingUrl());*/

        /*ConsoleViewRequest viewRequest = new ConsoleViewRequest();
        viewRequest.setReturnUrl("http://localhost:8080/state=123");

        viewRequest.setEnvelopeId(envelopeId);

        ViewUrl viewUrl = envelopesApi.createConsoleView(accountId, viewRequest);*/

        return viewUrl.getUrl();
    }

    public void webhookEventHandle(WebhookEventRequest webhookEventRequest) {
        System.out.println("Event: " + webhookEventRequest.getEvent() + " envelopeId: " + webhookEventRequest.getWebhookEventDataRequest().getEnvelopeId());
    }
}
