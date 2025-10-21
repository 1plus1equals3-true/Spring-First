const path = require('path');

module.exports = {
    // 1. 빌드를 시작할 파일 (Tiptap 초기화 코드가 있는 파일)
    entry: './src/main/resources/static/js/tiptap-initializer.js',

    // 2. 빌드 결과물이 저장될 위치
    output: {
        // 최종 파일 이름. 이제 HTML에서 이 파일 하나만 로드합니다.
        filename: 'tiptap-bundle.js',
        // 저장될 경로: src/main/resources/static/js
        path: path.resolve(__dirname, 'src/main/resources/static/js'),
    },

    // 3. 빌드 모드 (개발 중에는 'development', 배포 시에는 'production')
    mode: 'development',

    // 4. 모듈 처리 규칙 (Babel을 사용하여 JS 파일을 번역)
    module: {
        rules: [
            {
                // .js 확장자를 가진 모든 파일을 대상으로 합니다.
                test: /\.js$/,
                // node_modules 폴더는 제외하여 빌드 속도를 높입니다.
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        // ES 모듈을 일반 JS로 변환하기 위한 프리셋
                        presets: ['@babel/preset-env'],
                    },
                },
            },
        ],
    },

    // 5. 번들 파일 크기 경고 비활성화 (Tiptap은 파일이 커서 경고가 뜰 수 있음)
    performance: {
        hints: false
    }
};