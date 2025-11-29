import React from 'react';
import { Header } from './Header';
import { Footer } from './Footer';

export interface LayoutProps {
  children: React.ReactNode;
  user?: {
    id: number;
    name: string;
    balance: number;
  } | null;
  onLogout?: () => void;
}

export const Layout: React.FC<LayoutProps> = ({ children, user, onLogout }) => {
  return (
    <div className="min-h-screen flex flex-col">
      <Header user={user} onLogout={onLogout} />
      <main className="flex-1 bg-white">
        {children}
      </main>
      <Footer />
    </div>
  );
};
