import React from 'react';
import { Reel } from '../lib/api';

interface ReelCardProps {
  reel: Reel;
  onClick: (reel: Reel) => void;
}

const ReelCard: React.FC<ReelCardProps> = ({ reel, onClick }) => {
  return (
    <div 
      className="group relative cursor-pointer transform transition-all duration-300 hover:scale-105 hover:z-10"
      onClick={() => onClick(reel)}
    >
      <div className="absolute inset-0 bg-gradient-to-r from-pink-500 to-purple-500 rounded-2xl blur opacity-0 group-hover:opacity-75 transition"></div>
      <div className="relative glass rounded-2xl overflow-hidden border border-white/20">
        <div className="aspect-[9/16] bg-gradient-to-br from-gray-800 to-gray-900 relative overflow-hidden">
          <img 
            src={reel.thumbnail_url} 
            alt="Reel" 
            className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110" 
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity"></div>
          
          {reel.config.active ? (
            <div className="absolute top-3 right-3 bg-gradient-to-r from-green-400 to-emerald-500 text-white text-xs font-bold px-3 py-1.5 rounded-full shadow-lg flex items-center gap-1">
              <span className="w-2 h-2 bg-white rounded-full animate-pulse"></span>
              Active
            </div>
          ) : (
            <div className="absolute top-3 right-3 bg-gray-600/80 text-white text-xs font-bold px-3 py-1.5 rounded-full">
              Inactive
            </div>
          )}
          
          <div className="absolute bottom-0 left-0 right-0 p-4 transform translate-y-full group-hover:translate-y-0 transition-transform">
            <div className="bg-white/10 backdrop-blur-md rounded-lg p-2 border border-white/20">
              <p className="text-white text-xs font-medium text-center">Click to configure</p>
            </div>
          </div>
        </div>
        
        <div className="p-4 bg-gradient-to-br from-gray-900/50 to-gray-800/50">
          <p className="text-white text-sm font-medium truncate mb-2">{reel.caption || 'No caption'}</p>
          <div className="flex items-center gap-2">
            <span className="text-pink-400 text-xs text-sm">🔑</span>
            <p className="text-white/70 text-xs truncate flex-1">{reel.config.trigger_keyword}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ReelCard;
