package com.libertyfianzas.docusign.controllers;

import com.libertyfianzas.docusign.requests.WebhookEventRequest;
import com.libertyfianzas.docusign.services.DocuSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("ds/webhook")
public class WebhookController {
    @Autowired
    private DocuSignService docuSignService;

    @PostMapping
    public ResponseEntity<Void> webhook(@Valid @RequestBody WebhookEventRequest webhookEventRequest, BindingResult bindingResult) throws IllegalStateException {
        if (bindingResult.hasErrors())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bindingResult.getFieldError().getDefaultMessage(), null);

        docuSignService.webhookEventHandle(webhookEventRequest);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
