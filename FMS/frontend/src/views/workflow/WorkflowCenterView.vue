<template>
  <div class="page-wrap app-page">
    <el-card shadow="hover" class="card app-card">
      <template #header>
        <div class="card-head">
          <div>
            <div class="head-title">审批中心</div>
            <div class="head-tip">统一处理报销单与预算调整单的待办、已办和流转轨迹。</div>
          </div>
          <el-button @click="fetchPage(1)">刷新</el-button>
        </div>
      </template>

      <el-form :inline="true" class="query-row app-query-row" @submit.prevent>
        <el-form-item label="业务类型">
          <el-select v-model="query.bizType" clearable class="w140">
            <el-option label="报销单" value="REIMB" />
            <el-option label="预算调整单" value="BUDGET_ADJUST" />
          </el-select>
        </el-form-item>
        <el-form-item label="节点">
          <el-select v-model="query.nodeCode" clearable class="w160">
            <el-option v-for="item in nodeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" clearable placeholder="单号 / 标题 / 项目名称" class="w260" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchPage(1)">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover" class="card app-card mt16 app-mt16">
      <div class="tab-row">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="我的待办" name="todo" />
          <el-tab-pane label="我的已办" name="done" />
        </el-tabs>
      </div>
      <el-table v-loading="loading" :data="page.records" border stripe>
        <el-table-column prop="bizNo" label="业务单号" min-width="150" />
        <el-table-column label="业务类型" width="110">
          <template #default="{ row }">{{ bizText(row.bizType) }}</template>
        </el-table-column>
        <el-table-column label="当前节点" width="140">
          <template #default="{ row }">{{ nodeText(row.nodeCode) }}</template>
        </el-table-column>
        <el-table-column prop="title" label="标题/原因" min-width="220" show-overflow-tooltip />
        <el-table-column prop="projectName" label="项目" min-width="180" show-overflow-tooltip />
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column :label="activeTab === 'todo' ? '创建时间' : '办理时间'" width="170">
          <template #default="{ row }">{{ activeTab === 'todo' ? (row.createdAt || '-') : (row.completedAt || '-') }}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" :width="activeTab === 'todo' ? 320 : 110">
          <template #default="{ row }">
            <div class="op-list">
              <el-button text @click="openDetail(row)">详情</el-button>
              <template v-if="activeTab === 'todo'">
                <template v-if="row.bizType === 'REIMB' && row.nodeCode === 'PAY_ARCHIVE'">
                  <el-button text type="primary" @click="openPayArchive(row)">支付归档</el-button>
                </template>
                <template v-else>
                  <el-button text type="success" @click="openAction(row, 'approve')">通过</el-button>
                  <el-button text type="danger" @click="openAction(row, 'reject')">驳回</el-button>
                </template>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :current-page="page.pageNo"
          :page-size="page.pageSize"
          :total="page.total"
          @current-change="fetchPage"
        />
      </div>
    </el-card>

    <el-drawer v-model="detailDrawer.visible" title="审批业务详情" size="980px" destroy-on-close>
      <template v-if="detailDrawer.task">
        <div class="drawer-top">
          <div class="badge-line">
            <el-tag>{{ bizText(detailDrawer.task.bizType) }}</el-tag>
            <el-tag type="info">{{ nodeText(detailDrawer.task.nodeCode) }}</el-tag>
            <span>{{ detailDrawer.task.bizNo || '-' }}</span>
          </div>
        </div>

        <template v-if="detailDrawer.task.bizType === 'REIMB' && detailDrawer.reimbDetail">
          <div class="summary-grid">
            <div class="summary-item"><span>标题</span><b>{{ detailDrawer.reimbDetail.title }}</b></div>
            <div class="summary-item"><span>状态</span><b>{{ reimbStatusText(detailDrawer.reimbDetail.status) }}</b></div>
            <div class="summary-item"><span>项目</span><b>{{ detailDrawer.reimbDetail.projectName }}</b></div>
            <div class="summary-item"><span>申请人</span><b>{{ detailDrawer.reimbDetail.applicantName || '-' }}</b></div>
            <div class="summary-item"><span>总金额</span><b>{{ money(detailDrawer.reimbDetail.totalAmount) }}</b></div>
            <div class="summary-item"><span>提交时间</span><b>{{ detailDrawer.reimbDetail.submittedAt || '-' }}</b></div>
            <div class="summary-item full"><span>说明</span><b>{{ detailDrawer.reimbDetail.description || '-' }}</b></div>
          </div>

          <div class="drawer-section">
            <div class="section-title">报销明细</div>
            <el-table :data="detailDrawer.reimbDetail.items || []" border>
              <el-table-column prop="subjectCode" label="科目编码" width="120" />
              <el-table-column prop="subjectName" label="科目名称" min-width="160" />
              <el-table-column prop="itemName" label="明细名称" min-width="180" />
              <el-table-column prop="expenseDate" label="发生日期" width="130" />
              <el-table-column label="金额" width="130">
                <template #default="{ row }">{{ money(row.amount) }}</template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" min-width="150" />
            </el-table>
          </div>

          <div class="drawer-section" v-if="detailDrawer.reimbDetail.payment || detailDrawer.reimbDetail.archive">
            <div class="section-title">支付归档信息</div>
            <div class="summary-grid">
              <div class="summary-item" v-if="detailDrawer.reimbDetail.payment">
                <span>支付登记</span>
                <b>
                  {{ detailDrawer.reimbDetail.payment?.payMethod || '-' }}
                  / {{ money(detailDrawer.reimbDetail.payment?.payAmount) }}
                  / {{ detailDrawer.reimbDetail.payment?.paidAt || '-' }}
                  / {{ detailDrawer.reimbDetail.payment?.voucherNo || '-' }}
                </b>
              </div>
              <div class="summary-item" v-if="detailDrawer.reimbDetail.archive">
                <span>归档登记</span>
                <b>
                  {{ detailDrawer.reimbDetail.archive?.archiveNo || '-' }}
                  / {{ detailDrawer.reimbDetail.archive?.archivedAt || '-' }}
                </b>
              </div>
              <div class="summary-item full" v-if="detailDrawer.reimbDetail.archive?.archivePath">
                <span>档案路径</span>
                <b>{{ detailDrawer.reimbDetail.archive?.archivePath }}</b>
              </div>
            </div>
          </div>
        </template>

        <template v-if="detailDrawer.task.bizType === 'BUDGET_ADJUST' && detailDrawer.adjustDetail">
          <div class="summary-grid">
            <div class="summary-item"><span>调整原因</span><b>{{ detailDrawer.adjustDetail.reason }}</b></div>
            <div class="summary-item"><span>状态</span><b>{{ adjustStatusText(detailDrawer.adjustDetail.status) }}</b></div>
            <div class="summary-item"><span>项目</span><b>{{ detailDrawer.adjustDetail.projectName }}</b></div>
            <div class="summary-item"><span>申请人</span><b>{{ detailDrawer.adjustDetail.applicantName || '-' }}</b></div>
            <div class="summary-item"><span>合计变动</span><b :class="Number(detailDrawer.adjustDetail.totalDelta || 0) >= 0 ? 'delta-up' : 'delta-down'">{{ signedMoney(detailDrawer.adjustDetail.totalDelta) }}</b></div>
            <div class="summary-item"><span>提交时间</span><b>{{ detailDrawer.adjustDetail.submittedAt || '-' }}</b></div>
            <div class="summary-item full"><span>最近意见</span><b>{{ detailDrawer.adjustDetail.lastComment || '-' }}</b></div>
          </div>

          <div class="drawer-section">
            <div class="section-title">调整明细</div>
            <el-table :data="detailDrawer.adjustDetail.lines || []" border>
              <el-table-column prop="subjectCode" label="科目编码" width="120" />
              <el-table-column prop="subjectName" label="科目名称" min-width="180" />
              <el-table-column label="调整金额" width="150">
                <template #default="{ row }">
                  <span :class="Number(row.deltaAmount || 0) >= 0 ? 'delta-up' : 'delta-down'">{{ signedMoney(row.deltaAmount) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" min-width="180" />
            </el-table>
          </div>
        </template>

        <div class="drawer-section">
          <div class="section-title">流程轨迹</div>
          <el-timeline>
            <el-timeline-item v-for="log in detailDrawer.logs" :key="log.id" :timestamp="log.createdAt">
              <div class="log-title">{{ nodeText(log.nodeCode) }} · {{ log.action || '-' }}</div>
              <div class="log-meta">{{ log.actorName || '系统' }}</div>
              <div class="log-comment" v-if="log.comment">{{ log.comment }}</div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </template>
    </el-drawer>

    <el-dialog v-model="actionDialog.visible" :title="actionDialog.mode === 'approve' ? '审批通过' : '审批驳回'" width="520px">
      <el-form label-width="90px">
        <el-form-item label="业务单号">
          <div>{{ actionDialog.task?.bizNo || '-' }}</div>
        </el-form-item>
        <el-form-item label="业务类型">
          <div>{{ bizText(actionDialog.task?.bizType) }}</div>
        </el-form-item>
        <el-form-item label="当前节点">
          <div>{{ nodeText(actionDialog.task?.nodeCode) }}</div>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="actionDialog.comment" type="textarea" :rows="4" :placeholder="actionDialog.mode === 'approve' ? '审批通过时可填写补充说明' : '驳回时请明确填写退回原因'" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="actionDialog.visible = false">取消</el-button>
        <el-button :type="actionDialog.mode === 'approve' ? 'success' : 'danger'" :loading="actionSaving" @click="submitAction">
          {{ actionDialog.mode === 'approve' ? '确认通过' : '确认驳回' }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="payArchiveDialog.visible" title="支付与归档" width="620px">
      <el-form label-width="100px">
        <el-form-item label="业务单号">
          <div>{{ payArchiveDialog.task?.bizNo || '-' }}</div>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="payArchiveDialog.form.payMethod" style="width: 220px">
            <el-option label="银行转账" value="BANK" />
            <el-option label="现金" value="CASH" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付金额">
          <el-input-number v-model="payArchiveDialog.form.payAmount" :min="0" :precision="2" :step="100" />
        </el-form-item>
        <el-form-item label="支付时间">
          <el-input v-model="payArchiveDialog.form.paidAt" placeholder="例如 2026-03-18 10:00:00" />
        </el-form-item>
        <el-form-item label="凭证号">
          <el-input v-model="payArchiveDialog.form.voucherNo" placeholder="可选" />
        </el-form-item>
        <el-divider />
        <el-form-item label="档案编号">
          <el-input v-model="payArchiveDialog.form.archiveNo" placeholder="请输入档案编号" />
        </el-form-item>
        <el-form-item label="档案路径">
          <el-input v-model="payArchiveDialog.form.archivePath" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payArchiveDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="payArchiveSaving" @click="submitPayArchive">确认支付归档</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  apiWfApprove,
  apiWfDonePage,
  apiWfLogs,
  apiWfReject,
  apiWfTodoPage,
  type WfLogVO,
  type WfTaskVO,
} from '@/api/workflow';
import { apiReimburseDetail, type ReimburseDetailVO } from '@/api/reimburse';
import { apiBudgetAdjustDetail, type BudgetAdjustDetailVO } from '@/api/budgetAdjust';
import { apiPayCreateOrUpdate } from '@/api/pay';
import { apiArchiveCreate } from '@/api/archive';
import { useUserStore } from '@/stores/user';

const activeTab = ref<'todo' | 'done'>('todo');
const user = useUserStore();
const loading = ref(false);
const actionSaving = ref(false);
const payArchiveSaving = ref(false);
const page = reactive({ records: [] as WfTaskVO[], total: 0, pageNo: 1, pageSize: 10 });
const query = reactive({ bizType: '', nodeCode: '', keyword: '' });
const nodeOptions = computed(() => {
  if (user.isAdmin) {
    return [
      { label: '二级单位审核', value: 'UNIT_AUDIT' },
      { label: '财务复核', value: 'FIN_REVIEW' },
      { label: '支付归档', value: 'PAY_ARCHIVE' },
    ];
  }
  if (user.isUnitAdmin) return [{ label: '二级单位审核', value: 'UNIT_AUDIT' }];
  if (user.isFinance) {
    return [
      { label: '财务复核', value: 'FIN_REVIEW' },
      { label: '支付归档', value: 'PAY_ARCHIVE' },
    ];
  }
  return [];
});

const detailDrawer = reactive({
  visible: false,
  task: null as WfTaskVO | null,
  reimbDetail: null as ReimburseDetailVO | null,
  adjustDetail: null as BudgetAdjustDetailVO | null,
  logs: [] as WfLogVO[],
});

const actionDialog = reactive({
  visible: false,
  mode: 'approve' as 'approve' | 'reject',
  task: null as WfTaskVO | null,
  comment: '',
});

const payArchiveDialog = reactive({
  visible: false,
  task: null as WfTaskVO | null,
  form: {
    payMethod: 'BANK',
    voucherNo: '',
    payAmount: 0,
    paidAt: '',
    archiveNo: '',
    archivePath: '',
  },
});

function money(v?: number | null) {
  const n = Number(v || 0);
  return `¥${n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}
function signedMoney(v?: number | null) {
  const n = Number(v || 0);
  const base = `¥${Math.abs(n).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  if (n > 0) return `+${base}`;
  if (n < 0) return `-${base}`;
  return base;
}
function bizText(v?: string | null) {
  if (v === 'REIMB') return '报销单';
  if (v === 'BUDGET_ADJUST') return '预算调整单';
  return v || '-';
}
function nodeText(v?: string | null) {
  if (v === 'UNIT_AUDIT') return '二级单位审核';
  if (v === 'FIN_REVIEW') return '财务复核';
  if (v === 'PAY_ARCHIVE') return '支付归档';
  if (v === 'DONE') return '已完成';
  if (v === 'REJECTED') return '已驳回';
  if (v === 'WITHDRAWN') return '已撤销';
  if (v === 'CREATE') return '创建';
  if (v === 'CONTENT') return '内容保存';
  return v || '-';
}
function reimbStatusText(v?: number) {
  return [
    { value: 0, label: '草稿' },
    { value: 1, label: '待二级单位审核' },
    { value: 2, label: '待财务复核' },
    { value: 3, label: '待支付归档' },
    { value: 4, label: '已支付归档' },
    { value: 5, label: '已驳回' },
    { value: 6, label: '已撤销' },
  ].find((x) => x.value === v)?.label || '未知';
}
function adjustStatusText(v?: number) {
  return [
    { value: 0, label: '草稿' },
    { value: 1, label: '待二级单位审核' },
    { value: 2, label: '待财务复核' },
    { value: 3, label: '已生效' },
    { value: 4, label: '已驳回' },
    { value: 5, label: '已撤销' },
  ].find((x) => x.value === v)?.label || '未知';
}

async function fetchPage(pageNo = page.pageNo) {
  loading.value = true;
  try {
    const allowedNodeCodes = nodeOptions.value.map((item) => item.value);
    if (query.nodeCode && !allowedNodeCodes.includes(query.nodeCode)) {
      query.nodeCode = '';
    }
    const payload = {
      pageNo,
      pageSize: page.pageSize,
      bizType: query.bizType || undefined,
      nodeCode: query.nodeCode || undefined,
      keyword: query.keyword || undefined,
    };
    const res = activeTab.value === 'todo' ? await apiWfTodoPage(payload) : await apiWfDonePage(payload);
    page.records = res.records || [];
    page.total = res.total || 0;
    page.pageNo = res.pageNo || pageNo;
    page.pageSize = res.pageSize || page.pageSize;
  } finally {
    loading.value = false;
  }
}

function resetQuery() {
  query.bizType = '';
  query.nodeCode = '';
  query.keyword = '';
  fetchPage(1);
}

function handleTabChange() {
  fetchPage(1);
}

async function openDetail(row: WfTaskVO) {
  detailDrawer.task = row;
  detailDrawer.reimbDetail = null;
  detailDrawer.adjustDetail = null;
  detailDrawer.logs = [];
  if (row.bizType === 'REIMB') {
    detailDrawer.reimbDetail = await apiReimburseDetail(row.bizId);
  } else if (row.bizType === 'BUDGET_ADJUST') {
    detailDrawer.adjustDetail = await apiBudgetAdjustDetail(row.bizId);
  }
  detailDrawer.logs = await apiWfLogs(row.bizType, row.bizId);
  detailDrawer.visible = true;
}

function openAction(row: WfTaskVO, mode: 'approve' | 'reject') {
  actionDialog.task = row;
  actionDialog.mode = mode;
  actionDialog.comment = '';
  actionDialog.visible = true;
}

function openPayArchive(row: WfTaskVO) {
  payArchiveDialog.task = row;
  payArchiveDialog.form.payMethod = 'BANK';
  payArchiveDialog.form.voucherNo = '';
  payArchiveDialog.form.payAmount = 0;
  payArchiveDialog.form.paidAt = '';
  payArchiveDialog.form.archiveNo = '';
  payArchiveDialog.form.archivePath = '';
  payArchiveDialog.visible = true;
}

async function submitAction() {
  if (!actionDialog.task) return;
  if (actionDialog.mode === 'reject' && !actionDialog.comment.trim()) {
    await ElMessageBox.alert('驳回时必须填写审批意见。', '提示', { type: 'warning' });
    return;
  }
  actionSaving.value = true;
  try {
    const payload = {
      bizType: actionDialog.task.bizType,
      bizId: actionDialog.task.bizId,
      nodeCode: actionDialog.task.nodeCode || '',
      comment: actionDialog.comment || undefined,
    };
    if (actionDialog.mode === 'approve') await apiWfApprove(payload);
    else await apiWfReject(payload);
    actionDialog.visible = false;
    await fetchPage(page.pageNo);
    if (detailDrawer.visible && detailDrawer.task?.bizType === payload.bizType && detailDrawer.task?.bizId === payload.bizId) {
      await openDetail(actionDialog.task);
    }
  } finally {
    actionSaving.value = false;
  }
}

async function submitPayArchive() {
  if (!payArchiveDialog.task) return;
  if (!payArchiveDialog.form.payMethod || !payArchiveDialog.form.payAmount || !payArchiveDialog.form.paidAt || !payArchiveDialog.form.archiveNo) {
    ElMessage.warning('请先填写完整的支付与归档信息');
    return;
  }
  payArchiveSaving.value = true;
  try {
    await apiPayCreateOrUpdate({
      reimbId: payArchiveDialog.task.bizId,
      payMethod: payArchiveDialog.form.payMethod,
      voucherNo: payArchiveDialog.form.voucherNo || undefined,
      payAmount: Number(payArchiveDialog.form.payAmount),
      paidAt: payArchiveDialog.form.paidAt,
    });
    await apiArchiveCreate({
      reimbId: payArchiveDialog.task.bizId,
      archiveNo: payArchiveDialog.form.archiveNo,
      archivePath: payArchiveDialog.form.archivePath || undefined,
    });
    ElMessage.success('支付归档已完成');
    payArchiveDialog.visible = false;
    await fetchPage(page.pageNo);
    if (detailDrawer.visible && detailDrawer.task?.bizId === payArchiveDialog.task.bizId) {
      await openDetail(payArchiveDialog.task);
    }
  } finally {
    payArchiveSaving.value = false;
  }
}

onMounted(async () => {
  await fetchPage(1);
});
</script>

<style scoped>
.card { border-radius:18px; border:none; }
.mt16 { margin-top: 16px; }
.card-head { display:flex; justify-content:space-between; align-items:center; gap:16px; }
.head-title { font-size: 22px; font-weight: 700; color: #111827; }
.head-tip { margin-top: 6px; color: #6b7280; font-size: 13px; }
.query-row { margin-top: 2px; }
.tab-row :deep(.el-tabs__header) { margin-bottom: 12px; }
.w140 { width: 140px; }
.w160 { width: 160px; }
.w260 { width: 260px; }
.op-list { display:flex; flex-wrap:wrap; gap:6px 2px; }
.pager { display:flex; justify-content:flex-end; margin-top:16px; }
.drawer-top { padding-bottom: 8px; border-bottom: 1px solid #e5e7eb; }
.badge-line { display:flex; align-items:center; gap:10px; flex-wrap:wrap; font-weight:600; color:#0f172a; }
.summary-grid { display:grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap:14px; margin-top:16px; }
.summary-item { border-radius:14px; background:#f8fafc; padding:14px 16px; display:flex; flex-direction:column; gap:8px; }
.summary-item.full { grid-column:1 / -1; }
.summary-item span { color:#64748b; font-size:13px; }
.summary-item b { color:#111827; line-height:1.8; word-break:break-word; }
.drawer-section { margin-top:24px; }
.section-title { font-size:18px; font-weight:700; color:#111827; margin-bottom:12px; }
.log-title { font-weight:700; color:#111827; }
.log-meta { margin-top:4px; color:#64748b; font-size:13px; }
.log-comment { margin-top:6px; color:#334155; line-height:1.8; }
.delta-up { color:#059669; font-weight:700; }
.delta-down { color:#dc2626; font-weight:700; }
@media (max-width: 980px) {
  .card-head { flex-direction:column; align-items:flex-start; }
  .w140, .w160, .w260 { width: 100%; }
  .summary-grid { grid-template-columns:1fr; }
}
</style>
