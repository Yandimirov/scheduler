module.exports = {
    entry: [
        'babel-polyfill',
        './app/app.js',
        'webpack/hot/dev-server'
    ],

    output: {
        filename: "public/js/bundle.js",
        sourceMapFilename: "public/js/bundle.map",
    },
    devtool: '#source-map',

    devServer: {
        port: 3000,
        historyApiFallback: true
    },
    module: {
        loaders: [
            {
                loader: 'babel',
                exclude: /node_modules/
            }
        ]
    }
}
