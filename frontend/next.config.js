/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'standalone', // Dockerのマルチステージビルドで standalone 出力を使うために必須
}

module.exports = nextConfig
