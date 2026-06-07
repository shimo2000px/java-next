/** 任意の日付が属する週の月曜日を返す */
export function getMonday(date: Date): Date {
  const d = new Date(date)
  const day = d.getDay()
  const diff = day === 0 ? -6 : 1 - day
  d.setDate(d.getDate() + diff)
  d.setHours(0, 0, 0, 0)
  return d
}

/** Date を "YYYY-MM-DD" 文字列に変換 */
export function formatDate(date: Date): string {
  return date.toISOString().split('T')[0]
}

/** 月曜日から7日分の Date 配列を返す */
export function getWeekDays(monday: Date): Date[] {
  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date(monday)
    d.setDate(d.getDate() + i)
    return d
  })
}

/** 土日判定 */
export function isWeekend(date: Date): boolean {
  const day = date.getDay()
  return day === 0 || day === 6
}

/** "M/D 〜 M/D" 形式で週の範囲を返す */
export function formatWeekRange(monday: Date): string {
  const sunday = new Date(monday)
  sunday.setDate(sunday.getDate() + 6)
  const fmt = (d: Date) => `${d.getMonth() + 1}/${d.getDate()}`
  return `${fmt(monday)} 〜 ${fmt(sunday)}`
}

const DAY_LABELS = ['月', '火', '水', '木', '金', '土', '日']

/** "月" "火" などの曜日ラベルを返す */
export function getDayLabel(date: Date): string {
  const day = date.getDay()
  return DAY_LABELS[day === 0 ? 6 : day - 1]
}

/** "M/D" 形式の日付ラベルを返す */
export function formatShortDate(date: Date): string {
  return `${date.getMonth() + 1}/${date.getDate()}`
}
