'use client'

// 役割: ログインページ。
// React 18 (Next.js 14) では useActionState は未実装のため、
// react-dom の useFormState + useFormStatus を使う。
// React 19 / Next.js 15 以降は useActionState に統合された。

import { login } from '@/actions/auth'
import { useFormState, useFormStatus } from 'react-dom'
import Link from 'next/link'

type State = { error: string | undefined }

// useFormStatus はフォームの送信状態を取得するフック。
// フォームの外側に置けないため、ボタンを別コンポーネントに切り出す必要がある。
function SubmitButton() {
  const { pending } = useFormStatus()
  return (
    <button type="submit" disabled={pending} style={buttonStyle}>
      {pending ? 'ログイン中...' : 'ログイン'}
    </button>
  )
}

const initialState: State = { error: undefined }

export default function LoginPage() {
  const [state, formAction] = useFormState(
    async (_prev: State, formData: FormData): Promise<State> => {
      const result = await login(formData)
      return result ?? initialState
    },
    initialState
  )

  return (
    <div>
      <h1>ログイン</h1>
      <form action={formAction}>
        <div style={{ marginBottom: '12px' }}>
          <label>メールアドレス<br />
            <input type="email" name="email" required style={inputStyle} />
          </label>
        </div>
        <div style={{ marginBottom: '12px' }}>
          <label>パスワード<br />
            <input type="password" name="password" required style={inputStyle} />
          </label>
        </div>
        {state.error && <p style={{ color: 'red' }}>{state.error}</p>}
        <SubmitButton />
      </form>
      <p style={{ marginTop: '16px' }}>
        アカウントをお持ちでない方は <Link href="/register">新規登録</Link>
      </p>
    </div>
  )
}

const inputStyle: React.CSSProperties = { width: '100%', padding: '8px', boxSizing: 'border-box' }
const buttonStyle: React.CSSProperties = { padding: '8px 24px', cursor: 'pointer' }
