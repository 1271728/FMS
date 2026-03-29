<template>
  <div class="page-wrap app-page">
    <el-card shadow="hover" class="panel-card filter-card app-card">
      <div class="toolbar-row">
        <div class="toolbar-left">
          <span class="toolbar-label">项目</span>
          <el-select
            v-model="state.projectId"
            filterable
            clearable
            placeholder="请选择已立项项目"
            class="project-select"
            @change="handleProjectChange"
          >
            <el-option
              v-for="p in projectOptions"
              :key="p.id"
              :label="`${p.projectCode}｜${p.projectName}`"
              :value="p.id"
            />
          </el-select>
        </div>
        <div class="toolbar-actions">
          <el-button @click="loadBudget" :disabled="!state.projectId">刷新</el-button>
          <el-button type="primary" plain @click="goAdjust">预算调整单</el-button>
        </div>
      </div>
      <div v-if="currentProject" class="project-meta">
        <span>编号：{{ currentProject.projectCode }}</span>
        <span>名称：{{ currentProject.projectName }}</span>
        <span>负责人：{{ currentProject.principalName || '-' }}</span>
        <span>总预算：{{ money(currentProject.totalBudget) }}</span>
      </div>
    </el-card>

    <el-card shadow="hover" class="panel-card budget-ledger-card app-card">
      <div class="ledger-wrap" v-loading="loadingBudget">
        <el-table
          ref="tableRef"
          :data="displayRows"
          border
          stripe
          highlight-current-row
          row-key="id"
          class="ledger-table"
          @current-change="handleCurrentChange"
        >
          <el-table-column prop="code" label="预算项代码" min-width="130" />
          <el-table-column label="预算项名称" min-width="260">
            <template #default="{ row }">
              <div class="name-cell" :style="{ paddingLeft: `${row.depth * 18}px` }">
                <span>{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="controlMode" label="控制方式" width="110" align="center" />
          <el-table-column label="预算/收入数" min-width="130" align="right">
            <template #default="{ row }">
              <span class="amount-link">{{ money(row.budgetAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="支出数" min-width="110" align="right">
            <template #default="{ row }">
              <span class="amount-link">{{ money(row.expenseAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="其中往来" min-width="100" align="right">
            <template #default="{ row }">
              <span class="amount-link">{{ money(row.transferAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="冻结数" min-width="100" align="right">
            <template #default="{ row }">
              <span class="amount-link">{{ money(row.frozenAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="余额" min-width="120" align="right">
            <template #default="{ row }">
              <span class="amount-balance">{{ money(row.balanceAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="执行率" width="90" align="center">
            <template #default="{ row }">
              <span class="rate-text">{{ formatRate(row.executionRate) }}</span>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="!loadingBudget && !state.projectId" description="请选择项目" :image-size="72" />
        <el-empty v-else-if="!loadingBudget && !displayRows.length" description="当前项目暂无预算数据" :image-size="72" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { apiProjectPage, type ProjectVO } from '@/api/project';
import {
  apiBudgetSubjectTree,
  apiProjectBudgetList,
  type BudgetSubjectNode,
  type ProjectBudgetVO,
} from '@/api/budget';

const router = useRouter();
const tableRef = ref();
const loadingBudget = ref(false);
const state = reactive({ projectId: undefined as number | undefined });
const projectOptions = ref<ProjectVO[]>([]);
const subjectTree = ref<BudgetSubjectNode[]>([]);
const budgetRows = ref<ProjectBudgetVO[]>([]);
const currentRowId = ref<string | number | null>(null);

interface BudgetAggNode {
  id: number;
  code: string;
  name: string;
  approvedAmount: number;
  usedAmount: number;
  frozenAmount: number;
  availableAmount: number;
  children: BudgetAggNode[];
}

interface BudgetDisplayRow {
  id: string | number;
  code: string;
  name: string;
  depth: number;
  controlMode: string;
  budgetAmount: number;
  expenseAmount: number;
  transferAmount: number;
  frozenAmount: number;
  balanceAmount: number;
  executionRate: number | null;
  rowType: 'summary' | 'income' | 'expense' | 'group' | 'leaf';
}

const currentProject = computed(() => projectOptions.value.find((x) => x.id === state.projectId));

const displayRows = computed<BudgetDisplayRow[]>(() => {
  if (!subjectTree.value.length || !currentProject.value) return [];

  const budgetMap = new Map<number, ProjectBudgetVO>(budgetRows.value.map((item) => [item.subjectId, item]));

  const buildAggNode = (node: BudgetSubjectNode): BudgetAggNode => {
    const children = (node.children || []).map(buildAggNode);
    const own = budgetMap.get(node.id);
    const approvedAmount = Number(own?.approvedAmount || 0) + children.reduce((sum, item) => sum + item.approvedAmount, 0);
    const usedAmount = Number(own?.usedAmount || 0) + children.reduce((sum, item) => sum + item.usedAmount, 0);
    const frozenAmount = Number(own?.frozenAmount || 0) + children.reduce((sum, item) => sum + item.frozenAmount, 0);
    const availableAmount = Number(own?.availableAmount || 0) + children.reduce((sum, item) => sum + item.availableAmount, 0);
    return {
      id: node.id,
      code: node.code,
      name: node.name,
      approvedAmount,
      usedAmount,
      frozenAmount,
      availableAmount,
      children,
    };
  };

  const aggRoots = subjectTree.value.map(buildAggNode);
  const rootNode = aggRoots.find((item) => item.code === 'ROOT') || aggRoots[0];
  const subjectNodes = rootNode?.children?.length ? rootNode.children : aggRoots;

  const projectTotal = Number(currentProject.value.totalBudget || 0);
  const expenseBudget = subjectNodes.reduce((sum, item) => sum + item.approvedAmount, 0);
  const expenseUsed = subjectNodes.reduce((sum, item) => sum + item.usedAmount, 0);
  const expenseFrozen = subjectNodes.reduce((sum, item) => sum + item.frozenAmount, 0);
  const expenseAvailable = subjectNodes.reduce((sum, item) => sum + item.availableAmount, 0);
  const projectAvailable = projectTotal - expenseUsed - expenseFrozen;
  const projectCode = currentProject.value.projectCode || 'TOTAL';

  const rows: BudgetDisplayRow[] = [
    {
      id: `summary-${projectCode}`,
      code: projectCode,
      name: '项目余额',
      depth: 0,
      controlMode: '禁止超支',
      budgetAmount: projectTotal,
      expenseAmount: expenseUsed,
      transferAmount: 0,
      frozenAmount: expenseFrozen,
      balanceAmount: projectAvailable,
      executionRate: projectTotal > 0 ? expenseUsed / projectTotal : null,
      rowType: 'summary',
    },
    {
      id: `income-${projectCode}`,
      code: `${projectCode}-IN`,
      name: '收入',
      depth: 1,
      controlMode: '不控制',
      budgetAmount: projectTotal,
      expenseAmount: 0,
      transferAmount: 0,
      frozenAmount: 0,
      balanceAmount: projectTotal,
      executionRate: null,
      rowType: 'income',
    },
    {
      id: `expense-${projectCode}`,
      code: `${projectCode}-OUT`,
      name: '支出',
      depth: 1,
      controlMode: '不控制',
      budgetAmount: expenseBudget,
      expenseAmount: expenseUsed,
      transferAmount: 0,
      frozenAmount: expenseFrozen,
      balanceAmount: expenseAvailable,
      executionRate: expenseBudget > 0 ? expenseUsed / expenseBudget : null,
      rowType: 'expense',
    },
  ];

  const resolveControlMode = (node: BudgetAggNode, depth: number) => {
    if (depth <= 0) return '不控制';
    if (node.children.length > 0) return '不控制';
    if (node.approvedAmount > 0 || node.frozenAmount > 0 || node.availableAmount > 0) return '禁止超支';
    return '不控制';
  };

  const appendRows = (nodes: BudgetAggNode[], depth: number) => {
    nodes.forEach((node) => {
      rows.push({
        id: node.id,
        code: node.code,
        name: node.name,
        depth,
        controlMode: resolveControlMode(node, depth),
        budgetAmount: node.approvedAmount,
        expenseAmount: node.usedAmount,
        transferAmount: 0,
        frozenAmount: node.frozenAmount,
        balanceAmount: node.availableAmount,
        executionRate: node.approvedAmount > 0 ? node.usedAmount / node.approvedAmount : null,
        rowType: node.children.length ? 'group' : 'leaf',
      });
      if (node.children.length) appendRows(node.children, depth + 1);
    });
  };

  appendRows(subjectNodes, 2);
  return rows;
});

function money(v?: number | null) {
  const n = Number(v || 0);
  return n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function formatRate(v: number | null) {
  if (v === null || Number.isNaN(v)) return '-';
  return `${(v * 100).toFixed(2)}%`;
}

function handleCurrentChange(row?: BudgetDisplayRow) {
  currentRowId.value = row?.id ?? null;
}

async function loadProjects() {
  const res = await apiProjectPage({ pageNo: 1, pageSize: 200, status: 3 });
  projectOptions.value = res.records || [];
}

async function loadSubjectTree() {
  subjectTree.value = await apiBudgetSubjectTree();
}

async function loadBudget() {
  if (!state.projectId) {
    budgetRows.value = [];
    return;
  }
  loadingBudget.value = true;
  try {
    budgetRows.value = await apiProjectBudgetList(state.projectId);
  } finally {
    loadingBudget.value = false;
  }
}

async function handleProjectChange() {
  await loadBudget();
}

function goAdjust() {
  router.push('/budget/adjust');
}

watch(
  displayRows,
  async (rows) => {
    if (!rows.length) return;
    const target = rows.find((item) => item.rowType === 'leaf' && item.budgetAmount > 0)
      || rows.find((item) => item.rowType === 'group' && item.budgetAmount > 0)
      || rows[0];
    if (!target) return;
    currentRowId.value = target.id;
    await nextTick();
    if (tableRef.value?.setCurrentRow) {
      tableRef.value.setCurrentRow(target);
    }
  },
  { immediate: true },
);

onMounted(async () => {
  await Promise.all([loadProjects(), loadSubjectTree()]);
});
</script>

<style scoped>
.panel-card {
  border-radius: 18px;
  border: none;
  margin-bottom: 16px;
}

.filter-card {
  padding-bottom: 4px;
}

.toolbar-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-left,
.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-label {
  font-size: 14px;
  color: #64748b;
}

.project-select {
  width: 340px;
  max-width: 100%;
}

.project-meta {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px 18px;
  color: #475569;
  font-size: 13px;
}

.budget-ledger-card :deep(.el-card__body) {
  padding: 16px 18px 20px;
}

.ledger-wrap {
  position: relative;
}

.ledger-table {
  --el-table-border-color: #e7edf5;
  --el-table-header-bg-color: #ffffff;
  --el-table-row-hover-bg-color: #fff8ee;
}

.ledger-table :deep(.el-table__header-wrapper th) {
  font-size: 13px;
  font-weight: 700;
  color: #5b6472;
  padding-top: 8px;
  padding-bottom: 8px;
}

.ledger-table :deep(.el-table__cell) {
  padding-top: 8px;
  padding-bottom: 8px;
  font-size: 13px;
}

.ledger-table :deep(.el-table__row) {
  color: #4b5563;
}

.ledger-table :deep(.el-table__row .cell) {
  line-height: 1.3;
}

.ledger-table :deep(.el-table__row--striped td.el-table__cell) {
  background: #fcfdff;
}

.ledger-table :deep(.current-row td.el-table__cell) {
  background: #f7b267 !important;
}

.ledger-table :deep(.current-row td.el-table__cell .amount-link),
.ledger-table :deep(.current-row td.el-table__cell .amount-balance),
.ledger-table :deep(.current-row td.el-table__cell .rate-text),
.ledger-table :deep(.current-row td.el-table__cell .name-cell),
.ledger-table :deep(.current-row td.el-table__cell .cell) {
  color: #3f2d17;
}

.name-cell {
  display: flex;
  align-items: center;
  min-height: 18px;
}

.amount-link {
  color: #3f9df6;
  text-decoration: underline;
  text-underline-offset: 2px;
  font-variant-numeric: tabular-nums;
}

.amount-balance,
.rate-text {
  color: #4b5563;
  font-variant-numeric: tabular-nums;
}


.ledger-table :deep(.el-table__row td.el-table__cell) {
  transition: background-color 0.15s ease;
}

@media (max-width: 900px) {
  .project-select {
    width: 100%;
  }
}
</style>
