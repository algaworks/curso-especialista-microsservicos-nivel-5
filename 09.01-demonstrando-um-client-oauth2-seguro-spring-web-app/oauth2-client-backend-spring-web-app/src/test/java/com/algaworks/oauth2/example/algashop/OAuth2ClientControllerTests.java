package com.algaworks.oauth2.example.algashop;

import com.algaworks.oauth2.example.algashop.web.OAuth2ClientController;
import com.algaworks.oauth2.example.algashop.web.ProductsClientService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OAuth2ClientControllerTests {

    @Test
    void shouldRenderHomeView() {
        OAuth2ClientController controller = new OAuth2ClientController(mock(ProductsClientService.class));
        Model model = new ConcurrentModel();

        String viewName = controller.home(
            model,
            new UsernamePasswordAuthenticationToken("maria", "n/a", List.of())
        );

        assertEquals("home", viewName);
        assertEquals(true, model.getAttribute("authenticated"));
        assertEquals("maria", model.getAttribute("username"));
    }

    @Test
    void shouldRenderProtectedViewWithModelData() {
        OAuth2ClientController controller = new OAuth2ClientController(mock(ProductsClientService.class));
        Model model = new ConcurrentModel();
        DefaultOidcUser oidcUser = new DefaultOidcUser(
            List.of(),
            new OidcIdToken(
                "dummy-id-token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of(IdTokenClaimNames.SUB, "maria", "name", "Maria")
            ),
            new OidcUserInfo(Map.of(IdTokenClaimNames.SUB, "maria", "name", "Maria"))
        );

        String viewName = controller.protectedPage(
            model,
            oidcUser,
            TestAuthorizedClients.webClientWithScopes("orders:read", "openid")
        );

        assertEquals("protected-resource", viewName);
        assertEquals("Maria", model.getAttribute("username"));
        assertEquals("dummy-id-token", model.getAttribute("idToken"));
        assertEquals("dummy-token", model.getAttribute("accessToken"));
        Set<?> scopes = (Set<?>) model.getAttribute("scopes");
        assertNotNull(scopes);
        assertTrue(scopes.contains("orders:read"));
    }

    @Test
    void shouldRenderClientCredentialsViewWithSuccessData() {
        ProductsClientService m2mService = mock(ProductsClientService.class);
        OAuth2ClientController controller = new OAuth2ClientController(m2mService);
        Model model = new ConcurrentModel();

        when(m2mService.fetchProductsUsingClientCredentials()).thenReturn(
            new ProductsClientService.Response(
                "m2m-token",
                Set.of("products:read"),
                "[{\"id\":1,\"name\":\"Mouse\"}]",
                null
            )
        );

        String viewName = controller.testClientCredentials(model);

        assertEquals("test-client-credentials", viewName);
        assertEquals(false, model.getAttribute("hasError"));
        assertEquals("m2m-token", model.getAttribute("accessToken"));
        Set<?> scopes = (Set<?>) model.getAttribute("scopes");
        assertNotNull(scopes);
        assertTrue(scopes.contains("products:read"));
        assertEquals("[{\"id\":1,\"name\":\"Mouse\"}]", model.getAttribute("productsResponse"));
    }

    @Test
    void shouldRenderClientCredentialsViewWithErrorData() {
        ProductsClientService m2mService = mock(ProductsClientService.class);
        OAuth2ClientController controller = new OAuth2ClientController(m2mService);
        Model model = new ConcurrentModel();

        when(m2mService.fetchProductsUsingClientCredentials()).thenReturn(
            new ProductsClientService.Response(null, Set.of(), null, "falha na chamada")
        );

        String viewName = controller.testClientCredentials(model);

        assertEquals("test-client-credentials", viewName);
        assertEquals(true, model.getAttribute("hasError"));
        assertEquals("falha na chamada", model.getAttribute("errorMessage"));
    }
}
