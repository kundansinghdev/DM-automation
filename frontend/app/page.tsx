'use client'

import { useState, useEffect, useCallback } from 'react'
import { instagramApi, Reel, ReelConfig, Stats } from '../lib/api'
import StatCard from '../components/StatCard'
import ReelCard from '../components/ReelCard'
import EditModal from '../components/EditModal'

export default function Dashboard() {
  const [reels, setReels] = useState<Reel[]>([])
  const [stats, setStats] = useState<Stats | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [editingReel, setEditingReel] = useState<Reel | null>(null)
  const [formData, setFormData] = useState<ReelConfig>({
    trigger_keyword: '',
    dm_message: '',
    comment_reply: '',
    active: true
  })

  const fetchData = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const [reelsData, statsData] = await Promise.all([
        instagramApi.getReels(),
        instagramApi.getStats()
      ])
      setReels(reelsData)
      setStats(statsData)
    } catch (err: any) {
      setError(err.message || 'Failed to connect to the backend server.')
      console.error('Data fetch error:', err)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchData()
  }, [fetchData])

  const openEditModal = (reel: Reel) => {
    setEditingReel(reel)
    setFormData(reel.config)
  }

  const handleSave = async () => {
    if (!editingReel) return
    
    try {
      await instagramApi.updateReel(editingReel.id, formData)
      await fetchData()
      setEditingReel(null)
    } catch (err: any) {
      alert('Error updating configuration: ' + err.message)
    }
  }

  if (loading && !reels.length) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-pink-800 flex items-center justify-center">
        <div className="flex flex-col items-center gap-4">
          <div className="w-16 h-16 border-4 border-white/30 border-t-white rounded-full animate-spin"></div>
          <div className="text-white text-xl font-medium">Synchronizing with Instagram...</div>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-pink-800 p-4 md:p-8">
      <div className="max-w-7xl mx-auto">
        
        {/* Header Section */}
        <header className="text-center mb-10">
          <div className="inline-block mb-4 overflow-hidden rounded-2xl shadow-2xl">
            <div className="bg-gradient-to-r from-pink-500 via-purple-500 to-indigo-500 p-1">
              <div className="bg-gray-950 px-8 py-3 rounded-xl">
                <h1 className="text-4xl md:text-5xl font-bold bg-gradient-to-r from-pink-400 via-purple-400 to-indigo-400 bg-clip-text text-transparent">
                  Automate Instagram
                </h1>
              </div>
            </div>
          </div>
        </header>

        {error && (
          <div className="mb-8 bg-red-500/20 border border-red-500/50 rounded-xl p-4 text-red-200 text-center animate-pulse">
            ⚠️ {error}. Please ensure the backend is running on port 8000.
          </div>
        )}

        {/* Stats Grid */}
        {stats && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
            <StatCard 
              label="Total Reels" 
              value={stats.total_reels} 
              gradient="from-blue-500 to-cyan-500" 
            />
            <StatCard 
              label="Active Automations" 
              value={stats.configured} 
              gradient="from-green-500 to-emerald-500" 
            />
            <StatCard 
              label="Running Default" 
              value={stats.using_default} 
              gradient="from-purple-500 to-pink-500" 
            />
          </div>
        )}

        {/* Media Grid */}
        <section className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
          {reels.map((reel) => (
            <ReelCard 
              key={reel.id} 
              reel={reel} 
              onClick={openEditModal} 
            />
          ))}
        </section>

        {/* Configuration Modal */}
        {editingReel && (
          <EditModal 
            reel={editingReel}
            formData={formData}
            setFormData={setFormData}
            onSave={handleSave}
            onClose={() => setEditingReel(null)}
          />
        )}
      </div>
    </div>
  )
}
