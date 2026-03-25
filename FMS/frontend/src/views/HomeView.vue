<template>
  <div class="jump-wrap">
    <el-card class="jump-card" shadow="hover">
      <el-icon class="jump-icon"><Loading /></el-icon>
      <div class="jump-title">正在进入对应工作台</div>
      <div class="jump-desc">系统会根据当前登录账号的角色，自动跳转到项目负责人端、管理员端或审批端。</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useRouter } from "vue-router";
import { Loading } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const user = useUserStore();

onMounted(async () => {
  if (!user.loaded && user.token) {
    try {
      await user.fetchMe();
    } catch {
      user.logoutLocal();
      router.replace('/login');
      return;
    }
  }
  router.replace(user.defaultHomePath);
});
</script>

<style scoped>
.jump-wrap {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: linear-gradient(180deg, #f3f7ff 0%, #eff6ff 100%);
  padding: 24px;
}
.jump-card {
  width: 420px;
  border-radius: 20px;
  border: none;
  text-align: center;
}
.jump-icon {
  font-size: 32px;
  color: #2563eb;
}
.jump-title {
  margin-top: 14px;
  font-size: 22px;
  font-weight: 700;
  color: #111827;
}
.jump-desc {
  margin-top: 10px;
  color: #64748b;
  line-height: 1.8;
}
</style>
