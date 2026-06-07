'use client'

import { useState, useEffect } from 'react'
import { Stock, StockCategory } from '@/types'
import { getStocks, createStock, updateStock, useStock, restoreStock, deleteStock } from '@/lib/apiClient'

const CATEGORIES: { value: StockCategory; label: string }[] = [
  { value: 'rice',      label: 'ご飯' },
  { value: 'main_dish', label: 'おかず' },
  { value: 'side_dish', label: '副菜' },
  { value: 'noodle',    label: '麺類' },
]

interface FormState { name: string; category: StockCategory }
const EMPTY: FormState = { name: '', category: 'side_dish' }

export default function StocksPage() {
  const [stocks, setStocks]   = useState<Stock[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError]     = useState<string | null>(null)
  const [modal, setModal]     = useState(false)
  const [form, setForm]       = useState<FormState>(EMPTY)
  const [saving, setSaving]   = useState(false)

  const load = async () => {
    try {
      setStocks(await getStocks(true))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'ストックの取得に失敗しました')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const handleAdd = async () => {
    if (!form.name.trim()) return
    setSaving(true)
    setError(null)
    try {
      const created = await createStock({ name: form.name.trim(), category: form.category })
      setStocks((prev) => [...prev, created])
      setModal(false)
      setForm(EMPTY)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : '追加に失敗しました')
    } finally {
      setSaving(false)
    }
  }

  const handleUse = async (id: number) => {
    try {
      const updated = await useStock(id)
      setStocks((prev) => prev.map((s) => s.id === id ? updated : s))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : '操作に失敗しました')
    }
  }

  const handleRestore = async (id: number) => {
    try {
      const updated = await restoreStock(id)
      setStocks((prev) => prev.map((s) => s.id === id ? updated : s))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : '操作に失敗しました')
    }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('このストックを削除しますか？')) return
    try {
      await deleteStock(id)
      setStocks((prev) => prev.filter((s) => s.id !== id))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : '削除に失敗しました')
    }
  }

  return (
    <div>
      <div className="bg-emerald-500 text-white px-4 pt-10 pb-4 flex justify-between items-end">
        <h1 className="text-lg font-bold">ストック</h1>
        <button onClick={() => { setForm(EMPTY); setModal(true) }} className="bg-white text-emerald-600 font-bold px-4 py-2 rounded-lg text-sm">
          + 追加
        </button>
      </div>

      {error && <div className="mx-4 mt-3 p-3 bg-red-50 text-red-600 text-sm rounded-lg">{error}</div>}

      {loading ? (
        <div className="p-8 text-center text-gray-400">読み込み中...</div>
      ) : (
        <div className="px-4 py-4 space-y-6">
          {CATEGORIES.map(({ value, label }) => {
            const group = stocks.filter((s) => s.category === value)
            return (
              <section key={value}>
                <h2 className="text-xs font-bold text-gray-400 uppercase tracking-wide mb-2">{label}</h2>
                {group.length === 0 ? (
                  <p className="text-sm text-gray-300 pl-1">登録なし</p>
                ) : (
                  <ul className="space-y-2">
                    {group.map((s) => (
                      <li key={s.id} className={`flex items-center bg-white border rounded-xl px-4 py-3 shadow-sm ${s.isAvailable ? 'border-gray-100' : 'border-gray-100 opacity-50'}`}>
                        <span className={`flex-1 text-sm font-medium ${s.isAvailable ? '' : 'line-through text-gray-400'}`}>
                          {s.name}
                        </span>
                        {s.isAvailable ? (
                          <button onClick={() => handleUse(s.id)} className="text-xs text-amber-500 font-semibold mr-3">使った</button>
                        ) : (
                          <button onClick={() => handleRestore(s.id)} className="text-xs text-emerald-500 font-semibold mr-3">戻す</button>
                        )}
                        <button onClick={() => handleDelete(s.id)} className="text-xs text-red-400 font-semibold">削除</button>
                      </li>
                    ))}
                  </ul>
                )}
              </section>
            )
          })}
        </div>
      )}

      {modal && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-end" onClick={() => setModal(false)}>
          <div className="bg-white w-full max-w-md mx-auto rounded-t-2xl p-6" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-bold text-lg mb-4">ストック追加</h2>

            <label className="block mb-3">
              <span className="text-xs text-gray-500 font-semibold">品目名 *</span>
              <input
                className="mt-1 w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-emerald-400"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                placeholder="例：ひじき煮"
              />
            </label>

            <label className="block mb-4">
              <span className="text-xs text-gray-500 font-semibold">カテゴリ</span>
              <select
                className="mt-1 w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-emerald-400"
                value={form.category}
                onChange={(e) => setForm({ ...form, category: e.target.value as StockCategory })}
              >
                {CATEGORIES.map((c) => <option key={c.value} value={c.value}>{c.label}</option>)}
              </select>
            </label>

            <div className="flex gap-2">
              <button onClick={() => setModal(false)} className="flex-1 border border-gray-200 py-3 rounded-xl text-sm text-gray-500">
                キャンセル
              </button>
              <button
                onClick={handleAdd}
                disabled={saving || !form.name.trim()}
                className="flex-1 bg-emerald-500 text-white py-3 rounded-xl font-bold text-sm disabled:opacity-50"
              >
                {saving ? '追加中...' : '追加'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
