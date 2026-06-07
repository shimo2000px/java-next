'use client'

import { useState, useEffect, useCallback } from 'react'
import { MealPlan, BentoPlan, Recipe, Stock } from '@/types'
import { getMonday, getWeekDays, formatDate, formatWeekRange, getDayLabel, formatShortDate, isWeekend } from '@/utils/date'
import { getMealPlans, getBentoPlans, randomizeMealPlans, randomizeBentoPlans, updateMealPlan, saveBentoPlan, getRecipes, getStocks } from '@/lib/apiClient'
import RecipeModal from '@/components/RecipeModal'
import BentoModal from '@/components/BentoModal'

export default function CalendarPage() {
  const [monday, setMonday] = useState<Date>(() => getMonday(new Date()))
  const [mealPlans, setMealPlans] = useState<MealPlan[]>([])
  const [bentoPlans, setBentoPlans] = useState<BentoPlan[]>([])
  const [recipes, setRecipes] = useState<Recipe[]>([])
  const [stocks, setStocks] = useState<Stock[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // モーダル状態
  const [recipeModal, setRecipeModal] = useState<{ mealPlanId: number } | null>(null)
  const [bentoModal, setBentoModal]   = useState<{ date: string; current?: BentoPlan | null } | null>(null)

  const weekOf = formatDate(monday)
  const days   = getWeekDays(monday)

  const loadWeek = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const [mp, bp] = await Promise.all([getMealPlans(weekOf), getBentoPlans(weekOf)])
      setMealPlans(mp)
      setBentoPlans(bp)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'データ取得に失敗しました')
    } finally {
      setLoading(false)
    }
  }, [weekOf])

  useEffect(() => { loadWeek() }, [loadWeek])

  useEffect(() => {
    getRecipes().then(setRecipes).catch(() => {})
    getStocks(true).then(setStocks).catch(() => {})
  }, [])

  const handleAutoAssign = async () => {
    setLoading(true)
    setError(null)
    try {
      const [mp, bp] = await Promise.all([
        randomizeMealPlans(weekOf),
        randomizeBentoPlans(weekOf),
      ])
      setMealPlans(mp)
      setBentoPlans(bp)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : '自動割り振りに失敗しました')
    } finally {
      setLoading(false)
    }
  }

  const handleRecipeSelect = async (recipe: Recipe) => {
    if (!recipeModal) return
    try {
      const updated = await updateMealPlan(recipeModal.mealPlanId, recipe.id)
      setMealPlans((prev) => prev.map((mp) => mp.id === updated.id ? updated : mp))
      setRecipeModal(null)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : '更新に失敗しました')
    }
  }

  const handleBentoSave = async (data: Parameters<typeof saveBentoPlan>[0]) => {
    try {
      const updated = await saveBentoPlan(data)
      setBentoPlans((prev) => {
        const exists = prev.find((bp) => bp.plannedDate === updated.plannedDate)
        return exists ? prev.map((bp) => bp.plannedDate === updated.plannedDate ? updated : bp) : [...prev, updated]
      })
      setBentoModal(null)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : '更新に失敗しました')
    }
  }

  const getMealForDate = (date: string) => mealPlans.find((mp) => mp.plannedDate === date)
  const getBentoForDate = (date: string) => bentoPlans.find((bp) => bp.plannedDate === date)

  return (
    <div>
      {/* ヘッダー */}
      <div className="bg-emerald-500 text-white px-4 pt-10 pb-4">
        <h1 className="text-lg font-bold mb-3">献立カレンダー</h1>
        <div className="flex items-center justify-between mb-4">
          <button
            onClick={() => setMonday((d) => { const n = new Date(d); n.setDate(n.getDate() - 7); return n })}
            className="bg-white/20 text-white px-3 py-1 rounded-full text-sm"
          >← 前週</button>
          <span className="font-semibold text-sm">{formatWeekRange(monday)}</span>
          <button
            onClick={() => setMonday((d) => { const n = new Date(d); n.setDate(n.getDate() + 7); return n })}
            className="bg-white/20 text-white px-3 py-1 rounded-full text-sm"
          >次週 →</button>
        </div>
        <button
          onClick={handleAutoAssign}
          disabled={loading}
          className="w-full bg-white text-emerald-600 font-bold py-3 rounded-xl shadow disabled:opacity-50"
        >
          {loading ? '割り振り中...' : '🎲 1週間を自動割り振り'}
        </button>
      </div>

      {error && (
        <div className="mx-4 mt-3 p-3 bg-red-50 text-red-600 text-sm rounded-lg">{error}</div>
      )}

      {/* 週間カレンダー */}
      <div className="overflow-x-auto">
        <div className="flex min-w-[560px] gap-1 p-2">
          {days.map((day) => {
            const dateStr = formatDate(day)
            const meal    = getMealForDate(dateStr)
            const bento   = getBentoForDate(dateStr)
            const weekend = isWeekend(day)

            return (
              <div key={dateStr} className="flex-1 min-w-0">
                {/* 曜日・日付ヘッダー */}
                <div className={`text-center text-xs font-bold py-1 rounded-t-lg mb-1 ${weekend ? 'text-rose-400' : 'text-gray-600'}`}>
                  <div>{getDayLabel(day)}</div>
                  <div>{formatShortDate(day)}</div>
                </div>

                {/* 夜ご飯カード */}
                <button
                  onClick={() => meal && setRecipeModal({ mealPlanId: meal.id })}
                  className={`w-full text-left p-1.5 rounded-lg mb-1 text-xs min-h-[52px] ${
                    meal?.recipe
                      ? 'bg-emerald-50 border border-emerald-200'
                      : 'border border-dashed border-gray-300 text-gray-400'
                  }`}
                >
                  <div className="text-[10px] text-emerald-600 font-semibold mb-0.5">夜ご飯</div>
                  {meal?.recipe ? (
                    <div className="font-medium leading-tight">{meal.recipe.name}</div>
                  ) : (
                    <div className="text-center mt-1">未設定</div>
                  )}
                </button>

                {/* お弁当カード（平日のみ） */}
                {!weekend && (
                  <button
                    onClick={() => setBentoModal({ date: dateStr, current: bento })}
                    className={`w-full text-left p-1.5 rounded-lg text-xs min-h-[52px] ${
                      bento?.rice || bento?.main || bento?.noodle
                        ? 'bg-amber-50 border border-amber-200'
                        : 'border border-dashed border-gray-300 text-gray-400'
                    }`}
                  >
                    <div className="text-[10px] text-amber-600 font-semibold mb-0.5">お弁当</div>
                    {bento?.noodle ? (
                      <div className="font-medium leading-tight">{bento.noodle.name}</div>
                    ) : bento?.rice || bento?.main ? (
                      <div className="leading-tight space-y-0.5">
                        {bento.rice && <div>{bento.rice.name}</div>}
                        {bento.main && <div className="text-[10px] text-gray-500">{bento.main.name}</div>}
                      </div>
                    ) : (
                      <div className="text-center mt-1">未設定</div>
                    )}
                  </button>
                )}
              </div>
            )
          })}
        </div>
      </div>

      {/* ストックセクション */}
      <div className="px-4 mt-4">
        <h2 className="text-sm font-bold text-gray-600 mb-2">在庫ストック</h2>
        {stocks.filter((s) => s.isAvailable).length === 0 ? (
          <p className="text-sm text-gray-400">ストックが登録されていません</p>
        ) : (
          <div className="flex flex-wrap gap-2">
            {stocks.filter((s) => s.isAvailable).map((s) => (
              <span key={s.id} className="px-2 py-1 bg-gray-100 text-gray-600 rounded-full text-xs">
                {s.name}
              </span>
            ))}
          </div>
        )}
      </div>

      {/* モーダル */}
      {recipeModal && (
        <RecipeModal
          recipes={recipes}
          onSelect={handleRecipeSelect}
          onClose={() => setRecipeModal(null)}
        />
      )}
      {bentoModal && (
        <BentoModal
          stocks={stocks}
          current={bentoModal.current}
          plannedDate={bentoModal.date}
          onSave={handleBentoSave}
          onClose={() => setBentoModal(null)}
        />
      )}
    </div>
  )
}
