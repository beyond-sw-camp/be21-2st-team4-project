import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/layout';
import { useAuth } from './hooks/useAuth';

// Pages
import { Login } from './pages/Login';
import { Signup } from './pages/Signup';
import { PromotionList } from './pages/PromotionList';
import { Queue } from './pages/Queue';

function App() {
  const { user, logout } = useAuth();

  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />

        {/* Protected Routes with Layout */}
        <Route
          path="/"
          element={
            <Layout user={user} onLogout={logout}>
              <Navigate to="/promotions" replace />
            </Layout>
          }
        />
        <Route
          path="/promotions"
          element={
            <Layout user={user} onLogout={logout}>
              <PromotionList />
            </Layout>
          }
        />
        <Route
          path="/queue/:promotionId"
          element={
            <Layout user={user} onLogout={logout}>
              <Queue />
            </Layout>
          }
        />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/promotions" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
