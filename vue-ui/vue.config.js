module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8001',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    }
  },
  publicPath: './',
  outputDir: 'dist',
  assetsDir: 'static',
  productionSourceMap: false
}