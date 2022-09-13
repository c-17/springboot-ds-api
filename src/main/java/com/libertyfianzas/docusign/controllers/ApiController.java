package com.libertyfianzas.docusign.controllers;

import com.libertyfianzas.docusign.requests.CreateEnvelopeRequest;
import com.libertyfianzas.docusign.services.DocuSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("ds/api")
public class ApiController {
    @Autowired
    private DocuSignService docuSignService;

    @GetMapping("{envelope_id}")
    public ResponseEntity<String> getEnvelope(@PathVariable("envelope_id") String envelopeId) {
        try {
            return new ResponseEntity<String>(docuSignService.getEnvelope(envelopeId), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping
    public ResponseEntity<String> createEnvelope(@Valid @RequestBody CreateEnvelopeRequest envelopeRequest, BindingResult bindingResult) throws IllegalStateException {
        if (bindingResult.hasErrors())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bindingResult.getFieldError().getDefaultMessage(), null);

        try {
            return new ResponseEntity<String>(docuSignService.createEnvelope(envelopeRequest), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
