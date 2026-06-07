// 役割: Spring Boot APIへのfetch設定を共通化するユーティリティ。
//       Server Actions 内では INTERNAL_API_URL（コンテナ内部URL）を使う。

export const API_BASE = process.env.INTERNAL_API_URL ?? 'http://localhost:8080'

export async function apiFetch(path: string, options?: RequestInit) {
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  })
  return res
}
