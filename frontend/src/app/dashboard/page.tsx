// 役割: ログイン済みユーザーのみアクセスできるダッシュボードページ。
//       middleware.ts によって未認証ユーザーは /login にリダイレクトされる。

import { logout } from '@/actions/auth'

export default function DashboardPage() {
  return (
    <div>
      <h1>ダッシュボード</h1>
      <p>ログインに成功しました。</p>
      <form action={logout}>
        <button type="submit" style={{ padding: '8px 24px', cursor: 'pointer' }}>
          ログアウト
        </button>
      </form>
    </div>
  )
}
