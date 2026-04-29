// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { AuthConfig } from 'angular-oauth2-oidc';
import { OAuthModuleConfig } from 'angular-oauth2-oidc';

// OAuth/OIDC Configuration for development
// Example local development URLs using local domain resolution
export const authConfig: AuthConfig = {
  issuer: 'http://auth.algashop.local:8081',
  redirectUri: 'http://admin.algashop.local:4200',
  silentRefreshRedirectUri: 'http://admin.algashop.local:4200/silent-refresh.html',
  clientId: 'algashop-admin-web',
  responseType: 'code',
  scope: 'openid products:read products:write products:stock:write categories:read categories:write invoices:read orders:read customers:read shopping-carts:read users:read users:write',
  useSilentRefresh: true, // Needed for Code Flow to suggest using iframe-based refreshes
  silentRefreshTimeout: 5000, // For faster testing
  timeoutFactor: 0.25, // For faster testing
  sessionChecksEnabled: false, // Can turn this to true but it may cause CSP issues
  showDebugInformation: true, // Also requires enabling "Verbose" level in devtools
  clearHashAfterLogin: true,
  nonceStateSeparator: 'semicolon',
  oidc: true,
  disablePKCE: false,
  requireHttps: false, // Only for testing. In production, this should be `true`.
};

// Resource Server Configuration for development
export const authModuleConfig: OAuthModuleConfig = {
  resourceServer: {
    allowedUrls: [
      'http://auth.algashop.local:8081/api/v1'
    ],
    sendAccessToken: true,
  }
};

export const environment = {
  production: false,
  authConfig,
  authModuleConfig
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
