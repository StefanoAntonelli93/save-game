import { useState } from "react";
import type { Provider } from "../api/types";
import { useLinkAccount, useLinkedAccounts, useSyncAccount, useUnlinkAccount } from "../api/useAccounts";

const PROVIDERS: { value: Provider; label: string; note?: string }[] = [
  { value: "STEAM", label: "Steam" },
  { value: "XBOX", label: "Xbox", note: "sync not implemented yet" },
  { value: "PSN", label: "PlayStation", note: "experimental — unofficial API" },
];

export function AccountsPage() {
  const { data: accounts } = useLinkedAccounts();
  const linkAccount = useLinkAccount();
  const unlinkAccount = useUnlinkAccount();
  const syncAccount = useSyncAccount();

  const [provider, setProvider] = useState<Provider>("STEAM");
  const [externalId, setExternalId] = useState("");
  const [credentials, setCredentials] = useState("");

  return (
    <div className="accounts-page">
      <h1>Linked accounts</h1>
      <p className="muted">
        Steam uses your SteamID64 + a Steam Web API key. Xbox and PlayStation are not fully wired up yet — see the
        backend README for status.
      </p>

      <div className="link-form">
        <select value={provider} onChange={(e) => setProvider(e.target.value as Provider)}>
          {PROVIDERS.map((p) => (
            <option key={p.value} value={p.value}>
              {p.label}
              {p.note ? ` (${p.note})` : ""}
            </option>
          ))}
        </select>
        <input placeholder="External ID (e.g. SteamID64)" value={externalId} onChange={(e) => setExternalId(e.target.value)} />
        <input placeholder="Credentials (e.g. API key)" value={credentials} onChange={(e) => setCredentials(e.target.value)} />
        <button
          className="btn-primary"
          onClick={() => {
            linkAccount.mutate({ provider, request: { externalId, credentials } });
            setExternalId("");
            setCredentials("");
          }}
        >
          Link account
        </button>
      </div>

      <div className="account-list">
        {accounts?.map((account) => (
          <div className="account-row" key={account.id}>
            <span className="account-provider">{account.provider}</span>
            <span>{account.externalId}</span>
            <span className={`account-status account-status-${account.status.toLowerCase()}`}>{account.status}</span>
            <span className="muted">{account.lastSyncedAt ? new Date(account.lastSyncedAt).toLocaleString() : "never synced"}</span>
            <button className="btn-ghost" onClick={() => syncAccount.mutate(account.id)}>
              Sync now
            </button>
            <button className="btn-ghost btn-danger" onClick={() => unlinkAccount.mutate(account.id)}>
              Unlink
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
