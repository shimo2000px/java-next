import type { Metadata } from 'next'

export const metadata: Metadata = {
  title: 'Auth App',
  description: 'Spring Boot + Next.js 認証サンプル',
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ja">
      <body style={{ fontFamily: 'sans-serif', maxWidth: '400px', margin: '60px auto', padding: '0 16px' }}>
        {children}
      </body>
    </html>
  )
}
