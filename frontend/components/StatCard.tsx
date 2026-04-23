import React from 'react';

interface StatCardProps {
  label: string;
  value: number;
  gradient: string;
}

const StatCard: React.FC<StatCardProps> = ({ label, value, gradient }) => {
  return (
    <div className="group relative">
      <div className={`absolute inset-0 bg-gradient-to-r ${gradient} rounded-2xl blur opacity-75 group-hover:opacity-100 transition`}></div>
      <div className="relative glass rounded-2xl p-8 text-center border border-white/20">
        <div className="text-5xl font-bold text-white mb-2">{value}</div>
        <div className="text-white/80 text-sm uppercase tracking-wider">{label}</div>
      </div>
    </div>
  );
};

export default StatCard;
