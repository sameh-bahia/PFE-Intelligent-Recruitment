export default function Logo({ className = "" }: { className?: string }) {
  return (
    <div className={`flex items-center gap-3 ${className}`} style={{ fontFamily: 'Outfit, sans-serif' }}>
      <svg
        width="48"
        height="48"
        viewBox="0 0 48 48"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        className="flex-shrink-0 animate-float"
      >
        <defs>
          <linearGradient id="logoGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stopColor="#60A5FA" />
            <stop offset="100%" stopColor="#1E40AF" />
          </linearGradient>
        </defs>
        
        {/* Main shape with fluid lines */}
        <path
          d="M8 24C8 14.0589 16.0589 6 26 6C35.9411 6 44 14.0589 44 24C44 33.9411 35.9411 42 26 42H8V24Z"
          fill="url(#logoGradient)"
          opacity="0.9"
        />
        
        {/* Fluid wave accent */}
        <path
          d="M12 24C12 16.268 18.268 10 26 10C33.732 10 40 16.268 40 24C40 31.732 33.732 38 26 38"
          stroke="white"
          strokeWidth="2"
          strokeLinecap="round"
          fill="none"
          opacity="0.6"
        />
        
        {/* Check mark integrated for "Matching" */}
        <path
          d="M18 24L23 29L34 18"
          stroke="white"
          strokeWidth="3"
          strokeLinecap="round"
          strokeLinejoin="round"
          fill="none"
        />
        
        {/* Small accent dots for dynamic effect */}
        <circle cx="30" cy="32" r="1.5" fill="white" opacity="0.5" />
        <circle cx="34" cy="28" r="1" fill="white" opacity="0.4" />
      </svg>
      
      <span className="text-2xl">
        <span className="font-bold text-[#1E40AF]">Link</span>
        <span className="font-light text-[#60A5FA]">ia</span>
      </span>
    </div>
  );
}
