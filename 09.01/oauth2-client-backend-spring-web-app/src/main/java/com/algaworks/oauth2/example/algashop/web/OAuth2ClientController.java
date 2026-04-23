package com.algaworks.oauth2.example.algashop.web;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2ClientController {

    private final ProductsClientService productsClientService;

    public OAuth2ClientController(ProductsClientService productsClientService) {
        this.productsClientService = productsClientService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        boolean authenticated = authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);

        model.addAttribute("authenticated", authenticated);
        model.addAttribute("username", authenticated ? authentication.getName() : null);
        return "home";
    }

    @GetMapping("/protected-resource")
    public String protectedPage(
        Model model,
        @AuthenticationPrincipal OidcUser oidcUser,
        @RegisteredOAuth2AuthorizedClient("algashop-ecommerce-web") OAuth2AuthorizedClient authorizedClient
    ) {
        model.addAttribute("username", oidcUser.getFullName() != null ? oidcUser.getFullName() : oidcUser.getName());
        model.addAttribute("idToken", oidcUser.getIdToken().getTokenValue());
        model.addAttribute("accessToken", authorizedClient.getAccessToken().getTokenValue());
        model.addAttribute("scopes", authorizedClient.getAccessToken().getScopes());
        return "protected-resource";
    }

    @GetMapping("/test-client-credentials")
    public String testClientCredentials(Model model) {
        ProductsClientService.Response result = productsClientService.fetchProductsUsingClientCredentials();

        model.addAttribute("hasError", result.hasError());
        model.addAttribute("errorMessage", result.errorMessage());
        model.addAttribute("accessToken", result.accessToken());
        model.addAttribute("scopes", result.scopes());
        model.addAttribute("productsResponse", result.productsResponse());

        return "test-client-credentials";
    }
}

