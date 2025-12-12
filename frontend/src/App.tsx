import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/layout';
import { AuthProvider, useAuth } from './contexts/AuthContext';

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

// 라우트를 별도 컴포넌트로 분리 (AuthProvider 내부에서 useAuth 사용)
function AppRoutes() {
  const { user, loading, logout } = useAuth();

  // 로딩 중일 때 표시
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-bg-gray">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-sale-red mx-auto"></div>
          <p className="mt-4 text-text-secondary">로딩 중...</p>
        </div>
      </div>
    );
  }

  return (
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
  );
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
