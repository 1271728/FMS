<template>
  <div class="page-wrap app-page">
    <div class="page-head">
      <div>
        <div class="page-title">消息中心</div>
        <div class="page-subtitle">查看审批提醒、驳回通知与流程完成提醒。</div>
      </div>
      <div class="toolbar-right">
        <el-button @click="fetchPage(1)">刷新</el-button>
        <el-button type="primary" plain @click="markAllCurrent" :disabled="!unreadIds.length">本页全部已读</el-button>
      </div>
    </div>

    <el-card shadow="hover" class="card mt16 app-card app-mt16">
      <el-form :inline="true" class="query-row" @submit.prevent>
        <el-form-item label="阅读状态">
          <el-select v-model="query.isRead" clearable style="width: 140px">
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="消息类型">
          <el-input v-model="query.msgType" clearable placeholder="如 REJECT / DONE" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchPage(1)">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="page.records" border stripe>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isRead ? 'info' : 'danger'">{{ row.isRead ? '已读' : '未读' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="content" label="内容" min-width="340" show-overflow-tooltip />
        <el-table-column prop="msgType" label="类型" width="120" />
        <el-table-column prop="createdAt" label="时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button text @click="openBiz(row)" :disabled="!row.relatedBizType || !row.relatedBizId">查看业务</el-button>
            <el-button text type="primary" @click="markRead(row)" :disabled="!!row.isRead">已读</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination background layout="total, prev, pager, next" :current-page="page.pageNo" :page-size="page.pageSize" :total="page.total" @current-change="fetchPage" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { apiMsgMarkRead, apiMsgMarkReadBatch, apiMsgPage, type MsgVO } from '@/api/msg';

const router = useRouter();
const loading = ref(false);
const page = reactive({ records: [] as MsgVO[], total: 0, pageNo: 1, pageSize: 10 });
const query = reactive<{ isRead?: number; msgType: string }>({ isRead: undefined, msgType: '' });
const unreadIds = computed(() => page.records.filter((x) => !x.isRead).map((x) => x.id));

async function fetchPage(pageNo = page.pageNo) {
  loading.value = true;
  try {
    const res = await apiMsgPage({ pageNo, pageSize: page.pageSize, isRead: query.isRead, msgType: query.msgType || undefined });
    page.records = res.records || [];
    page.total = res.total || 0;
    page.pageNo = res.pageNo || pageNo;
    page.pageSize = res.pageSize || page.pageSize;
  } finally { loading.value = false; }
}
function resetQuery() { query.isRead = undefined; query.msgType = ''; fetchPage(1); }
async function markRead(row: MsgVO) { if (row.isRead) return; await apiMsgMarkRead(row.id); ElMessage.success('已标记为已读'); await fetchPage(page.pageNo); }
async function markAllCurrent() { if (!unreadIds.value.length) return; await apiMsgMarkReadBatch(unreadIds.value); ElMessage.success('本页未读消息已全部标记为已读'); await fetchPage(page.pageNo); }
function openBiz(row: MsgVO) { if (!row.relatedBizType || !row.relatedBizId) return; if (row.relatedBizType === 'REIMB') router.push('/reimburse/manage'); else if (row.relatedBizType === 'BUDGET_ADJUST') router.push('/budget/adjust'); else router.push('/home'); }
onMounted(() => fetchPage(1));
</script>

<style scoped>
.page-head { display:flex; justify-content:space-between; align-items:center; gap:16px; }
.page-title { font-size: 24px; font-weight: 700; color:#111827; }
.page-subtitle { margin-top:8px; color:#6b7280; line-height:1.8; }
.toolbar-right { display:flex; gap:10px; }
.mt16 { margin-top:16px; }
.card { border-radius:16px; border:none; }
.query-row { margin-top: 4px; }
.pager { display:flex; justify-content:flex-end; margin-top:16px; }
@media (max-width: 960px) { .page-head { flex-direction:column; align-items:flex-start; } }
</style>
