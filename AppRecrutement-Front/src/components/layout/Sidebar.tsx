import { Link, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  Briefcase,
  FileText,
  LogOut,
  Building2,
  User,
  Users,
  Settings
} from 'lucide-react';

interface SidebarProps {
  role?: string;
}

export default function Sidebar({ role = 'CANDIDAT' }: SidebarProps) {
  const location = useLocation();

  const candidateLinks = [
    { name: 'Dashboard', path: '/dashboard/candidat', icon: LayoutDashboard },
    { name: 'Voir les offres', path: '/dashboard/candidat/offres', icon: Briefcase },
    { name: 'Mes candidatures', path: '/dashboard/candidat/candidatures', icon: FileText },
    { name: 'Mon Profil', path: '/dashboard/candidat/profil', icon: User },
  ];

  const recruiterLinks = [
    { name: 'Dashboard', path: '/dashboard/recruteur', icon: LayoutDashboard },
    { name: 'Mes Offres', path: '/dashboard/recruteur/offres', icon: Briefcase },
    { name: 'Candidatures Reçues', path: '/dashboard/recruteur/candidatures', icon: FileText },
    { name: 'Mon Profil', path: '/dashboard/recruteur/profil', icon: User },
  ];

  const adminLinks = [
    { name: 'Dashboard', path: '/admin/dashboard', icon: LayoutDashboard },
    { name: 'Utilisateurs', path: '/admin/users', icon: Users },
    { name: 'Paramètres', path: '/admin/settings', icon: Settings },
  ];

  const links = role === 'CANDIDAT' ? candidateLinks : role === 'RECRUTEUR' ? recruiterLinks : adminLinks;

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    window.location.href = '/login';
  };

  return (
    <aside className="fixed left-0 top-0 h-screen w-64 bg-[#1E293B] text-white flex flex-col shadow-xl z-50">
      <div className="p-6 border-b border-[#334155]">
        <h1 className="text-2xl font-bold flex items-center gap-2">
          <Building2 className="w-8 h-8 text-[#3B82F6]" />
          Linkia
        </h1>
      </div>

      <nav className="flex-1 p-4 overflow-y-auto">
        <ul className="space-y-2">
          {links.map((link) => {
            const Icon = link.icon;
            const isActive = location.pathname === link.path;
            return (
              <li key={link.name}>
                <Link
                  to={link.path}
                  className={`flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 ${
                    isActive
                      ? 'bg-[#3B82F6] text-white shadow-md'
                      : 'text-gray-300 hover:bg-[#334155] hover:text-white'
                  }`}
                >
                  <Icon className="w-5 h-5 flex-shrink-0" />
                  <span className="font-medium">{link.name}</span>
                </Link>
              </li>
            );
          })}
        </ul>
      </nav>

      <div className="p-4 border-t border-[#334155]">
        <button
          onClick={handleLogout}
          className="flex items-center gap-3 w-full px-4 py-3 rounded-lg text-gray-300 hover:bg-[#334155] hover:text-white transition-all duration-200"
        >
          <LogOut className="w-5 h-5 flex-shrink-0" />
          <span className="font-medium">Déconnexion</span>
        </button>
      </div>
    </aside>
  );
}
