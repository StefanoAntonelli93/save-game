import type { AuthProviderProps } from "react-oidc-context";

export const oidcConfig: AuthProviderProps = {
  authority: import.meta.env.VITE_KEYCLOAK_AUTHORITY ?? "http://localhost:8081/realms/savestate",
  client_id: import.meta.env.VITE_KEYCLOAK_CLIENT_ID ?? "savestate-frontend",
  redirect_uri: window.location.origin,
  scope: "openid profile email",
  onSigninCallback: () => {
    window.history.replaceState({}, document.title, window.location.pathname);
  },
};
