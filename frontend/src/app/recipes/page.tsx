'use client'

import { useState, useEffect } from 'react'
import { Recipe, RecipeCategory } from '@/types'
import { getRecipes, createRecipe, updateRecipe, deleteRecipe } from '@/lib/apiClient'

const CATEGORIES: { value: RecipeCategory; label: string }[] = [
  { value: 'main_dish', label: 'おかず' },
  { value: 'rice',      label: 'ご飯もの' },
  { value: 'noodle',    label: '麺類' },
]

interface FormState { name: string; category: RecipeCategory; size: string; memo: string }
const EMPTY: FormState = { name: '', category: 'main_dish', size: '', memo: '' }

export default function RecipesPage() {
  const [recipes, setRecipes]   = useState<Recipe[]>([])
  const [loading, setLoading]   = useState(true)
  const [error, setError]       = useState<string | null>(null)
  const [modal, setModal]       = useState<{ mode: 'add' | 'edit'; recipe?: Recipe } | null>(null)
  const [form, setForm]         = useState<FormState>(EMPTY)
  const [saving, setSaving]     = useState(false)

  const load = async () => {
    try {
      setRecipes(await getRecipes())
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'レシピの取得に失敗しました')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const openAdd = () => { setForm(EMPTY); setModal({ mode: 'add' }) }
  const openEdit = (r: Recipe) => {
    setForm({ name: r.name, category: r.category, size: r.size ?? '', memo: r.memo ?? '' })
    setModal({ mode: 'edit', recipe: r })
  }

  const handleSave = async () => {
    if (!form.name.trim()) return
    setSaving(true)
    setError(null)
    try {
      const data = { name: form.name.trim(), category: form.category, size: form.size || undefined, memo: form.memo || undefined }
      if (modal?.mode === 'edit' && modal.recipe) {
        const updated = await updateRecipe(modal.recipe.id, data)
        setRecipes((prev) => prev.map((r) => r.id === updated.id ? updated : r))
      } else {
        const created = await createRecipe(data)
        setRecipes((prev) => [...prev, created])
      }
      setModal(null)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : '保存に失敗しました')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('このレシピを削除しますか？')) return
    try {
      await deleteRecipe(id)
      setRecipes((prev) => prev.filter((r) => r.id !== id))
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : '削除に失敗しました')
    }
  }

  return (
    <div>
      <div className="bg-emerald-500 text-white px-4 pt-10 pb-4 flex justify-between items-end">
        <h1 className="text-lg font-bold">レシピ</h1>
        <button onClick={openAdd} className="bg-white text-emerald-600 font-bold px-4 py-2 rounded-lg text-sm">
          + 追加
        </button>
      </div>

      {error && <div className="mx-4 mt-3 p-3 bg-red-50 text-red-600 text-sm rounded-lg">{error}</div>}

      {loading ? (
        <div className="p-8 text-center text-gray-400">読み込み中...</div>
      ) : (
        <div className="px-4 py-4 space-y-6">
          {CATEGORIES.map(({ value, label }) => {
            const group = recipes.filter((r) => r.category === value)
            return (
              <section key={value}>
                <h2 className="text-xs font-bold text-gray-400 uppercase tracking-wide mb-2">{label}</h2>
                {group.length === 0 ? (
                  <p className="text-sm text-gray-300 pl-1">登録なし</p>
                ) : (
                  <ul className="space-y-2">
                    {group.map((r) => (
                      <li key={r.id} className="flex items-center bg-white border border-gray-100 rounded-xl px-4 py-3 shadow-sm">
                        <span className="flex-1 font-medium text-sm">{r.name}</span>
                        {r.memo && <span className="text-xs text-gray-400 mr-3 truncate max-w-[100px]">{r.memo}</span>}
                        <button onClick={() => openEdit(r)} className="text-emerald-500 text-xs mr-2 font-semibold">編集</button>
                        <button onClick={() => handleDelete(r.id)} className="text-red-400 text-xs font-semibold">削除</button>
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
        <div className="fixed inset-0 bg-black/50 z-50 flex items-end" onClick={() => setModal(null)}>
          <div className="bg-white w-full max-w-md mx-auto rounded-t-2xl p-6" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-bold text-lg mb-4">{modal.mode === 'add' ? 'レシピ追加' : 'レシピ編集'}</h2>

            <label className="block mb-3">
              <span className="text-xs text-gray-500 font-semibold">料理名 *</span>
              <input
                className="mt-1 w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-emerald-400"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                placeholder="例：鶏の照り焼き"
              />
            </label>

            <label className="block mb-3">
              <span className="text-xs text-gray-500 font-semibold">カテゴリ</span>
              <select
                className="mt-1 w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-emerald-400"
                value={form.category}
                onChange={(e) => setForm({ ...form, category: e.target.value as RecipeCategory })}
              >
                {CATEGORIES.map((c) => <option key={c.value} value={c.value}>{c.label}</option>)}
              </select>
            </label>

            <label className="block mb-3">
              <span className="text-xs text-gray-500 font-semibold">サイズ（任意）</span>
              <select
                className="mt-1 w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-emerald-400"
                value={form.size}
                onChange={(e) => setForm({ ...form, size: e.target.value })}
              >
                <option value="">未設定</option>
                <option value="large">大</option>
                <option value="medium">中</option>
                <option value="small">小</option>
              </select>
            </label>

            <label className="block mb-4">
              <span className="text-xs text-gray-500 font-semibold">メモ（任意）</span>
              <textarea
                className="mt-1 w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-emerald-400"
                rows={2}
                value={form.memo}
                onChange={(e) => setForm({ ...form, memo: e.target.value })}
                placeholder="例：週1でもOK"
              />
            </label>

            <div className="flex gap-2">
              <button onClick={() => setModal(null)} className="flex-1 border border-gray-200 py-3 rounded-xl text-sm text-gray-500">
                キャンセル
              </button>
              <button
                onClick={handleSave}
                disabled={saving || !form.name.trim()}
                className="flex-1 bg-emerald-500 text-white py-3 rounded-xl font-bold text-sm disabled:opacity-50"
              >
                {saving ? '保存中...' : '保存'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
