package com.example.postxmlfiletoendpoint.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UploadController {

    @PostMapping(value = "/uploadXml-FileToEndpoint", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> uploadFile(@RequestBody String xmlFile) {
        try {
            System.out.println("Received XML file: " + xmlFile);

            sendFile(xmlFile);

            return ResponseEntity.status(HttpStatus.OK).body("The XML file has been successfully received and sent!");
        } catch(Exception e){
            System.err.println("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    public void sendFile(String xmlString) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8081/api").build();

        System.out.println("Sending XML file to the destination endpoint...");

        Mono<String> response = webClient.post()
                .uri("/receive")
                .contentType(MediaType.APPLICATION_XML)
                .body(Mono.just(xmlString), String.class)
                .retrieve()
                .bodyToMono(String.class);

        try {
            String serverResponse = response.block();
            System.out.println("Server response: " + serverResponse);
            System.out.println("Successfully sent the XML file!");
        } catch(Exception e) {
            System.err.println("Failed to send XML file: " + e.getMessage());
            throw new RuntimeException("Failed to send XML file: " + e.getMessage());
        }
    }
}