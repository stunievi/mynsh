var webpack = require('webpack')

var p = '/Users/bin/work/hzcp/www'

module.exports = {
    entry : {
        'common': ["./src/lib.js"]
        // ,"dict": "./src/dict.js"
    },
    output:{
        path: '/Users/bin/work/hzcp/www/dist',
        filename: "[name].js"
    },
    resolve:{
        extensions: ['.js'],
        alias:{
            layer: p + "/static/vendor/layui/layer/layer.js",
            "layer.css": p + "/static/vendor/layui/layer/theme/default/layer.css"
        }
    },
    module: {
        rules: [
            // exclude 排除，不需要编译的目录，提高编译速度
            {test: /\.js$/, use: 'babel-loader', exclude: /node_modules/},
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            }
            // {test: require.resolve("jquery"), use: ["expose-loader?$","expose-loader?jQuery"]}
        ]
    },
    plugins:[
        new webpack.ProvidePlugin({
            "$": "jquery",
            "jQuery": "jquery",
            "window.jQuery": "jquery"
        })
    ]
}