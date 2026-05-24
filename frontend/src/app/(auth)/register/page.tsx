'use client'

// 役割: 新規登録ページ。
// React 18 (Next.js 14) では useFormState + useFormStatus を使う。

import { register } from '@/actions/auth'
import { useFormState, useFormStatus } from 'react-dom'
import Link from 'next/link'

type State = { error: string | undefined }

function SubmitButton() {
  const { pending } = useFormStatus()
  return (
    <button type="submit" disabled={pending} style={buttonStyle}>
      {pending ? '登録中...' : '登録する'}
    </button>
  )
}

const initialState: State = { error: undefined }

export default function RegisterPage() {
  const [state, formAction] = useFormState(
    async (_prev: State, formData: FormData): Promise<State> => {
      const result = await register(formData)
      return result ?? initialState
    },
    initialState
  )

  return (
    <div>
      <h1>新規登録</h1>
      <form action={formAction}>
        <div style={{ marginBottom: '12px' }}>
          <label>名前<br />
            <input type="text" name="name" required style={inputStyle} />
          </label>
        </div>
        <div style={{ marginBottom: '12px' }}>
          <label>メールアドレス<br />
            <input type="email" name="email" required style={inputStyle} />
          </label>
        </div>
        <div style={{ marginBottom: '12px' }}>
          <label>パスワード（8文字以上・英大小文字・数字・記号を含む）<br />
            <input type="password" name="password" required style={inputStyle} />
          </label>
        </div>
        {state.error && <p style={{ color: 'red' }}>{state.error}</p>}
        <SubmitButton />
      </form>
      <p style={{ marginTop: '16px' }}>
        既にアカウントをお持ちの方は <Link href="/login">ログイン</Link>
      </p>
    </div>
  )
}

const inputStyle: React.CSSProperties = { width: '100%', padding: '8px', boxSizing: 'border-box' }
const buttonStyle: React.CSSProperties = { padding: '8px 24px', cursor: 'pointer' }
