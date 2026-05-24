import { redirect } from 'next/navigation'

// ルートにアクセスしたらログインページへ転送
export default function Home() {
  redirect('/login')
}
