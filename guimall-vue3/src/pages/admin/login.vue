<template>
  <div class="login-container">
    <div class="login-card">
      <!-- 左侧图片 -->
      <img class="left-img animate__animated animate__bounceInLeft animate__fast" :src="leftImg" />

      <!-- 右侧登录表单 -->
      <div class="form-wrapper animate__animated animate__bounceInRight animate__fast">
        <h1 class="title">hello !</h1>
        <p class="tips">欢迎来到 {{ title }}</p>

        <a-form
          ref="formRef"
          :model="form"
          :rules="rules"
          layout="vertical"
        >
          <!-- 用户名 -->
          <a-form-item name="username">
            <a-input v-model:value="form.username" placeholder="请输入用户名">
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <!-- 密码 -->
          <a-form-item name="password">
            <a-input-password
              v-model:value="form.password"
              placeholder="请输入密码"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <!-- 验证码 -->
          <a-form-item name="verificationCode">
            <div class="code-row">
              <a-input
                v-model:value="form.verificationCode"
                placeholder="验证码"
              >
                <template #prefix>
                  <SafetyOutlined />
                </template>
              </a-input>
              <!-- 点击验证码图片可刷新 -->
              <img class="code-img" :src="codeUrl" @click="changeCode" />
            </div>
          </a-form-item>

          <!-- 登录按钮 -->
          <a-button
            type="primary"
            block
            size="large"
            :loading="loading"
            @click="onSubmit"
          >
            登录
          </a-button>

          <!-- 注册/忘记密码入口（按需开启） -->
          <!--
          <div class="extra">
            <router-link to="/register">
              <a-button type="primary">注册</a-button>
            </router-link>
            <router-link to="/password">
              <a-button type="link">忘记密码</a-button>
            </router-link>
          </div>
          -->
        </a-form>
      </div>
    </div>

    <footer>Mall-Shop © 2026 GuiMall</footer>
  </div>
</template>

<script setup>
import {
  UserOutlined,
  LockOutlined,
  SafetyOutlined,
} from '@ant-design/icons-vue'
import { login } from '@/api/admin/user'
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

import leftImg from '@/assets/left_img_1.png'
import { showMessage } from '@/composables/util'
import { setToken } from '@/composables/cookie'
import { useUserStore } from '@/stores/user'

const title = 'GuiMall'
const router = useRouter()
const userStore = useUserStore()

// 登录按钮加载状态
const loading = ref(false)

// 表单数据
const form = reactive({
  username: '',
  password: '',
  verificationCode: '',
})

// 表单实例引用
const formRef = ref(null)

// 表单校验规则
const rules = {
  username: [
    {
      required: true,
      message: '请输入用户名',
      trigger: 'blur',
    },
  ],
  password: [
    {
      required: true,
      message: '请输入密码',
      trigger: 'blur',
    },
    { min: 6, message: '密码不能少于6位', trigger: 'blur' },
  ],
  verificationCode: [
    {
      required: true,
      message: '请输入验证码',
      trigger: 'blur',
    },
  ],
}

// 验证码长度
const CAPTCHA_LEN = 4
// 当前验证码明文（仅前端校验用）
const generatedCode = ref('')
// 验证码图片地址（data:image/svg+xml）
const codeUrl = ref('')

// 生成随机验证码文本
const createCaptchaText = (len = CAPTCHA_LEN) => {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  let text = ''
  for (let i = 0; i < len; i++) {
    text += chars[Math.floor(Math.random() * chars.length)]
  }
  return text
}

// 将验证码文本绘制成 SVG，并转换为 data url
const createCaptchaDataUrl = (text) => {
  const w = 120
  const h = 40
  const bg = `hsl(${Math.floor(Math.random() * 360)}, 80%, 96%)`
  const rotate = Math.floor(Math.random() * 11) - 5
  const y = 27 + Math.floor(Math.random() * 5)

  // 干扰线
  const lines = Array.from({ length: 3 })
    .map(() => {
      const x1 = Math.floor(Math.random() * w)
      const y1 = Math.floor(Math.random() * h)
      const x2 = Math.floor(Math.random() * w)
      const y2 = Math.floor(Math.random() * h)
      return `<line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}" stroke="#9fb6e9" stroke-width="1" />`
    })
    .join('')

  // 干扰点
  const dots = Array.from({ length: 20 })
    .map(() => {
      const x = Math.floor(Math.random() * w)
      const y2 = Math.floor(Math.random() * h)
      return `<circle cx="${x}" cy="${y2}" r="1" fill="#8aa4df" />`
    })
    .join('')

  const svg = `
<svg xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}" viewBox="0 0 ${w} ${h}">
  <rect width="100%" height="100%" rx="6" ry="6" fill="${bg}" />
  ${lines}
  ${dots}
  <text x="60" y="${y}" text-anchor="middle"
        font-family="Arial, sans-serif" font-size="24"
        letter-spacing="5" fill="#2c5cc5"
        transform="rotate(${rotate}, 60, 20)">
    ${text}
  </text>
</svg>`

  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`
}

// 刷新验证码
const changeCode = () => {
  const text = createCaptchaText()
  generatedCode.value = text
  codeUrl.value = createCaptchaDataUrl(text)
}

// 登录提交
const onSubmit = async () => {
  try {
    // 先做表单必填/长度校验
    await formRef.value.validate()

    // 再做验证码正确性校验
    if (form.verificationCode.trim().toUpperCase() !== generatedCode.value) {
      showMessage('验证码错误，请重试', 'error')
      form.verificationCode = ''
      changeCode()
      return
    }

    loading.value = true
    const res = await login(form.username, form.password)

    if (res.success === true) {
      const token = res.data.token
      setToken(token)
      userStore.setUserInfo()
      showMessage('登录成功', 'success')
      router.push('/admin/index')
    } else {
      showMessage(res?.data?.message || res?.message || '登录失败', 'error')
      changeCode()
    }
  } catch (err) {
    console.log('表单校验未通过', err)
  } finally {
    loading.value = false
  }
}

// 回车键触发登录
function onKeyUp(e) {
  if (e.key === 'Enter') {
    onSubmit()
  }
}

// 页面挂载：初始化验证码 + 绑定键盘事件
onMounted(() => {
  changeCode()
  document.addEventListener('keyup', onKeyUp)
})

// 页面卸载：移除键盘事件
onBeforeUnmount(() => {
  document.removeEventListener('keyup', onKeyUp)
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #79a8ff, #5b8dff);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 1000px;
  background: #eaf1ff;
  border-radius: 20px;
  display: flex;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.left-img {
  width: 420px;
}

.form-wrapper {
  flex: 1;
  padding-left: 60px;
}

.title {
  font-size: 48px;
  margin-bottom: 10px;
}

.tips {
  font-size: 24px;
  color: #666;
  margin-bottom: 30px;
}

.code-row {
  display: flex;
  gap: 10px;
}

.code-img {
  height: 40px;
  cursor: pointer;
}

/* .extra {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
} */

footer {
  margin-top: 20px;
  color: #333;
}
</style>
