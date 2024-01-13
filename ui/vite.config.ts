import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    resolve: {
        alias: [
            {find: 'pages', replacement: '/src/pages'},
            {find: 'components', replacement: '/src/components'},
            {find: 'features', replacement: '/src/features'},
            {find: 'common', replacement: '/src/common'},
        ]
    }
})
