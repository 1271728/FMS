<template>
  <div class="home-page">
    <el-card shadow="never" class="home-card">
      <div class="home-head">
        <div class="home-title">项目负责人端</div>
      </div>

      <div class="entry-grid">
        <div class="entry-item" @click="go('/project/manage')">
          <div class="entry-name">项目管理</div>
        </div>
        <div class="entry-item" @click="go('/budget/overview')">
          <div class="entry-name">预算总览</div>
        </div>
        <div class="entry-item" @click="go('/budget/adjust')">
          <div class="entry-name">预算调整单</div>
        </div>
        <div class="entry-item" @click="go('/reimburse/manage')">
          <div class="entry-name">报销单管理</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
const router = useRouter();
function go(path: string) {
  router.push(path);
}
</script>

<style scoped>
.home-page {
  min-height: calc(100vh - 132px);
}
.home-card {
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  text-align: left;
}
.home-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}
.home-title {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
}
.entry-grid {
  margin-top: 20px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}
.entry-item {
  padding: 18px;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  background: #fafafa;
  cursor: pointer;
  transition: all .18s ease;
}
.entry-item:hover {
  transform: translateY(-1px);
  border-color: #cfd4dc;
  background: #ffffff;
}
.entry-name {
  font-size: 17px;
  font-weight: 700;
  color: #111827;
}
@media (max-width: 960px) {
  .entry-grid {
    grid-template-columns: 1fr;
  }
}
</style>
