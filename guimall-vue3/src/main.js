import '@/assets/main.css'//引入main.css样式文件
import 'animate.css';//引入animate.css动画库

import { createApp } from 'vue'//引入createApp方法
import App from '@/App.vue'//引入根组件App.vue
//导入路由
import router from '@/router'
//引入样式
import 'ant-design-vue/dist/reset.css'

const app = createApp(App)


//应用路由
app.use(router)
app.mount('#app')
// 创建应用，并将App根组件挂载到<div id="app"></div>中
// createApp(App).use(router).mount('#app')