import { Bell, Settings } from 'lucide-react';

interface NavbarProps {
  userName?: string;
  userRole?: string;
}

export default function Navbar({ userName = 'Utilisateur', userRole = 'CANDIDAT' }: NavbarProps) {
  return (
    <header className="h-16 bg-white border-b border-[#E2E8F0] shadow-sm flex items-center justify-between px-6 z-40">
      <div className="flex items-center gap-4">
        <h2 className="text-xl font-semibold text-[#1E293B]">
          {userRole === 'CANDIDAT' ? 'Espace Candidat' : 'Espace Recruteur'}
        </h2>
      </div>

      <div className="flex items-center gap-4">
        <button className="p-2 rounded-lg hover:bg-[#F8FAFC] transition-colors">
          <Bell className="w-5 h-5 text-[#334155]" />
        </button>
        <button className="p-2 rounded-lg hover:bg-[#F8FAFC] transition-colors">
          <Settings className="w-5 h-5 text-[#334155]" />
        </button>
        
        <div className="flex items-center gap-3 pl-4 border-l border-[#E2E8F0]">
          <div className="w-10 h-10 rounded-full bg-[#3B82F6] flex items-center justify-center text-white font-semibold">
            {userName.charAt(0).toUpperCase()}
          </div>
          <div className="flex flex-col">
            <span className="text-sm font-medium text-[#1E293B]">{userName}</span>
            <span className="text-xs text-gray-500">{userRole === 'CANDIDAT' ? 'Candidat' : 'Recruteur'}</span>
          </div>
        </div>
      </div>
    </header>
  );
}
