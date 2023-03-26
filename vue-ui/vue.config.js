module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8001',
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/api': '/api'
        },
      },
    },
    headers: {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept",
    },
    // disableHostCheck: true,
  },
  publicPath: './',
  outputDir: 'dist',
  assetsDir: 'static',
  productionSourceMap: false
}