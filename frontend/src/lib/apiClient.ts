import { Recipe, MealPlan, Stock, BentoPlan } from '@/types'

const BASE = process.env.NEXT_PUBLIC_API_URL ?? ''

async function req<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: 'エラーが発生しました' }))
    throw new Error(err.message ?? 'エラーが発生しました')
  }
  if (res.status === 204) return undefined as T
  return res.json()
}

// --- Recipe ---
export const getRecipes = (category?: string) =>
  req<Recipe[]>(`/api/recipes${category ? `?category=${category}` : ''}`)

export const createRecipe = (data: Omit<Recipe, 'id' | 'createdAt'>) =>
  req<Recipe>('/api/recipes', { method: 'POST', body: JSON.stringify(data) })

export const updateRecipe = (id: number, data: Omit<Recipe, 'id' | 'createdAt'>) =>
  req<Recipe>(`/api/recipes/${id}`, { method: 'PUT', body: JSON.stringify(data) })

export const deleteRecipe = (id: number) =>
  req<void>(`/api/recipes/${id}`, { method: 'DELETE' })

// --- MealPlan ---
export const getMealPlans = (weekOf: string) =>
  req<MealPlan[]>(`/api/meal-plans?weekOf=${weekOf}`)

export const randomizeMealPlans = (weekOf: string) =>
  req<MealPlan[]>('/api/meal-plans/random', { method: 'POST', body: JSON.stringify({ weekOf }) })

export const updateMealPlan = (id: number, recipeId: number) =>
  req<MealPlan>(`/api/meal-plans/${id}`, { method: 'PUT', body: JSON.stringify({ recipeId }) })

// --- Stock ---
export const getStocks = (all = false) =>
  req<Stock[]>(`/api/stocks${all ? '?all=true' : ''}`)

export const createStock = (data: Pick<Stock, 'name' | 'category'>) =>
  req<Stock>('/api/stocks', { method: 'POST', body: JSON.stringify(data) })

export const updateStock = (id: number, data: Pick<Stock, 'name' | 'category'>) =>
  req<Stock>(`/api/stocks/${id}`, { method: 'PUT', body: JSON.stringify(data) })

export const useStock = (id: number) =>
  req<Stock>(`/api/stocks/${id}/use`, { method: 'PATCH' })

export const restoreStock = (id: number) =>
  req<Stock>(`/api/stocks/${id}/restore`, { method: 'PATCH' })

export const deleteStock = (id: number) =>
  req<void>(`/api/stocks/${id}`, { method: 'DELETE' })

// --- BentoPlan ---
export const getBentoPlans = (weekOf: string) =>
  req<BentoPlan[]>(`/api/bento-plans?weekOf=${weekOf}`)

export const randomizeBentoPlans = (weekOf: string) =>
  req<BentoPlan[]>('/api/bento-plans/random', { method: 'POST', body: JSON.stringify({ weekOf }) })

export const saveBentoPlan = (data: {
  plannedDate: string
  riceStockId?: number | null
  mainStockId?: number | null
  sideStockId?: number | null
  noodleStockId?: number | null
  memo?: string | null
}) => req<BentoPlan>('/api/bento-plans', { method: 'POST', body: JSON.stringify(data) })
