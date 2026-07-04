import type { PropsWithChildren } from "react";
import { useAuth } from "react-oidc-context";

export function RequireAuth({ children }: PropsWithChildren) {
  const auth = useAuth();

  if (auth.isLoading) {
    return <div className="centered-message">Loading…</div>;
  }

  if (auth.error) {
    return <div className="centered-message">Authentication error: {auth.error.message}</div>;
  }

  if (!auth.isAuthenticated) {
    return (
      <div className="centered-message">
        <button className="btn-primary" onClick={() => auth.signinRedirect()}>
          Sign in
        </button>
      </div>
    );
  }

  return <>{children}</>;
}
