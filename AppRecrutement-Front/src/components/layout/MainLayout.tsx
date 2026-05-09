import { type ReactNode } from 'react';
import Sidebar from './Sidebar';
import Navbar from './Navbar';

interface MainLayoutProps {
  children: ReactNode;
  role?: string;
  userName?: string;
}

export default function MainLayout({ children, role = 'CANDIDAT', userName = 'Utilisateur' }: MainLayoutProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC] flex">
      <Sidebar role={role} />
      <div className="ml-64 min-h-screen flex-1 flex flex-col">
        <Navbar userName={userName} userRole={role} />
        <main className="p-8 flex-1">
          {children}
        </main>
      </div>
    </div>
  );
}
