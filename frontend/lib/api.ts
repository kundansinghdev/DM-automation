import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8000';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface ReelConfig {
  trigger_keyword: string;
  dm_message: string;
  comment_reply: string;
  active: boolean;
}

export interface Reel {
  id: string;
  thumbnail_url: string;
  permalink: string;
  caption: string;
  config: ReelConfig;
}

export interface Stats {
  total_reels: number;
  configured: number;
  using_default: number;
}

export const instagramApi = {
  getReels: () => api.get<{ reels: Reel[] }>('/api/reels').then(res => res.data.reels),
  getStats: () => api.get<Stats>('/api/stats').then(res => res.data),
  updateReel: (id: string, config: ReelConfig) => api.put(`/api/reels/${id}`, config),
};

export default api;
