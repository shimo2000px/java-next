'use client'

import { Recipe } from '@/types'

interface Props {
  recipes: Recipe[]
  onSelect: (recipe: Recipe) => void
  onClose: () => void
}

const CATEGORY_LABEL: Record<string, string> = {
  main_dish: 'おかず',
  rice:      'ご飯もの',
  noodle:    '麺類',
}

export default function RecipeModal({ recipes, onSelect, onClose }: Props) {
  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-end" onClick={onClose}>
      <div
        className="bg-white w-full max-w-md mx-auto rounded-t-2xl max-h-[80vh] overflow-y-auto"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="sticky top-0 bg-white px-4 pt-4 pb-2 border-b">
          <div className="flex justify-between items-center">
            <h2 className="font-bold text-lg">レシピを選択</h2>
            <button onClick={onClose} className="text-gray-400 text-2xl leading-none">×</button>
          </div>
        </div>
        <ul className="divide-y divide-gray-100 pb-8">
          {recipes.length === 0 && (
            <li className="p-4 text-center text-gray-400 text-sm">レシピが登録されていません</li>
          )}
          {recipes.map((r) => (
            <li key={r.id}>
              <button
                className="w-full text-left px-4 py-3 hover:bg-emerald-50 transition-colors"
                onClick={() => onSelect(r)}
              >
                <span className="font-medium">{r.name}</span>
                <span className="ml-2 text-xs text-gray-400">{CATEGORY_LABEL[r.category]}</span>
              </button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}
