import type { PropsWithChildren } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "react-oidc-context";

export function Layout({ children }: PropsWithChildren) {
  const auth = useAuth();

  return (
    <div className="app-shell">
      <header className="top-bar">
        <div className="top-bar-inner">
          <div className="brand">
            <span className="brand-dot" />
            <span>SAVE·STATE</span>
          </div>
          <nav className="nav-links">
            <Link to="/library">Library</Link>
            <Link to="/accounts">Accounts</Link>
          </nav>
          {auth.isAuthenticated && (
            <button className="btn-ghost" onClick={() => auth.signoutRedirect()}>
              Sign out
            </button>
          )}
        </div>
      </header>
      <main className="content">{children}</main>
    </div>
  );
}
