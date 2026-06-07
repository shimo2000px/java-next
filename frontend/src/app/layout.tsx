import type { Metadata } from 'next'
import './globals.css'
import TabBar from '@/components/TabBar'

export const metadata: Metadata = {
  title: '献立アプリ',
  description: '毎日の食事の意思決定をゼロにする',
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ja">
      <body className="bg-gray-50 min-h-screen">
        <div className="max-w-md mx-auto min-h-screen bg-white shadow-sm pb-20">
          {children}
        </div>
        <TabBar />
      </body>
    </html>
  )
}
