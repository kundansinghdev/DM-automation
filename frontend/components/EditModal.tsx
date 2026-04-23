import React from 'react';
import { Reel, ReelConfig } from '../lib/api';

interface EditModalProps {
  reel: Reel;
  formData: ReelConfig;
  setFormData: (data: ReelConfig) => void;
  onSave: () => void;
  onClose: () => void;
}

const EditModal: React.FC<EditModalProps> = ({ 
  reel, 
  formData, 
  setFormData, 
  onSave, 
  onClose 
}) => {
  return (
    <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fadeIn" onClick={onClose}>
      <div className="relative max-w-2xl w-full" onClick={(e) => e.stopPropagation()}>
        <div className="absolute inset-0 bg-gradient-to-r from-pink-500 via-purple-500 to-indigo-500 rounded-3xl blur-xl opacity-50"></div>
        <div className="relative glass rounded-3xl p-8 border border-white/20 shadow-2xl">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-3xl font-bold bg-gradient-to-r from-pink-400 to-purple-400 bg-clip-text text-transparent">
              Configure Automation
            </h2>
            <button 
              onClick={onClose}
              className="text-white/60 hover:text-white transition-colors"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <div className="space-y-5">
            <div>
              <label className="block text-white font-semibold mb-2 text-sm uppercase tracking-wide">Trigger Keyword</label>
              <input
                type="text"
                value={formData.trigger_keyword}
                onChange={(e) => setFormData({...formData, trigger_keyword: e.target.value})}
                className="w-full px-4 py-3 rounded-xl bg-white/10 text-white placeholder-white/40 border border-white/20 focus:border-pink-400 focus:outline-none focus:ring-2 focus:ring-pink-400/50 transition"
                placeholder="e.g., info, guide, help"
              />
            </div>

            <div>
              <label className="block text-white font-semibold mb-2 text-sm uppercase tracking-wide">DM Message</label>
              <textarea
                value={formData.dm_message}
                onChange={(e) => setFormData({...formData, dm_message: e.target.value})}
                className="w-full px-4 py-3 rounded-xl bg-white/10 text-white placeholder-white/40 border border-white/20 focus:border-purple-400 focus:outline-none focus:ring-2 focus:ring-purple-400/50 transition h-28 resize-none"
                placeholder="Message to send via DM when keyword is detected"
              />
            </div>

            <div>
              <label className="block text-white font-semibold mb-2 text-sm uppercase tracking-wide">Comment Reply</label>
              <textarea
                value={formData.comment_reply}
                onChange={(e) => setFormData({...formData, comment_reply: e.target.value})}
                className="w-full px-4 py-3 rounded-xl bg-white/10 text-white placeholder-white/40 border border-white/20 focus:border-indigo-400 focus:outline-none focus:ring-2 focus:ring-indigo-400/50 transition h-28 resize-none"
                placeholder="Public reply to the comment"
              />
            </div>

            <div className="flex items-center gap-3 p-4 rounded-xl bg-white/5 border border-white/10">
              <input
                type="checkbox"
                checked={formData.active}
                onChange={(e) => setFormData({...formData, active: e.target.checked})}
                className="w-6 h-6 rounded accent-green-500"
              />
              <label className="text-white font-medium">Enable automation for this reel</label>
            </div>
          </div>

          <div className="flex gap-4 mt-8">
            <button
              onClick={onSave}
              className="flex-1 bg-gradient-to-r from-pink-500 to-purple-500 text-white px-6 py-4 rounded-xl font-bold hover:from-pink-600 hover:to-purple-600 transition-all transform hover:scale-105 shadow-lg"
            >
              Save Changes
            </button>
            <button
              onClick={onClose}
              className="flex-1 bg-white/10 text-white px-6 py-4 rounded-xl font-bold hover:bg-white/20 transition-all border border-white/20"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EditModal;
