<template>
  <div class="page-wrap app-page">
    <el-card shadow="hover" class="card app-card">
      <el-form :model="query" inline>
        <el-form-item label="关键字">
          <el-input v-model="query.keyword" clearable placeholder="单号 / 原因 / 项目名" style="width:220px" />
        </el-form-item>
        <el-form-item label="项目">
          <el-select v-model="query.projectId" clearable filterable placeholder="全部" style="width:240px">
            <el-option v-for="p in projectOptions" :key="p.id" :label="`${p.projectCode}｜${p.projectName}`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width:170px">
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="query.todoOnly">只看待办口径</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchPage(1)">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button @click="goOverview">预算总览</el-button>
          <el-button type="primary" @click="openCreate">新建调整单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover" class="card mt16 app-card app-mt16">
      <el-table :data="page.records" border v-loading="loading">
        <el-table-column prop="adjustNo" label="调整单号" width="160" />
        <el-table-column label="项目" min-width="220">
          <template #default="{ row }">
            <div class="project-cell">
              <b>{{ row.projectName }}</b>
              <span>{{ row.projectCode }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人" width="110" />
        <el-table-column prop="unitName" label="单位" width="140" />
        <el-table-column prop="reason" label="调整原因" min-width="220" show-overflow-tooltip />
        <el-table-column label="合计变动" width="150">
          <template #default="{ row }">
            <span :class="Number(row.totalDelta || 0) >= 0 ? 'delta-up' : 'delta-down'">{{ signedMoney(row.totalDelta) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submittedAt" label="提交时间" width="170" />
        <el-table-column label="操作" fixed="right" width="340">
          <template #default="{ row }">
            <div class="op-list">
              <el-button text @click="openDetail(row)">详情</el-button>
              <el-button text v-if="row.canEdit === 1" @click="openEdit(row)">编辑</el-button>
              <el-button text type="danger" v-if="row.canEdit === 1" @click="handleDelete(row)">删除</el-button>
              <el-button text type="primary" v-if="row.canSubmit === 1" @click="handleSubmit(row)">提交</el-button>
              <el-button text type="warning" v-if="row.canWithdraw === 1" @click="handleWithdraw(row)">撤销</el-button>
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

    <el-dialog v-model="editDialog.visible" :title="editDialog.mode === 'create' ? '新建预算调整单' : '编辑预算调整单'" width="980px" destroy-on-close>
      <el-form label-width="96px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="项目">
              <el-select v-model="editDialog.form.projectId" filterable placeholder="请选择项目" style="width:100%">
                <el-option v-for="p in projectOptions" :key="p.id" :label="`${p.projectCode}｜${p.projectName}`" :value="p.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合计变动">
              <el-input :model-value="signedMoney(formTotal)" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="调整原因">
          <el-input v-model="editDialog.form.reason" type="textarea" :rows="3" placeholder="请说明预算调整背景、执行阶段变化或资金结构调整原因" />
        </el-form-item>

        <div class="section-head">
          <div class="section-title">调整明细</div>
          <el-button type="primary" plain @click="addLine">新增明细</el-button>
        </div>

        <el-table :data="editDialog.form.lines" border>
          <el-table-column label="预算科目" min-width="220">
            <template #default="{ row }">
              <el-select v-model="row.subjectId" filterable placeholder="请选择预算科目" style="width:100%">
                <el-option v-for="s in subjectOptions" :key="s.id" :label="`${s.code}｜${s.name}`" :value="s.id" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="调整金额" width="180">
            <template #default="{ row }">
              <el-input-number v-model="row.deltaAmount" :precision="2" :step="100" style="width:100%" />
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="220">
            <template #default="{ row }">
              <el-input v-model="row.remark" placeholder="可填写细化说明" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" fixed="right">
            <template #default="{ $index }">
              <el-button text type="danger" @click="removeLine($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveForm">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailDrawer.visible" title="预算调整单详情" size="860px" destroy-on-close>
      <template v-if="detailDrawer.data">
        <div class="detail-grid">
          <div class="detail-item"><span>单号</span><b>{{ detailDrawer.data.adjustNo }}</b></div>
          <div class="detail-item"><span>状态</span><b>{{ statusText(detailDrawer.data.status) }}</b></div>
          <div class="detail-item"><span>项目</span><b>{{ detailDrawer.data.projectName }}</b></div>
          <div class="detail-item"><span>项目编号</span><b>{{ detailDrawer.data.projectCode || '-' }}</b></div>
          <div class="detail-item"><span>申请人</span><b>{{ detailDrawer.data.applicantName || '-' }}</b></div>
          <div class="detail-item"><span>单位</span><b>{{ detailDrawer.data.unitName || '-' }}</b></div>
          <div class="detail-item"><span>合计变动</span><b :class="Number(detailDrawer.data.totalDelta || 0) >= 0 ? 'delta-up' : 'delta-down'">{{ signedMoney(detailDrawer.data.totalDelta) }}</b></div>
          <div class="detail-item"><span>提交时间</span><b>{{ detailDrawer.data.submittedAt || '-' }}</b></div>
          <div class="detail-item full"><span>当前节点</span><b>{{ nodeText(detailDrawer.data.currentNode) }}</b></div>
          <div class="detail-item full"><span>调整原因</span><b>{{ detailDrawer.data.reason || '-' }}</b></div>
          <div class="detail-item full" v-if="detailDrawer.data.lastComment"><span>最近意见</span><b>{{ detailDrawer.data.lastComment }}</b></div>
        </div>

        <div class="drawer-section">
          <div class="section-title">调整明细</div>
          <el-table :data="detailDrawer.data.lines || []" border>
            <el-table-column prop="subjectCode" label="科目编码" width="120" />
            <el-table-column prop="subjectName" label="科目名称" min-width="180" />
            <el-table-column label="调整金额" width="160">
              <template #default="{ row }">
                <span :class="Number(row.deltaAmount || 0) >= 0 ? 'delta-up' : 'delta-down'">{{ signedMoney(row.deltaAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="180" />
          </el-table>
        </div>

        <div class="drawer-section">
          <div class="section-title">审批轨迹</div>
          <el-timeline>
            <el-timeline-item v-for="log in detailDrawer.data.wfLogs || []" :key="log.id" :timestamp="log.createdAt">
              <div class="log-title">{{ nodeText(log.nodeCode) }} · {{ log.action || '-' }}</div>
              <div class="log-meta">{{ log.actorName || '系统' }}</div>
              <div class="log-comment" v-if="log.comment">{{ log.comment }}</div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';
import { apiBudgetSubjectTree, type BudgetSubjectNode } from '@/api/budget';
import {
  apiBudgetAdjustCreate,
  apiBudgetAdjustDelete,
  apiBudgetAdjustDetail,
  apiBudgetAdjustPage,
  apiBudgetAdjustSubmit,
  apiBudgetAdjustUpdate,
  apiBudgetAdjustWithdraw,
  type BudgetAdjustCreateReq,
  type BudgetAdjustDetailVO,
  type BudgetAdjustLineReq,
  type BudgetAdjustVO,
} from '@/api/budgetAdjust';
import { apiProjectPage, type ProjectVO } from '@/api/project';

interface SubjectOption {
  id: number;
  code: string;
  name: string;
}

const router = useRouter();
const loading = ref(false);
const saving = ref(false);
const page = reactive({ records: [] as BudgetAdjustVO[], total: 0, pageNo: 1, pageSize: 10 });
const query = reactive({ keyword: '', status: undefined as number | undefined, projectId: undefined as number | undefined, todoOnly: false });
const projectOptions = ref<ProjectVO[]>([]);
const subjectOptions = ref<SubjectOption[]>([]);

const statusOptions = [
  { value: 0, label: '草稿' },
  { value: 1, label: '待二级单位审核' },
  { value: 2, label: '待财务复核' },
  { value: 3, label: '已生效' },
  { value: 4, label: '已驳回' },
  { value: 5, label: '已撤销' },
];

const editDialog = reactive({
  visible: false,
  mode: 'create' as 'create' | 'edit',
  form: {
    id: undefined as number | undefined,
    projectId: null as number | null,
    reason: '',
    lines: [] as BudgetAdjustLineReq[],
  },
});

const detailDrawer = reactive({ visible: false, data: null as BudgetAdjustDetailVO | null });

const formTotal = computed(() => editDialog.form.lines.reduce((sum, item) => sum + Number(item.deltaAmount || 0), 0));

function money(v?: number | null) {
  const n = Number(v || 0);
  return `¥${Math.abs(n).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}
function signedMoney(v?: number | null) {
  const n = Number(v || 0);
  if (n > 0) return `+${money(n)}`;
  if (n < 0) return `-${money(n)}`;
  return money(0);
}
function statusText(v?: number) {
  return statusOptions.find((s) => s.value === v)?.label || '未知';
}
function statusTag(v?: number) {
  if (v === 3) return 'success';
  if (v === 4 || v === 5) return 'danger';
  if (v === 1 || v === 2) return 'warning';
  return 'info';
}
function nodeText(v?: string | null) {
  if (v === 'UNIT_AUDIT') return '二级单位审核';
  if (v === 'FIN_REVIEW') return '财务复核';
  if (v === 'DONE') return '已完成';
  if (v === 'REJECTED') return '已驳回';
  if (v === 'WITHDRAWN') return '已撤销';
  if (v === 'CREATE') return '创建';
  if (v === 'CONTENT') return '内容保存';
  return v || '-';
}

function goOverview() { router.push('/budget/overview'); }

function flattenTree(list: BudgetSubjectNode[], out: SubjectOption[] = []) {
  for (const item of list || []) {
    out.push({ id: item.id, code: item.code, name: item.name });
    if (item.children?.length) flattenTree(item.children, out);
  }
  return out;
}

async function loadProjects() {
  const res = await apiProjectPage({ pageNo: 1, pageSize: 300, status: 3 });
  projectOptions.value = res.records || [];
}

async function loadSubjects() {
  const tree = await apiBudgetSubjectTree();
  subjectOptions.value = flattenTree(tree || []);
}

async function fetchPage(pageNo = page.pageNo) {
  loading.value = true;
  try {
    const res = await apiBudgetAdjustPage({
      pageNo,
      pageSize: page.pageSize,
      keyword: query.keyword || undefined,
      projectId: query.projectId,
      status: query.status,
      todoOnly: query.todoOnly || undefined,
    });
    page.records = res.records || [];
    page.total = res.total || 0;
    page.pageNo = res.pageNo || pageNo;
    page.pageSize = res.pageSize || page.pageSize;
  } finally {
    loading.value = false;
  }
}

function resetQuery() {
  query.keyword = '';
  query.projectId = undefined;
  query.status = undefined;
  query.todoOnly = false;
  fetchPage(1);
}

function resetForm() {
  editDialog.form.id = undefined;
  editDialog.form.projectId = null;
  editDialog.form.reason = '';
  editDialog.form.lines = [{ subjectId: null, deltaAmount: null, remark: '' }];
}

function openCreate() {
  editDialog.mode = 'create';
  resetForm();
  editDialog.visible = true;
}

async function openEdit(row: BudgetAdjustVO) {
  const detail = await apiBudgetAdjustDetail(row.id);
  editDialog.mode = 'edit';
  editDialog.form.id = detail.id;
  editDialog.form.projectId = detail.projectId;
  editDialog.form.reason = detail.reason || '';
  editDialog.form.lines = (detail.lines || []).map((x) => ({
    subjectId: x.subjectId,
    deltaAmount: Number(x.deltaAmount),
    remark: x.remark || '',
  }));
  if (!editDialog.form.lines.length) editDialog.form.lines = [{ subjectId: null, deltaAmount: null, remark: '' }];
  editDialog.visible = true;
}

async function openDetail(row: BudgetAdjustVO) {
  detailDrawer.data = await apiBudgetAdjustDetail(row.id);
  detailDrawer.visible = true;
}

function addLine() {
  editDialog.form.lines.push({ subjectId: null, deltaAmount: null, remark: '' });
}

function removeLine(index: number) {
  if (editDialog.form.lines.length === 1) {
    editDialog.form.lines[0] = { subjectId: null, deltaAmount: null, remark: '' };
    return;
  }
  editDialog.form.lines.splice(index, 1);
}

async function saveForm() {
  const payload: BudgetAdjustCreateReq = {
    projectId: editDialog.form.projectId,
    reason: editDialog.form.reason,
    lines: editDialog.form.lines,
  };
  saving.value = true;
  try {
    if (editDialog.mode === 'create') {
      await apiBudgetAdjustCreate(payload);
    } else {
      await apiBudgetAdjustUpdate({ id: editDialog.form.id as number, ...payload });
    }
    editDialog.visible = false;
    await fetchPage(editDialog.mode === 'create' ? 1 : page.pageNo);
  } finally {
    saving.value = false;
  }
}

async function handleDelete(row: BudgetAdjustVO) {
  await ElMessageBox.confirm(`确认删除预算调整单【${row.adjustNo}】吗？`, '提示', { type: 'warning' });
  await apiBudgetAdjustDelete(row.id);
  await fetchPage(page.pageNo);
}

async function handleSubmit(row: BudgetAdjustVO) {
  await ElMessageBox.confirm(`确认提交预算调整单【${row.adjustNo}】吗？`, '提示', { type: 'warning' });
  await apiBudgetAdjustSubmit(row.id);
  await fetchPage(page.pageNo);
}

async function handleWithdraw(row: BudgetAdjustVO) {
  await ElMessageBox.confirm(`确认撤销预算调整单【${row.adjustNo}】吗？`, '提示', { type: 'warning' });
  await apiBudgetAdjustWithdraw(row.id);
  await fetchPage(page.pageNo);
}

onMounted(async () => {
  await Promise.all([loadProjects(), loadSubjects()]);
  await fetchPage(1);
});
</script>

<style scoped>
.mt16 { margin-top:16px; }
.card { border-radius:18px; border:none; }
.note-line { margin-top: 6px; color:#64748b; font-size:13px; }
.project-cell { display:flex; flex-direction:column; gap:6px; }
.project-cell b { color:#0f172a; }
.project-cell span { color:#64748b; font-size:12px; }
.op-list { display:flex; flex-wrap:wrap; gap:6px 2px; }
.pager { display:flex; justify-content:flex-end; margin-top:16px; }
.section-head { display:flex; align-items:center; gap:14px; margin:16px 0 12px; }
.section-title { font-size:18px; font-weight:700; color:#1f2937; }
.section-tip { color:#64748b; font-size:13px; flex:1; }
.detail-grid { display:grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap:14px; }
.detail-item { border-radius:14px; background:#f8fafc; padding:14px 16px; display:flex; flex-direction:column; gap:8px; }
.detail-item.full { grid-column:1 / -1; }
.detail-item span { color:#64748b; font-size:13px; }
.detail-item b { color:#111827; line-height:1.8; word-break:break-word; }
.drawer-section { margin-top:22px; }
.log-title { font-weight:700; color:#111827; }
.log-meta { margin-top:4px; color:#64748b; font-size:13px; }
.log-comment { margin-top:6px; color:#334155; line-height:1.8; }
.delta-up { color:#059669; font-weight:700; }
.delta-down { color:#dc2626; font-weight:700; }
@media (max-width: 980px) {
  .section-head { flex-direction:column; align-items:flex-start; }
  .detail-grid { grid-template-columns:1fr; }
}
</style>
