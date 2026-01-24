<template>
  <div class="login-container">
    <div class="login-card">
      <!-- 左侧图片 -->
      <img class="left-img animate__animated animate__bounceInLeft animate__fast" :src="leftImg" />

      <!-- 右侧表单 -->
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
              <img class="code-img" :src="codeUrl" @click="changeCode" />
            </div>
          </a-form-item>

          <!-- 登录按钮 -->
          <a-button
            type="primary"
            block
            size="large"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </a-button>

          <!-- 注册/忘记密码 -->
          <div class="extra">
            <router-link to="/register">
              <a-button type="primary">注册</a-button>
            </router-link>

            <router-link to="/password">
              <a-button type="link">忘记密码</a-button>
            </router-link>
          </div>
        </a-form>
      </div>
    </div>

    <footer>Mall-Shop © 2026 GuiMall</footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  LockOutlined,
  SafetyOutlined,
} from '@ant-design/icons-vue'

import leftImg from '@/assets/left_img_1.png'

const title = 'GuiMall'
const router = useRouter()
const route = useRoute()

const loading = ref(false)
const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  verificationCode: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码不能少于6位' },
  ],
}

const codeUrl = ref('https://www.oschina.net/action/user/captcha')

const changeCode = () => {
  codeUrl.value = `https://www.oschina.net/action/user/captcha?${Date.now()}`
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    // 模拟登录
    setTimeout(() => {
      loading.value = false
      message.success('登录成功')
      router.push('/')
    }, 1000)
  } catch (err) {
    console.log(err)
  }
}
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
  box-shadow: 0 10px 30px rgba(0,0,0,.1);
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

.extra {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
}

footer {
  margin-top: 20px;
  color: #333;
}
</style>
