'use client'

import { useState } from 'react'
import { Stock, BentoPlan } from '@/types'

interface Props {
  stocks: Stock[]
  current?: BentoPlan | null
  plannedDate: string
  onSave: (data: {
    plannedDate: string
    riceStockId?: number | null
    mainStockId?: number | null
    sideStockId?: number | null
    noodleStockId?: number | null
  }) => void
  onClose: () => void
}

export default function BentoModal({ stocks, current, plannedDate, onSave, onClose }: Props) {
  const [isNoodleDay, setIsNoodleDay] = useState(!!current?.noodle)
  const [riceId, setRiceId] = useState<number | null>(current?.rice?.id ?? null)
  const [mainId, setMainId] = useState<number | null>(current?.main?.id ?? null)
  const [sideId, setSideId] = useState<number | null>(current?.side?.id ?? null)
  const [noodleId, setNoodleId] = useState<number | null>(current?.noodle?.id ?? null)

  const byCategory = (cat: string) => stocks.filter((s) => s.category === cat && s.isAvailable)

  const handleSave = () => {
    onSave({
      plannedDate,
      riceStockId:   isNoodleDay ? null : riceId,
      mainStockId:   isNoodleDay ? null : mainId,
      sideStockId:   isNoodleDay ? null : sideId,
      noodleStockId: isNoodleDay ? noodleId : null,
    })
  }

  const Radio = ({ label, items, value, onChange }: {
    label: string
    items: Stock[]
    value: number | null
    onChange: (id: number | null) => void
  }) => (
    <div className="mb-4">
      <p className="text-xs font-semibold text-gray-500 mb-1">{label}</p>
      {items.length === 0 ? (
        <p className="text-xs text-gray-300">在庫なし</p>
      ) : (
        <div className="flex flex-wrap gap-2">
          <button
            className={`px-3 py-1 rounded-full text-sm border ${value === null ? 'bg-emerald-500 text-white border-emerald-500' : 'border-gray-300 text-gray-600'}`}
            onClick={() => onChange(null)}
          >なし</button>
          {items.map((s) => (
            <button
              key={s.id}
              className={`px-3 py-1 rounded-full text-sm border ${value === s.id ? 'bg-emerald-500 text-white border-emerald-500' : 'border-gray-300 text-gray-600'}`}
              onClick={() => onChange(s.id)}
            >{s.name}</button>
          ))}
        </div>
      )}
    </div>
  )

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-end" onClick={onClose}>
      <div
        className="bg-white w-full max-w-md mx-auto rounded-t-2xl max-h-[85vh] overflow-y-auto"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="sticky top-0 bg-white px-4 pt-4 pb-2 border-b">
          <div className="flex justify-between items-center">
            <h2 className="font-bold text-lg">お弁当を設定</h2>
            <button onClick={onClose} className="text-gray-400 text-2xl leading-none">×</button>
          </div>
        </div>

        <div className="px-4 py-4 pb-8">
          <label className="flex items-center gap-2 mb-4 cursor-pointer">
            <input
              type="checkbox"
              checked={isNoodleDay}
              onChange={(e) => setIsNoodleDay(e.target.checked)}
              className="w-4 h-4 accent-emerald-500"
            />
            <span className="text-sm font-medium">麺類の日にする</span>
          </label>

          {isNoodleDay ? (
            <Radio label="麺類" items={byCategory('noodle')} value={noodleId} onChange={setNoodleId} />
          ) : (
            <>
              <Radio label="ご飯"   items={byCategory('rice')}      value={riceId} onChange={setRiceId} />
              <Radio label="おかず" items={byCategory('main_dish')} value={mainId} onChange={setMainId} />
              <Radio label="副菜"   items={byCategory('side_dish')} value={sideId} onChange={setSideId} />
            </>
          )}

          <button
            onClick={handleSave}
            className="w-full bg-emerald-500 text-white py-3 rounded-xl font-semibold mt-2"
          >
            保存
          </button>
        </div>
      </div>
    </div>
  )
}
