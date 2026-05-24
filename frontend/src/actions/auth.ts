'use server'

// 役割: 認証関連のServer Actions。
//       Server ActionsはNode.js（サーバー）側で実行されるため、INTERNAL_API_URLを使ってbackendコンテナに直接通信する。
//       ブラウザからは見えないため、秘密情報を扱うのに適した層。

import { apiFetch } from '@/lib/api'
import { cookies } from 'next/headers'
import { redirect } from 'next/navigation'

export async function register(formData: FormData) {
  const body = {
    email: formData.get('email'),
    password: formData.get('password'),
    name: formData.get('name'),
  }

  const res = await apiFetch('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(body),
  })

  const data = await res.json()

  if (!res.ok) {
    return { error: data.message }
  }

  redirect('/login')
}

export async function login(formData: FormData) {
  const body = {
    email: formData.get('email'),
    password: formData.get('password'),
  }

  const res = await apiFetch('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(body),
  })

  const data = await res.json()

  if (!res.ok) {
    return { error: data.message }
  }

  // Spring BootのSet-CookieヘッダーをNext.js側のCookieに転送する
  // セキュリティポイント: HttpOnly Cookie なのでJSから読めないが、
  //                       サーバー側のcookies()経由では転送できる
  const setCookie = res.headers.get('set-cookie')
  if (setCookie) {
    const cookieStore = await cookies()
    const tokenMatch = setCookie.match(/token=([^;]+)/)
    const maxAgeMatch = setCookie.match(/Max-Age=(\d+)/i)
    if (tokenMatch) {
      cookieStore.set('token', tokenMatch[1], {
        httpOnly: true,
        secure: true,
        sameSite: 'lax',
        path: '/',
        maxAge: maxAgeMatch ? parseInt(maxAgeMatch[1]) : 86400,
      })
    }
  }

  redirect('/dashboard')
}

export async function logout() {
  await apiFetch('/api/auth/logout', { method: 'POST' })

  const cookieStore = await cookies()
  cookieStore.delete('token')

  redirect('/login')
}
