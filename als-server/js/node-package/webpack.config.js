const path = require('path')
const { merge } = require('webpack-merge');
const TerserPlugin = require('terser-webpack-plugin')
const webpack = require("webpack")

/** Function for ignoring a list of chunks by name or id */
const createChunkFilter = chunkInfo => chunk => {
  const info = chunkInfo.find(
    info => (info.name !== undefined && info.name === chunk.name) || (info.id !== undefined && info.id === chunk.id),
  )

  return !info
}

const baseConfig = {
  mode: 'production',
  target: 'web',
  output: {
    filename: '[name].js',
    chunkFilename: '[name].js',
    path: path.resolve(__dirname, 'dist'),
    libraryTarget: 'var',
    library: 'AlsServer',
    globalObject: 'this'
  },
  resolve: {
    modules: [path.resolve(__dirname, 'node_modules')],
    alias: {
      os: "os-browserify/browser",
      path: "path-browserify",
      https: "https-browserify",
      url: "url",
      http: "stream-http"
    },
    fallback: {
      fs: false,
      net: false,
      child_process: false,
      crypto: false
    }
  },
  plugins: [
    new webpack.ProvidePlugin({
      Buffer: ['buffer', 'Buffer'],
    }),
    new webpack.ProvidePlugin({
      process: 'process/browser',
    }),
  ],
  module: {
    rules: [
      {
        test: /\.js$/,
        include: [
          /amf-shacl-node\/.*/,
          /vscode-jsonrpc\/.*/,
          /vscode-languageserver-protocol\/.*/,
        ],
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env'],
          },
        },
      },
    ],
  },
  optimization: {
    splitChunks: {
      chunks: 'all',
    },
    minimize: false,
  },
}

const fastOptConfig = {
  entry: {
    'als-server': [
        '@babel/polyfill',
        path.resolve(__dirname, 'lib/als-server.js')
      ]
  },
  optimization: {
    splitChunks: {
      cacheGroups: {
        defaultVendors: {
          test: /[\\/]node_modules[\\/]/,
          name: "vendors~als-server",
        },
      },
    },
  }
}

const fullOptIgnoredChunks = [{name: 'als-server.min'}]

const fullOptConfig = {
  entry: {
    'als-server.min': [
        '@babel/polyfill',
        path.resolve(__dirname, 'lib/als-server.min.js')
      ]
  },
  optimization: {
    minimize: true,
    minimizer: [new TerserPlugin({
      extractComments: false,
      chunkFilter: createChunkFilter(fullOptIgnoredChunks),
    })],
    splitChunks: {
      cacheGroups: {
        defaultVendors: {
          test: /[\\/]node_modules[\\/]/,
          name: "vendors~als-server.min",
        },
      },
    },
  }
}

module.exports = [
  merge(baseConfig, fastOptConfig),
  merge(baseConfig, fullOptConfig),
]