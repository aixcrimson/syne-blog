<template>
  <div
    class="page-shell flex overflow-hidden relative justify-center items-center px-4 py-12 min-h-screen transition-colors duration-500 sm:px-6 lg:px-8"
  >
    <div class="pointer-events-none absolute inset-0 paper-bg">
      <div
        class="absolute -top-24 -right-16 w-64 h-64 rounded-full blur-3xl bg-amber-200/40 dark:bg-amber-400/10"
      />
      <div
        class="absolute -bottom-20 -left-14 w-72 h-72 rounded-full blur-3xl bg-sky-200/40 dark:bg-sky-500/10"
      />
      <div class="absolute inset-0 opacity-60 page-grid dark:opacity-30" />
    </div>

    <div
      class="paper-card relative z-10 p-8 w-full max-w-md"
    >
      <!-- 头部切换 -->
      <div class="mb-8 text-center">
        <h2 class="mb-2 text-3xl font-semibold text-slate-900 dark:text-slate-100">
          {{ isLoginMode ? "欢迎回来" : "创建账号" }}
        </h2>
        <p class="mt-2 text-sm text-slate-600 dark:text-slate-400">
          {{ isLoginMode ? "还没有账号？" : "已有账号？" }}
          <a
            href="#"
            class="font-medium transition-colors text-primary-600 hover:text-primary-500"
            @click.prevent="toggleMode"
          >
            {{ isLoginMode ? "立即注册" : "立即登录" }}
          </a>
        </p>
      </div>

      <!-- 登录表单 -->
      <el-form
        v-if="isLoginMode"
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="space-y-6"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名 / 邮箱"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <div class="flex justify-between items-center">
          <div class="flex items-center">
            <input
              id="remember-me"
              name="remember-me"
              type="checkbox"
              class="w-4 h-4 rounded border-slate-300 text-primary-600 focus:ring-primary-500"
            />
            <label
              for="remember-me"
              class="block ml-2 text-sm text-slate-700 dark:text-slate-300"
            >
              记住我
            </label>
          </div>
          <div class="text-sm">
            <a
              href="#"
              class="font-medium text-primary-600 hover:text-primary-500"
              >忘记密码?</a
            >
          </div>
        </div>

        <div>
          <el-button
            type="primary"
            class="w-full !rounded-lg !h-12 !text-lg !font-medium"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </div>
      </el-form>

      <!-- 注册表单 -->
      <el-form
        v-else
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="space-y-4"
        size="large"
      >
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="邮箱"
            :prefix-icon="Message"
            clearable
          />
        </el-form-item>

        <el-form-item prop="code">
          <div class="flex gap-4 w-full">
            <el-input
              v-model="registerForm.code"
              placeholder="验证码"
              :prefix-icon="Key"
              maxlength="6"
              class="flex-1"
            />
            <el-button
              type="primary"
              plain
              :disabled="coutdown > 0"
              @click="sendCode"
              class="!w-[120px]"
            >
              {{ coutdown > 0 ? `${coutdown}s后重试` : "获取验证码" }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <div class="pt-2">
          <el-button
            type="primary"
            class="w-full !rounded-lg !h-12 !text-lg !font-medium"
            :loading="loading"
            @click="handleRegister"
          >
            注 册
          </el-button>
        </div>
      </el-form>

      <!-- 底部辅助 -->
      <div class="mt-6">
        <div class="relative">
          <div class="flex absolute inset-0 items-center">
            <div
              class="w-full border-t border-slate-300/70 dark:border-slate-700/70"
            ></div>
          </div>
          <div class="flex relative justify-center text-sm">
            <span
              class="px-2 text-slate-500 bg-white/70 dark:bg-slate-900/70 dark:text-slate-400"
            >
              或者返回
            </span>
          </div>
        </div>
        <div class="grid grid-cols-1 gap-3 mt-6">
          <el-button @click="router.push('/')">回到首页</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { User, Lock, Message, Key } from "@element-plus/icons-vue";
import type { FormInstance, FormRules } from "element-plus";
import { ElMessage } from "element-plus";
import { authApi } from "@/api/auth";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();

// 状态
const isLoginMode = ref(true);
const loading = ref(false);
const coutdown = ref(0);
let timer: any = null;

/* ---------------- 登录逻辑 ---------------- */
const loginFormRef = ref<FormInstance>();
const loginForm = reactive({
  username: "",
  password: "",
});

const loginRules = reactive<FormRules>({
  username: [
    { required: true, message: "请输入用户名或邮箱", trigger: "blur" },
    { min: 3, message: "长度至少 3 个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
  ],
});

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        // 使用类型断言
        const res = await authApi.login(loginForm);
        ElMessage.success("登录成功");

        // 使用 Pinia store
        userStore.setToken(res.token);
        // 修改：后端返回的是扁平结构，res 本身包含用户信息
        userStore.setUser(res);

        router.push("/");
      } catch (error: any) {
        ElMessage.error(error.message || "登录失败");
      } finally {
        loading.value = false;
      }
    }
  });
};

/* ---------------- 注册逻辑 ---------------- */
const registerFormRef = ref<FormInstance>();
const registerForm = reactive({
  email: "",
  code: "",
  username: "",
  password: "",
  confirmPassword: "",
});

const validatePass2 = (_rule: any, value: any, callback: any) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== registerForm.password) {
    callback(new Error("两次输入密码不一致!"));
  } else {
    callback();
  }
};

const registerRules = reactive<FormRules>({
  email: [
    { required: true, message: "请输入邮箱地址", trigger: "blur" },
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
  code: [
    { required: true, message: "请输入验证码", trigger: "blur" },
    { len: 6, message: "验证码长度为6位", trigger: "blur" },
  ],
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
  ],
  confirmPassword: [{ validator: validatePass2, trigger: "blur" }],
});

const sendCode = async () => {
  // 先验证邮箱格式
  if (!registerForm.email) {
    ElMessage.warning("请先填写邮箱");
    return;
  }

  loading.value = true;
  try {
    await authApi.sendCode(registerForm.email);
    ElMessage.success("验证码已发送 (请查看控制台 Mock 输出)");
    // 开始倒计时
    coutdown.value = 60;
    timer = setInterval(() => {
      coutdown.value--;
      if (coutdown.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);
  } catch (error: any) {
    ElMessage.error("验证码发送失败");
  } finally {
    loading.value = false;
  }
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await authApi.register(registerForm);
        ElMessage.success("注册成功，请登录");
        toggleMode(); // 切换到登录模式
      } catch (error: any) {
        ElMessage.error(error.message || "注册失败");
      } finally {
        loading.value = false;
      }
    }
  });
};

/* ---------------- 通用逻辑 ---------------- */
const toggleMode = () => {
  isLoginMode.value = !isLoginMode.value;
  // 重置表单
  loginFormRef.value?.resetFields();
  registerFormRef.value?.resetFields();
  clearInterval(timer);
  coutdown.value = 0;
};
</script>
