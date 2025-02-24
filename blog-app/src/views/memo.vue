<template>
  <div>
    <h2 class="page-title">备忘录管理</h2>
    <!-- 跳转到 MemoWrite 页面 -->
    <el-button type="primary" class="add-button" @click="goToWritePage">添加备忘录</el-button>

    <!-- 显示未过期的备忘录列表 -->
    <el-table :data="memos" class="memo-table">
      <el-table-column label="备忘录" prop="title" :min-width="150"></el-table-column>
      <el-table-column label="创建时间" prop="createdDate" :min-width="180"></el-table-column>
      <el-table-column label="截止时间" prop="dueDate" :min-width="180"></el-table-column>
      <el-table-column label="操作" :min-width="250">
        <template slot-scope="scope">
          <div class="operation-buttons">
            <el-button @click="viewMemo(scope.row)" type="info" class="table-button">查看</el-button>
            <el-button @click="goToWritePage(scope.row)" type="primary" class="table-button">编辑</el-button>
            <el-button @click="deleteMemo(scope.$index)" type="danger" class="table-button">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 查看备忘录的弹框 -->
    <el-dialog
      :visible.sync="dialogVisible"
      width="60%"
      title="备忘录详情"
      @close="resetDialog"
      class="memo-dialog">
      <div v-if="currentMemo">
        <h3>{{ currentMemo.title }}</h3>
        <p><strong>创建时间:</strong> {{ currentMemo.createdDate }}</p>
        <p><strong>过期时间:</strong> {{ currentMemo.dueDate }}</p>
        <!-- 使用 v-html 渲染正文 -->
        <div v-html="currentMemo.body"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Message } from 'element-ui';
import axios from 'axios';
import MarkdownEditor from '@/components/markdown/MarkdownEditor'

export default {
  name: 'Memo',
  components: {
    'markdown-editor': MarkdownEditor,
  },
  data() {
    return {
      memos: [], // 储存备忘录
      dialogVisible: false, // 控制弹框的显示
      currentMemo: null, // 当前查看的备忘录
    };
  },
  methods: {
    // 跳转到 MemoWrite 页面并传递数据
    goToWritePage(memo) {
      if (memo) {
        this.$router.push({ path: '/MemoWrite', query: { memoId: memo.id } });
      } else {
        this.$router.push('/MemoWrite'); // 如果没有传递memo数据，则是新建
      }
    },

    // 查看备忘录详情
    viewMemo(memo) {
      this.currentMemo = memo;
      this.dialogVisible = true; // 打开弹框
    },

    // 删除备忘录
    deleteMemo(index) {
      const memo = this.memos[index];
      axios.delete(`http://localhost:8888/api/memo/${memo.id}`)
        .then(() => {
          Message.success('备忘录已删除');
          this.memos.splice(index, 1); // 删除前端的备忘录
        })
        .catch(error => {
          Message.error('删除失败');
          console.error('Error deleting memo:', error);
        });
    },

    // 获取未过期的备忘录
    getMemos() {
      axios.get('http://localhost:8888/api/memos')
        .then(response => {
          this.memos = response.data.map(memo => {
            // Format the createdDate and dueDate to only show the date part (YYYY-MM-DD)
            const formatDate = (date) => new Date(date).toISOString().split('T')[0];

            return {
              ...memo,
              createdDate: formatDate(memo.createdDate),
              dueDate: formatDate(memo.dueDate),
            };
          });
        })
        .catch(error => {
          Message.error('获取备忘录失败');
          console.error('Error fetching memos:', error);
        });
    },

    // 重置弹框
    resetDialog() {
      this.currentMemo = null;
    }
  },
  mounted() {
    this.getMemos(); // 页面加载时获取未过期的备忘录
  },
};
</script>

<style scoped>
/* 样式 */
.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.add-button {
  margin-bottom: 20px;
}

.memo-table {
  width: 100%;
  border: 1px solid #f0f0f0;
  border-radius: 5px;
  overflow: hidden;
  table-layout: auto;
}

.el-table th, .el-table td {
  padding: 12px 20px;
  text-align: left;
  border-bottom: 1px solid #eaeaea;
}

.operation-buttons {
  display: flex;
  gap: 10px;
}

.table-button {
  margin: 0;
  padding: 8px 16px;
}

.table-button:hover {
  opacity: 0.8;
}

.memo-dialog h3 {
  font-size: 20px;
  margin-bottom: 10px;
}

.memo-dialog p {
  font-size: 16px;
  margin-bottom: 8px;
}

.memo-dialog .el-dialog__body {
  padding: 20px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .memo-table {
    font-size: 14px;
  }

  .operation-buttons {
    flex-direction: column;
  }

  .table-button {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>
