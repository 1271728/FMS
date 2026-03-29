<template>
  <div class="page-wrap app-page">
    <div class="page-head">
      <div>
        <div class="page-title">工作台</div>
        <div class="page-subtitle">当前角色：{{ user.primaryRoleLabel }}</div>
      </div>
      <el-tag>未读消息 {{ stats.unreadCount || 0 }}</el-tag>
    </div>

    <div v-if="stats.cards?.length" class="stats-grid mt16">
      <el-card v-for="item in stats.cards" :key="item.key" shadow="hover" class="stat-card">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value">{{ formatValue(item.value) }}</div>
      </el-card>
    </div>

    <el-card shadow="hover" class="card app-card mt16">
      <template #header><div class="card-head app-card-head"><span>快捷入口</span></div></template>
      <div class="entry-grid">
        <div v-for="item in quickLinks" :key="item.path" class="entry-item" @click="go(item.path)">
          <div class="entry-name">{{ item.label }}</div>
          <div class="entry-tip">{{ item.tip }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { apiDashboardStats, type DashboardStatsResp } from '@/api/dashboard';

const router = useRouter();
const user = useUserStore();
const stats = reactive<DashboardStatsResp>({ role: '', unreadCount: 0, cards: [] });

const quickLinks = computed(() => {
  const links: Array<{ path: string; label: string; tip: string }> = [];
  if (user.isAdmin) links.push({ path: '/admin/users', label: '用户与权限', tip: '维护组织、账号状态和角色配置' });
  if (user.canViewProjectModule) links.push({ path: '/project/manage', label: '项目管理', tip: '查看项目、成员与立项状态' });
  if (user.canViewBudgetOverview) links.push({ path: '/budget/overview', label: '预算总览', tip: '查看项目预算、余额和执行率' });
  if (user.canManageBudgetAdjust) links.push({ path: '/budget/adjust', label: '预算调整单', tip: '发起或处理预算调整流程' });
  if (user.canManageReimburse) links.push({ path: '/reimburse/manage', label: '报销单管理', tip: '维护报销单、明细与提交流程' });
  if (user.canUseWorkflowCenter) links.push({ path: '/workflow/center', label: '审批中心', tip: '集中处理待办、已办和审批轨迹' });
  links.push({ path: '/msg/center', label: '消息中心', tip: '查看驳回、完成和流程提醒消息' });
  return links;
});

function go(path: string) { router.push(path); }
function formatValue(v: number | string) {
  if (typeof v === 'number') return v.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
  return v;
}
async function loadStats() {
  const res = await apiDashboardStats();
  stats.role = res.role;
  stats.unreadCount = Number(res.unreadCount || 0);
  stats.cards = Array.isArray(res.cards) ? res.cards : [];
}
onMounted(loadStats);
</script>

<style scoped>
.page-head { display:flex; justify-content:space-between; align-items:center; gap:16px; }
.page-title { font-size: 24px; font-weight: 700; color:#111827; }
.page-subtitle { margin-top: 6px; color:#6b7280; }
.card, .stat-card { border-radius: 16px; border:none; }
.mt16 { margin-top: 16px; }
.stats-grid { display:grid; grid-template-columns: repeat(4, minmax(0,1fr)); gap: 14px; }
.stat-label { font-size: 13px; color:#6b7280; }
.stat-value { margin-top: 12px; font-size: 28px; font-weight: 800; color:#111827; }
.entry-grid { display:grid; grid-template-columns: repeat(3, minmax(0,1fr)); gap: 14px; }
.entry-item { padding: 18px; border-radius: 14px; border: 1px solid #e5e7eb; background:#fafafa; cursor:pointer; transition:all .18s ease; }
.entry-item:hover { transform: translateY(-1px); border-color:#cfd4dc; background:#fff; }
.entry-name { font-size: 17px; font-weight: 700; color:#111827; }
.entry-tip { margin-top: 8px; color:#6b7280; line-height:1.8; font-size:13px; }
@media (max-width: 1200px) { .stats-grid { grid-template-columns: repeat(2, minmax(0,1fr)); } }
@media (max-width: 960px) { .entry-grid, .stats-grid { grid-template-columns: 1fr; } .page-head { flex-direction:column; align-items:flex-start; } }
</style>
