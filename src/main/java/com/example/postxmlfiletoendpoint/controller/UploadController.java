package com.example.postxmlfiletoendpoint.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UploadController {

    @PostMapping(value = "/uploadXml-FileToEndpoint", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> fileUpload(@RequestParam("file") MultipartFile xmlFile) {

        try {
            // Omvandlar MultipartFile till XML-sträng
            String xmlString = new String(xmlFile.getBytes());

            sendFile(xmlString);

            return ResponseEntity.status(HttpStatus.OK).body("The XML file has been successfully received and sent!");
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    public void sendFile(String xmlString) {
        WebClient webClient = WebClient.builder().baseUrl("http://destination.com/api").build();

        // Skickar XML data
        Mono<String> response = webClient.post()
                .uri("/upload")
                .contentType(MediaType.APPLICATION_XML)
                .body(Mono.just(xmlString), String.class)
                .retrieve()
                .bodyToMono(String.class);

        try {
            response.block();
        } catch(Exception e) {
            throw new RuntimeException("Failed to send XML file: " + e.getMessage()); // Kastar error uppåt så vår controller kan fanga den
        }
    }
}
