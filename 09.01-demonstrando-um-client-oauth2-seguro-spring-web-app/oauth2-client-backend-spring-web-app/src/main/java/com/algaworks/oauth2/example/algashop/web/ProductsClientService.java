package com.algaworks.oauth2.example.algashop.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Set;

@Service
public class ProductsClientService {

    private static final String M2M_REGISTRATION_ID = "algashop-ecommerce-m2m";
    private static final String M2M_PRINCIPAL = "m2m-client";

    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final RestClient restClient;

    public ProductsClientService(
        OAuth2AuthorizedClientManager authorizedClientManager,
        RestClient.Builder restClientBuilder,
        @Value("${app.products-api.base-url:http://localhost:8083}") String productsApiBaseUrl
    ) {
        this.authorizedClientManager = authorizedClientManager;
        this.restClient = restClientBuilder.baseUrl(productsApiBaseUrl).build();
    }

    public Response fetchProductsUsingClientCredentials() {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(M2M_REGISTRATION_ID)
            .principal(M2M_PRINCIPAL)
            .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
            return Response.error("Nao foi possivel obter access token para o client m2m.");
        }

        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        Set<String> scopes = authorizedClient.getAccessToken().getScopes();

        try {
            String productsResponse = restClient.get()
                .uri("/api/v1/products")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(String.class);

            return Response.success(accessToken, scopes, productsResponse);
        } catch (Exception ex) {
            return Response.error("Falha ao chamar API de produtos: " + ex.getMessage());
        }
    }

    public record Response(String accessToken, Set<String> scopes, String productsResponse, String errorMessage) {

        static Response success(String accessToken, Set<String> scopes, String productsResponse) {
            return new Response(accessToken, scopes, productsResponse, null);
        }

        static Response error(String errorMessage) {
            return new Response(null, Set.of(), null, errorMessage);
        }

        public boolean hasError() {
            return errorMessage != null;
        }
    }
}

