<template>
  <div class="page-wrap app-page">
    <el-card shadow="hover" class="card app-card">
      <el-form :model="query" inline>
        <el-form-item label="关键字">
          <el-input v-model="query.keyword" placeholder="单号/标题/项目名" clearable style="width:220px" />
        </el-form-item>
        <el-form-item label="项目">
          <el-select v-model="query.projectId" placeholder="全部" clearable filterable style="width:240px">
            <el-option v-for="p in projectOptions" :key="p.id" :label="`${p.projectCode}｜${p.projectName}`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:170px">
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="query.todoOnly">只看待我审批</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchPage(1)">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="primary" @click="openCreate">新增报销单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover" class="card mt16 app-card app-mt16">
      <template #header>
        <div class="card-head app-card-head">
          <span>报销单列表</span>
          <el-button text type="primary" @click="fetchPage(page.pageNo)">刷新</el-button>
        </div>
      </template>

      <el-table :data="page.records" border v-loading="loading">
        <el-table-column prop="reimburseNo" label="报销单号" width="170" />
        <el-table-column prop="title" label="标题" min-width="190" />
        <el-table-column label="项目" min-width="220">
          <template #default="{ row }">
            <div class="project-cell">
              <b>{{ row.projectName }}</b>
              <span>{{ row.projectCode }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column prop="unitName" label="单位" width="130" />
        <el-table-column label="总金额" width="140">
          <template #default="{ row }">{{ money(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column prop="itemCount" label="明细数" width="90" />
        <el-table-column label="状态" width="140">
          <template #default="{ row }"><el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="submittedAt" label="提交时间" width="170" />
        <el-table-column label="操作" fixed="right" width="400">
          <template #default="{ row }">
            <div class="op-list">
              <el-button text @click="openDetail(row)">详情</el-button>
              <el-button text v-if="row.canEdit === 1" @click="openEdit(row)">编辑</el-button>
              <el-button text type="primary" v-if="canShowSubmit(row)" @click="handleSubmit(row)">提交</el-button>
              <el-button text type="warning" v-if="row.canWithdraw === 1" @click="handleWithdraw(row)">撤销</el-button>
              <el-button text type="success" v-if="row.canLeaderAudit === 1 || row.canUnitAudit === 1 || row.canFinanceAudit === 1" @click="openAudit(row)">审批</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination background layout="total, prev, pager, next" :current-page="page.pageNo" :page-size="page.pageSize" :total="page.total" @current-change="fetchPage" />
      </div>
    </el-card>

    <el-dialog v-model="editDialog.visible" :title="editDialog.mode === 'create' ? '新增报销单' : '编辑报销单'" width="1120px" destroy-on-close>
      <el-form label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="项目">
              <el-select v-model="editDialog.form.projectId" filterable placeholder="请选择项目" style="width:100%" @change="handleFormProjectChange">
                <el-option v-for="p in projectOptions" :key="p.id" :label="`${p.projectCode}｜${p.projectName}`" :value="p.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标题">
              <el-input v-model="editDialog.form.title" placeholder="如：设备购置报销 / 差旅报销" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="说明">
          <el-input v-model="editDialog.form.description" type="textarea" :rows="3" placeholder="可填写用途说明、报销原因等" />
        </el-form-item>

        <div class="section-head">
          <div>
            <div class="section-title">发票 / 凭证附件</div>
            <div class="section-tip">提交审批前至少上传一份发票或凭证，支持多文件。</div>
          </div>
          <div class="upload-actions">
            <el-upload :show-file-list="false" :auto-upload="false" accept="*" :on-change="(uploadFile: any) => handleUpload(uploadFile?.raw as File, 'INVOICE')">
              <el-button type="primary" plain>上传发票</el-button>
            </el-upload>
            <el-upload :show-file-list="false" :auto-upload="false" accept="*" :on-change="(uploadFile: any) => handleUpload(uploadFile?.raw as File, 'VOUCHER')">
              <el-button type="primary" plain>上传凭证</el-button>
            </el-upload>
          </div>
        </div>
        <div class="attach-list" v-if="editDialog.form.attachments.length">
          <div v-for="(item, index) in editDialog.form.attachments" :key="`${item.fileUrl}-${index}`" class="attach-item">
            <div class="attach-main">
              <el-tag size="small">{{ attachmentLabel(item.fileCategory) }}</el-tag>
              <a :href="item.fileUrl" target="_blank" class="attach-link">{{ item.originalName || item.fileUrl }}</a>
            </div>
            <el-button text type="danger" @click="removeAttachment(index)">删除</el-button>
          </div>
        </div>

        <div class="section-head mt12">
          <div>
            <div class="section-title">报销明细</div>
            <div class="section-tip" v-if="hasTravelSubsidyRows">差旅类科目支持补助计算：总额 = 基础金额 + 差旅天数 × 每日补助。</div>
            <div class="section-tip" v-else>当前未选择差旅类科目，仅需填写基础金额。</div>
          </div>
          <el-button type="primary" plain @click="addItem">新增明细</el-button>
        </div>
        <el-table :data="editDialog.form.items" border>
          <el-table-column label="预算科目" min-width="180">
            <template #default="{ row }">
              <el-select v-model="row.subjectId" filterable placeholder="选择预算科目" style="width:100%" @change="handleSubjectChange(row)">
                <el-option v-for="b in budgetOptions" :key="b.subjectId" :label="`${b.subjectCode}｜${b.subjectName}（可用${money(b.availableAmount)}）`" :value="b.subjectId" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="明细名称" min-width="160">
            <template #default="{ row }">
              <el-input v-model="row.itemName" placeholder="如：差旅费、会议费、设备款" />
            </template>
          </el-table-column>
          <el-table-column label="发生日期" width="150">
            <template #default="{ row }">
              <el-date-picker v-model="row.expenseDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" />
            </template>
          </el-table-column>
          <el-table-column label="基础金额" width="140">
            <template #default="{ row }">
              <el-input-number v-model="row.baseAmount" :min="0" :precision="2" :step="100" style="width:100%" @change="recalcItem(row)" />
            </template>
          </el-table-column>
          <el-table-column v-if="hasTravelSubsidyRows" label="差旅天数" width="120">
            <template #default="{ row }">
              <el-input-number
                v-if="isTravelSubject(row.subjectId)"
                v-model="row.travelDays"
                :min="0"
                :precision="0"
                :step="1"
                style="width:100%"
                @change="recalcItem(row)"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column v-if="hasTravelSubsidyRows" label="每日补助" width="140">
            <template #default="{ row }">
              <el-input-number
                v-if="isTravelSubject(row.subjectId)"
                v-model="row.subsidyPerDay"
                :min="0"
                :precision="2"
                :step="10"
                style="width:100%"
                @change="recalcItem(row)"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column v-if="hasTravelSubsidyRows" label="补助金额" width="130">
            <template #default="{ row }">{{ isTravelSubject(row.subjectId) ? money(row.subsidyAmount) : '-' }}</template>
          </el-table-column>
          <el-table-column label="总金额" width="130">
            <template #default="{ row }">{{ money(row.amount) }}</template>
          </el-table-column>
          <el-table-column label="备注" min-width="160">
            <template #default="{ row }">
              <el-input v-model="row.remark" placeholder="可选" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" fixed="right">
            <template #default="{ $index }"><el-button text type="danger" @click="removeItem($index)">删除</el-button></template>
          </el-table-column>
        </el-table>

        <div class="total-box">合计金额：<b>{{ money(formTotal) }}</b></div>
      </el-form>
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveForm">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailDrawer.visible" title="报销单详情" size="900px">
      <template v-if="detailDrawer.data">
        <div class="detail-grid">
          <div class="detail-item"><span>报销单号</span><b>{{ detailDrawer.data.reimburseNo }}</b></div>
          <div class="detail-item"><span>状态</span><b>{{ statusText(detailDrawer.data.status) }}</b></div>
          <div class="detail-item"><span>项目</span><b>{{ detailDrawer.data.projectName }}</b></div>
          <div class="detail-item"><span>申请人</span><b>{{ detailDrawer.data.applicantName }}</b></div>
          <div class="detail-item"><span>单位</span><b>{{ detailDrawer.data.unitName || '-' }}</b></div>
          <div class="detail-item"><span>总金额</span><b>{{ money(detailDrawer.data.totalAmount) }}</b></div>
          <div class="detail-item full"><span>标题</span><b>{{ detailDrawer.data.title }}</b></div>
          <div class="detail-item full"><span>说明</span><b>{{ detailDrawer.data.description || '-' }}</b></div>
        </div>

        <el-divider content-position="left">附件材料</el-divider>
        <div v-if="detailDrawer.data.attachments?.length" class="drawer-attach-list">
          <div v-for="(item, index) in detailDrawer.data.attachments" :key="`${item.fileUrl}-${index}`" class="drawer-attach-item">
            <span>{{ attachmentLabel(item.fileCategory) }}</span>
            <a :href="item.fileUrl" target="_blank">{{ item.originalName || item.fileUrl }}</a>
          </div>
        </div>
        <el-empty v-else description="暂无附件" :image-size="70" />

        <el-divider content-position="left">报销明细</el-divider>
        <el-table :data="detailDrawer.data.items || []" border>
          <el-table-column prop="subjectCode" label="科目编码" width="110" />
          <el-table-column prop="subjectName" label="科目名称" min-width="160" />
          <el-table-column prop="itemName" label="明细名称" min-width="160" />
          <el-table-column prop="expenseDate" label="发生日期" width="120" />
          <el-table-column label="基础金额" width="130"><template #default="{ row }">{{ money(row.baseAmount ?? row.amount) }}</template></el-table-column>
          <el-table-column label="差旅天数" width="100"><template #default="{ row }">{{ row.travelDays || '-' }}</template></el-table-column>
          <el-table-column label="每日补助" width="120"><template #default="{ row }">{{ row.subsidyPerDay ? money(row.subsidyPerDay) : '-' }}</template></el-table-column>
          <el-table-column label="补助金额" width="120"><template #default="{ row }">{{ row.subsidyAmount ? money(row.subsidyAmount) : '-' }}</template></el-table-column>
          <el-table-column label="总金额" width="130"><template #default="{ row }">{{ money(row.amount) }}</template></el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" />
        </el-table>

        <el-divider content-position="left">支付归档信息</el-divider>
        <div class="detail-grid">
          <div class="detail-item"><span>支付方式</span><b>{{ detailDrawer.data.payment?.payMethod || '-' }}</b></div>
          <div class="detail-item"><span>支付凭证号</span><b>{{ detailDrawer.data.payment?.voucherNo || '-' }}</b></div>
          <div class="detail-item"><span>支付时间</span><b>{{ detailDrawer.data.payment?.paidAt || '-' }}</b></div>
          <div class="detail-item"><span>归档号</span><b>{{ detailDrawer.data.archive?.archiveNo || '-' }}</b></div>
          <div class="detail-item"><span>归档时间</span><b>{{ detailDrawer.data.archive?.archivedAt || '-' }}</b></div>
          <div class="detail-item"><span>归档位置</span><b>{{ detailDrawer.data.archive?.archivePath || '-' }}</b></div>
        </div>

        <el-divider content-position="left">审批轨迹</el-divider>
        <el-timeline>
          <el-timeline-item v-for="log in detailDrawer.data.auditLogs || []" :key="log.id" :timestamp="log.createdAt">
            <div class="log-title">{{ log.action }}</div>
            <div class="log-desc">{{ log.operatorName || '-' }}<span v-if="log.comment">：{{ log.comment }}</span></div>
          </el-timeline-item>
        </el-timeline>
      </template>
    </el-drawer>

    <el-dialog v-model="auditDialog.visible" title="审批报销单" width="520px">
      <el-form label-width="90px">
        <el-form-item label="单号"><div>{{ auditDialog.row?.reimburseNo }}</div></el-form-item>
        <el-form-item label="当前节点"><div>{{ auditStageText(auditDialog.row) }}</div></el-form-item>
        <el-form-item label="操作">
          <el-radio-group v-model="auditDialog.action">
            <el-radio label="pass">通过</el-radio>
            <el-radio label="reject">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="意见">
          <el-input v-model="auditDialog.comment" type="textarea" :rows="4" placeholder="驳回时必填，审批通过时可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="auditSaving" @click="submitAudit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { apiProjectPage, type ProjectVO } from '@/api/project';
import { apiProjectBudgetList, type ProjectBudgetVO } from '@/api/budget';
import { useUserStore } from '@/stores/user';
import {
  apiReimburseAudit,
  apiReimburseCreate,
  apiReimburseDetail,
  apiReimbursePage,
  apiReimburseSubmit,
  apiReimburseUpdate,
  apiReimburseUpload,
  apiReimburseWithdraw,
  type ReimburseAttachmentReq,
  type ReimburseCreateReq,
  type ReimburseDetailVO,
  type ReimburseItemReq,
  type ReimburseVO,
} from '@/api/reimburse';

const loading = ref(false);
const user = useUserStore();
const saving = ref(false);
const auditSaving = ref(false);
const page = reactive({ records: [] as ReimburseVO[], total: 0, pageNo: 1, pageSize: 10 });
const query = reactive({ keyword: '', status: undefined as number | undefined, projectId: undefined as number | undefined, todoOnly: false });
const projectOptions = ref<ProjectVO[]>([]);
const budgetOptions = ref<ProjectBudgetVO[]>([]);

const statusOptions = [
  { value: 0, label: '草稿' },
  { value: 7, label: '待组长审批' },
  { value: 1, label: '待二级单位审核' },
  { value: 2, label: '待财务复核' },
  { value: 3, label: '待支付归档' },
  { value: 4, label: '已支付归档' },
  { value: 5, label: '已驳回' },
  { value: 6, label: '已撤销' },
];

function blankItem(): ReimburseItemReq {
  return { subjectId: null, itemName: '', expenseDate: '', amount: null, baseAmount: null, travelDays: null, subsidyPerDay: null, subsidyAmount: null, remark: '' };
}

const editDialog = reactive({
  visible: false,
  mode: 'create' as 'create' | 'edit',
  form: {
    id: undefined as number | undefined,
    projectId: null as number | null,
    title: '',
    description: '',
    items: [blankItem()] as ReimburseItemReq[],
    attachments: [] as ReimburseAttachmentReq[],
  },
});
const detailDrawer = reactive({ visible: false, data: null as ReimburseDetailVO | null });
const auditDialog = reactive({ visible: false, row: null as ReimburseVO | null, action: 'pass' as 'pass' | 'reject', comment: '' });
const formTotal = computed(() => editDialog.form.items.reduce((sum, item) => sum + Number(item.amount || 0), 0));
const hasTravelSubsidyRows = computed(() => editDialog.form.items.some((item) => isTravelSubject(item.subjectId)));

function money(v?: number | null) {
  const n = Number(v || 0);
  return `¥${n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}
function statusText(v?: number) { return statusOptions.find((s) => s.value === v)?.label || '未知'; }
function statusTag(v?: number) {
  if (v === 4) return 'success';
  if (v === 5 || v === 6) return 'danger';
  if (v === 7 || v === 1 || v === 2 || v === 3) return 'warning';
  return 'info';
}
function attachmentLabel(type?: string) {
  if (type === 'INVOICE') return '发票';
  if (type === 'VOUCHER') return '凭证';
  return type || '附件';
}
function auditStageText(row: ReimburseVO | null) {
  if (!row) return '-';
  if (row.canLeaderAudit === 1) return '组长审批';
  if (row.canUnitAudit === 1) return '二级单位审核';
  if (row.canFinanceAudit === 1) return '财务复核';
  return statusText(row.status);
}
function canShowSubmit(row: ReimburseVO) {
  const mine = Number(user.me?.id || 0);
  const owner = Number(row.applicantUserId || 0);
  return row.canSubmit === 1 && mine > 0 && owner > 0 && mine === owner && (row.status === 0 || row.status === 5);
}
function recalcItem(row: ReimburseItemReq) {
  const base = Number(row.baseAmount || 0);
  if (!isTravelSubject(row.subjectId)) {
    row.travelDays = null;
    row.subsidyPerDay = null;
    row.subsidyAmount = null;
    row.amount = Number(base.toFixed(2));
    return;
  }
  const days = Number(row.travelDays || 0);
  const perDay = Number(row.subsidyPerDay || 0);
  const subsidy = days > 0 && perDay > 0 ? Number((days * perDay).toFixed(2)) : 0;
  row.subsidyAmount = subsidy || null;
  row.amount = Number((base + subsidy).toFixed(2));
}
function isTravelSubject(subjectId?: number | null) {
  if (!subjectId) return false;
  const subjectCode = (budgetOptions.value.find((item) => item.subjectId === subjectId)?.subjectCode || '').toUpperCase();
  return subjectCode === 'TRAVEL' || subjectCode.startsWith('TRAVEL_');
}
function handleSubjectChange(row: ReimburseItemReq) {
  recalcItem(row);
}

async function loadProjects() {
  const res = await apiProjectPage({ pageNo: 1, pageSize: 300, status: 3 });
  projectOptions.value = res.records || [];
}
async function fetchPage(pageNo = page.pageNo) {
  loading.value = true;
  try {
    const res = await apiReimbursePage({ pageNo, pageSize: page.pageSize, keyword: query.keyword || undefined, status: query.status, projectId: query.projectId, todoOnly: query.todoOnly || undefined });
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
  query.status = undefined;
  query.projectId = undefined;
  query.todoOnly = false;
  fetchPage(1);
}
function resetForm() {
  editDialog.form.id = undefined;
  editDialog.form.projectId = null;
  editDialog.form.title = '';
  editDialog.form.description = '';
  editDialog.form.items = [blankItem()];
  editDialog.form.attachments = [];
  budgetOptions.value = [];
}
function openCreate() { editDialog.mode = 'create'; resetForm(); editDialog.visible = true; }

async function openEdit(row: ReimburseVO) {
  const detail = await apiReimburseDetail(row.id);
  editDialog.mode = 'edit';
  editDialog.form.id = detail.id;
  editDialog.form.projectId = detail.projectId;
  editDialog.form.title = detail.title;
  editDialog.form.description = detail.description || '';
  editDialog.form.attachments = (detail.attachments || []).map((item) => ({ ...item }));
  editDialog.form.items = (detail.items || []).length ? (detail.items || []).map((x) => ({
    subjectId: x.subjectId,
    itemName: x.itemName,
    amount: x.amount,
    baseAmount: x.baseAmount ?? x.amount,
    travelDays: x.travelDays ?? null,
    subsidyPerDay: x.subsidyPerDay ?? null,
    subsidyAmount: x.subsidyAmount ?? null,
    expenseDate: x.expenseDate,
    remark: x.remark || '',
  })) : [blankItem()];
  editDialog.visible = true;
  await handleFormProjectChange();
  editDialog.form.items.forEach(recalcItem);
}
async function openDetail(row: ReimburseVO) { detailDrawer.data = await apiReimburseDetail(row.id); detailDrawer.visible = true; }
function openAudit(row: ReimburseVO) { auditDialog.row = row; auditDialog.action = 'pass'; auditDialog.comment = ''; auditDialog.visible = true; }

async function handleSubmit(row: ReimburseVO) {
  await ElMessageBox.confirm(`确认提交报销单【${row.reimburseNo}】吗？提交后将冻结对应预算余额。`, '提示', { type: 'warning' });
  await apiReimburseSubmit(row.id);
  row.canSubmit = 0;
  row.canEdit = 0;
  row.status = 7;
  ElMessage.success('提交成功');
  await fetchPage(page.pageNo);
}
async function handleWithdraw(row: ReimburseVO) {
  await ElMessageBox.confirm(`确认撤销报销单【${row.reimburseNo}】吗？`, '提示', { type: 'warning' });
  await apiReimburseWithdraw(row.id);
  ElMessage.success('已撤销');
  await fetchPage(page.pageNo);
}
async function handleFormProjectChange() {
  if (!editDialog.form.projectId) { budgetOptions.value = []; return; }
  budgetOptions.value = await apiProjectBudgetList(editDialog.form.projectId);
}
function addItem() { editDialog.form.items.push(blankItem()); }
function removeItem(index: number) {
  if (editDialog.form.items.length === 1) { editDialog.form.items = [blankItem()]; return; }
  editDialog.form.items.splice(index, 1);
}
async function handleUpload(file: File | undefined, fileCategory: string) {
  if (!file) return;
  const res = await apiReimburseUpload(file, fileCategory);
  editDialog.form.attachments.push(res);
  ElMessage.success('上传成功');
}
function removeAttachment(index: number) { editDialog.form.attachments.splice(index, 1); }

async function saveForm() {
  if (!editDialog.form.projectId) { ElMessage.warning('请选择项目'); return; }
  if (!editDialog.form.title.trim()) { ElMessage.warning('请填写标题'); return; }
  if (!editDialog.form.items.length) { ElMessage.warning('请至少填写一条报销明细'); return; }
  editDialog.form.items.forEach(recalcItem);
  const payload: ReimburseCreateReq = {
    projectId: editDialog.form.projectId,
    title: editDialog.form.title,
    description: editDialog.form.description,
    items: editDialog.form.items,
    attachments: editDialog.form.attachments,
  };
  saving.value = true;
  try {
    if (editDialog.mode === 'create') {
      await apiReimburseCreate(payload);
      ElMessage.success('报销单已创建');
    } else {
      await apiReimburseUpdate({ id: editDialog.form.id as number, ...payload });
      ElMessage.success('报销单已更新');
    }
    editDialog.visible = false;
    await fetchPage(editDialog.mode === 'create' ? 1 : page.pageNo);
  } finally {
    saving.value = false;
  }
}
async function submitAudit() {
  if (!auditDialog.row) return;
  if (auditDialog.action === 'reject' && !auditDialog.comment.trim()) { ElMessage.warning('驳回时请填写意见'); return; }
  auditSaving.value = true;
  try {
    await apiReimburseAudit(auditDialog.row.id, auditDialog.action, auditDialog.comment || undefined);
    auditDialog.visible = false;
    ElMessage.success('操作成功');
    await fetchPage(page.pageNo);
    if (detailDrawer.visible && detailDrawer.data?.id === auditDialog.row.id) detailDrawer.data = await apiReimburseDetail(auditDialog.row.id);
  } finally {
    auditSaving.value = false;
  }
}

onMounted(async () => {
  await loadProjects();
  await fetchPage(1);
});
</script>

<style scoped>
.card { border-radius: 16px; border:none; }
.mt16 { margin-top:16px; }
.card-head { display:flex; justify-content:space-between; align-items:center; gap:12px; font-weight:700; }
.pager { display:flex; justify-content:flex-end; margin-top:16px; }
.project-cell { display:flex; flex-direction:column; gap:4px; }
.project-cell b { color:#1f2937; }
.project-cell span { color:#64748b; font-size:12px; }
.op-list { display:flex; gap:6px; flex-wrap:wrap; }
.section-head { display:flex; justify-content:space-between; align-items:center; gap:16px; margin:14px 0 10px; }
.section-title { font-size:16px; font-weight:700; color:#1f2937; }
.section-tip { margin-top:4px; color:#64748b; font-size:13px; }
.upload-actions { display:flex; gap:10px; flex-wrap:wrap; }
.attach-list { display:grid; gap:10px; margin-bottom:16px; }
.attach-item, .drawer-attach-item { display:flex; justify-content:space-between; align-items:center; gap:12px; padding:10px 12px; background:#f8fafc; border:1px solid #e5e7eb; border-radius:12px; }
.attach-main { display:flex; align-items:center; gap:10px; flex-wrap:wrap; }
.attach-link { color:#2563eb; text-decoration:none; }
.total-box { text-align:right; margin-top:12px; font-size:16px; color:#475569; }
.total-box b { color:#111827; font-size:20px; }
.detail-grid { display:grid; grid-template-columns:1fr 1fr; gap:14px; }
.detail-item { background:#f8fafc; border:1px solid #e5e7eb; border-radius:12px; padding:12px 14px; display:flex; flex-direction:column; gap:8px; }
.detail-item.full { grid-column:1 / -1; }
.detail-item span { color:#64748b; font-size:13px; }
.detail-item b { color:#111827; word-break:break-all; }
.drawer-attach-list { display:grid; gap:10px; }
.drawer-attach-item span { color:#64748b; min-width:52px; }
.drawer-attach-item a { color:#2563eb; text-decoration:none; word-break:break-all; }
.log-title { font-weight:700; color:#1f2937; }
.log-desc { margin-top:4px; color:#64748b; }
@media (max-width: 900px) {
  .detail-grid { grid-template-columns:1fr; }
  .section-head { flex-direction:column; align-items:flex-start; }
}
</style>
