import React from 'react';
import { useApp } from '../context/AppContext';

export const GradeSelector: React.FC = () => {
  const { selectedGrade, setSelectedGrade } = useApp();
  const grades = [6, 7, 8, 9, 10, 11, 12];

  return (
    <div className="flex items-center gap-1.5 overflow-x-auto pb-1 scrollbar-none">
      <span className="text-xs font-bold text-[#778DA9] mr-1 whitespace-nowrap uppercase tracking-wider">
        Khối:
      </span>
      {grades.map((g) => {
        const isSelected = selectedGrade === g;
        return (
          <button
            key={g}
            onClick={() => setSelectedGrade(g)}
            className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all shrink-0 cursor-pointer ${
              isSelected
                ? 'bg-[#00E5FF] text-[#0D1B2A] shadow-lg shadow-[#00E5FF]/20 scale-105'
                : 'bg-[#1E2D40] text-[#ADB5BD] hover:bg-[#27384E] hover:text-white border border-[#2D3F56]/60'
            }`}
          >
            Lớp {g}
          </button>
        );
      })}
    </div>
  );
};
