// 役割: /dashboard/** へのアクセスを保護するNext.js Middleware。
//       リクエストがサーバーに到達する前に実行され、Cookie内のJWTを確認する。
//       セキュリティポイント: MiddlewareはEdge Runtimeで動くため、重い処理は避けJWTの存在チェックのみに留める。

import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

export function middleware(request: NextRequest) {
  const token = request.cookies.get('token')?.value

  // /dashboard 以下に未認証でアクセスした場合は /login へリダイレクト
  if (!token) {
    return NextResponse.redirect(new URL('/login', request.url))
  }

  return NextResponse.next()
}

export const config = {
  matcher: ['/dashboard/:path*'],
}
