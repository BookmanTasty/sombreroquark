package com.leyvadev.sombreroquark.clients;


import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@ApplicationScoped
public class EmailTemplateClient {

    public String downloadTemplate(String url) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        try {
            return httpClient.send(HttpRequest.newBuilder()
                                    .uri(URI.create(url))
                                    .build(),
                            HttpResponse.BodyHandlers.ofString())
                    .body();
        }catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error downloading template";
        }
    }
}
