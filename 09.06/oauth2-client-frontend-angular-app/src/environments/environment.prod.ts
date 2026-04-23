import { AuthConfig } from 'angular-oauth2-oidc';
import { OAuthModuleConfig } from 'angular-oauth2-oidc';

// OAuth/OIDC Configuration for production
// Configure with your actual production authorization server and application URLs
export const authConfig: AuthConfig = {
  issuer: 'https://auth.algashop.com',
  redirectUri: 'https://admin.algashop.com',
  silentRefreshRedirectUri: 'https://admin.algashop.com/silent-refresh.html',
  clientId: 'algashop-admin-web',
  responseType: 'code',
  scope: 'openid products:read products:write products:stock:write categories:read categories:write invoices:read orders:read customers:read shopping-carts:read users:read users:write',
  useSilentRefresh: true, // Needed for Code Flow to suggest using iframe-based refreshes
  silentRefreshTimeout: 5000,
  timeoutFactor: 0.25,
  sessionChecksEnabled: false, // Can turn this to true but it may cause CSP issues
  showDebugInformation: false, // Set to false in production
  clearHashAfterLogin: true,
  nonceStateSeparator: 'semicolon',
  oidc: true,
  disablePKCE: false,
  requireHttps: true, // Required in production
};

// Resource Server Configuration for production
export const authModuleConfig: OAuthModuleConfig = {
  resourceServer: {
    allowedUrls: [
      'https://api.algashop.com/api/v1',
      'https://auth.algashop.com/api/v1'
    ],
    sendAccessToken: true,
  }
};

export const environment = {
  production: true,
  authConfig,
  authModuleConfig
};
