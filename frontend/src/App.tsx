import { Navigate, Route, Routes } from "react-router-dom";
import { Layout } from "./components/Layout";
import { RequireAuth } from "./auth/RequireAuth";
import { LibraryPage } from "./pages/LibraryPage";
import { GameDetailPage } from "./pages/GameDetailPage";
import { AccountsPage } from "./pages/AccountsPage";

function App() {
  return (
    <RequireAuth>
      <Layout>
        <Routes>
          <Route path="/" element={<Navigate to="/library" replace />} />
          <Route path="/library" element={<LibraryPage />} />
          <Route path="/games/:gameId" element={<GameDetailPage />} />
          <Route path="/accounts" element={<AccountsPage />} />
        </Routes>
      </Layout>
    </RequireAuth>
  );
}

export default App;
