# OAuth2 Client Backend Spring Web App

Spring Boot web app configured as an OAuth2 Client with two registrations:

- `algashop-ecommerce-web` (`authorization_code`)
- `algashop-ecommerce-m2m` (`client_credentials`)

## Configuration applied

- Application port: `9080`
- Authorization Server issuer: `http://algashop-authorization-server:8081`
- Requested scopes:
  - Web client: all scopes registered for this client
  - M2M client: all scopes registered for this client
- Products API base URL for M2M calls: `http://localhost:8083`

The properties are in `src/main/resources/application.yaml`.

`post_logout_redirect_uri` configured: `http://algashop-ecommerce:9080?logout-success`

## Endpoints

- `GET /` -> public Thymeleaf homepage
- `GET /protected-resource` -> protected page that requires OAuth2 login
- `GET /test-client-credentials` -> public page that fetches an M2M token and calls `GET /api/v1/products`
- `GET /oauth2/authorization/algashop-ecommerce-web` -> starts the web OAuth2 login flow
- `GET /login` -> Login page with a link to start the web OAuth2 login flow
- `POST /logout` -> starts OIDC logout with post-logout redirect

