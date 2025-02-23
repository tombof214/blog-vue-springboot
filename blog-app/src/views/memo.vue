<template>
  <div>
    <h2>备忘录管理</h2>
    <!-- 跳转到 MemoWrite 页面 -->
    <el-button type="primary" @click="goToWritePage">添加备忘录</el-button>

    <!-- 显示未过期的备忘录列表 -->
    <el-table :data="memos" style="margin-top: 20px;">
      <el-table-column label="备忘录" prop="title"></el-table-column>
      <el-table-column label="截止时间" prop="dueDate"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button @click="viewMemo(scope.row)" type="info">查看</el-button>
          <el-button @click="goToWritePage(scope.row)" type="primary">编辑</el-button>
          <el-button @click="deleteMemo(scope.$index)" type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 查看备忘录的弹框 -->
    <el-dialog
      :visible.sync="dialogVisible"
      width="60%"
      title="备忘录详情"
      @close="resetDialog">
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
      console.log('Current Memo:', this.currentMemo); // 添加日志
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
          console.log('Fetched memos:', response.data);
          this.memos = response.data;
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
</style>
