import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/layout';
import { useAuth } from './hooks/useAuth';

// Pages
import { RoleSelect } from './pages/RoleSelect';
import { Login } from './pages/Login';
import { AdminLogin } from './pages/AdminLogin';
import { Signup } from './pages/Signup';
import { PromotionList } from './pages/PromotionList';
import { PromotionDetail } from './pages/PromotionDetail';
import { Queue } from './pages/Queue';
import { Checkout } from './pages/Checkout';
import { OrderComplete } from './pages/OrderComplete';
import { Orders } from './pages/Orders';
import { AdminDashboard } from './pages/AdminDashboard';

function App() {
  const { user, logout } = useAuth();

  return (
    <BrowserRouter>
      <Routes>
        {/* Role Selection - Entry Point */}
        <Route path="/" element={<RoleSelect />} />

        {/* Public Routes - User */}
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />

        {/* Public Routes - Admin */}
        <Route path="/admin/login" element={<AdminLogin />} />

        {/* Protected Routes - Admin */}
        <Route
          path="/admin/dashboard"
          element={
            user ? <AdminDashboard /> : <Navigate to="/admin/login" replace />
          }
        />

        {/* Protected Routes - User with Layout */}
        <Route
          path="/promotions"
          element={
            user ? (
              <Layout user={user} onLogout={logout}>
                <PromotionList />
              </Layout>
            ) : (
              <Navigate to="/" replace />
            )
          }
        />
        <Route
          path="/promotions/:id"
          element={
            user ? (
              <Layout user={user} onLogout={logout}>
                <PromotionDetail />
              </Layout>
            ) : (
              <Navigate to="/" replace />
            )
          }
        />
        <Route
          path="/queue/:promotionId"
          element={
            user ? (
              <Layout user={user} onLogout={logout}>
                <Queue />
              </Layout>
            ) : (
              <Navigate to="/" replace />
            )
          }
        />
        <Route
          path="/checkout/:promotionId"
          element={
            user ? (
              <Layout user={user} onLogout={logout}>
                <Checkout />
              </Layout>
            ) : (
              <Navigate to="/" replace />
            )
          }
        />
        <Route
          path="/order-complete"
          element={
            user ? (
              <Layout user={user} onLogout={logout}>
                <OrderComplete />
              </Layout>
            ) : (
              <Navigate to="/" replace />
            )
          }
        />
        <Route
          path="/orders"
          element={
            user ? (
              <Layout user={user} onLogout={logout}>
                <Orders />
              </Layout>
            ) : (
              <Navigate to="/" replace />
            )
          }
        />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
