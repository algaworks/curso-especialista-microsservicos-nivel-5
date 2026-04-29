package com.algaworks.algashop.authorizationserver.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
@RequiredArgsConstructor
public class OAuth2TokenCustomizerConfig {

	private final OidcUserInfoService oidcUserInfoService;

	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
		return context -> {
			String tokenType = context.getTokenType().getValue();
			if (OidcParameterNames.ID_TOKEN.equals(tokenType)) {
				OidcUserInfo oidcUserInfo = loadUserInfo(context);
				context.getClaims().claims(claims -> claims.putAll(oidcUserInfo.getClaims()));
			} else if (OAuth2TokenType.ACCESS_TOKEN.getValue().equals(tokenType)
				&& AuthorizationGrantType.AUTHORIZATION_CODE.equals(context.getAuthorizationGrantType())) {
				OidcUserInfo oidcUserInfo = loadUserInfo(context);
				context.getClaims().subject(oidcUserInfo.getSubject());
			}
		};
	}

	private OidcUserInfo loadUserInfo(JwtEncodingContext context) {
		String email = context.getPrincipal().getName();
		return oidcUserInfoService.loadUser(email);
	}

}
