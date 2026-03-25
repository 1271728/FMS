<template>
  <div class="login-wrap">
    <el-card class="login-card" shadow="never">
      <div class="system-title">科研经费报销管理系统</div>
      <div class="system-subtitle">统一登录后，系统将根据账号角色自动进入对应工作台。</div>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" @submit.prevent>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" clearable placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password placeholder="请输入密码" @keyup.enter="submit" />
        </el-form-item>

        <el-button type="primary" class="submit-btn" :loading="loading" @click="submit">
          登录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const route = useRoute();
const user = useUserStore();

const loading = ref(false);
const formRef = ref<FormInstance>();
const form = reactive({ username: "", password: "" });

const rules: FormRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

async function submit() {
  await formRef.value?.validate();
  loading.value = true;
  try {
    await user.login(form.username.trim(), form.password);
    ElMessage.success(`登录成功，正在进入${user.primaryRoleLabel}`);
    await router.replace((route.query.redirect as string) || user.defaultHomePath);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background: #f5f6f8;
}
.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  text-align: left;
}
.system-title {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
}
.system-subtitle {
  margin: 10px 0 24px;
  color: #6b7280;
  line-height: 1.8;
}
.submit-btn {
  width: 100%;
  height: 42px;
  border-radius: 10px;
  margin-top: 8px;
}
</style>
