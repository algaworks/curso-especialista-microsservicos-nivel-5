package com.algaworks.oauth2.example.algashop;

import java.time.Instant;
import java.util.Set;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

final class TestAuthorizedClients {

    private TestAuthorizedClients() {
    }

    static OAuth2AuthorizedClient webClientWithScopes(String... scopes) {
        Set<String> scopeSet = Set.of(scopes);

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("algashop-ecommerce-web")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .clientId("algashop-ecommerce-web")
            .clientSecret("ecommerce123")
            .authorizationUri("http://algashop-authorization-server:8081/oauth2/authorize")
            .tokenUri("http://algashop-authorization-server:8081/oauth2/token")
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope(scopeSet)
            .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "dummy-token",
            Instant.now(),
            Instant.now().plusSeconds(300),
            scopeSet
        );

        return new OAuth2AuthorizedClient(clientRegistration, "maria", token);
    }
}

