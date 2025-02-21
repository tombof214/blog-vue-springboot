<template>
  <div>
    <h2>设置备忘录</h2>
    <el-input v-model="newMemo" placeholder="输入新的备忘录"></el-input>
    <el-date-picker
      v-model="reminderTime"
      type="datetime"
      placeholder="选择deadline"
    ></el-date-picker>
    <el-button type="primary" @click="addMemo">添加备忘录</el-button>

    <!-- 显示未过期的备忘录列表 -->
    <el-table :data="memos" style="margin-top: 20px;">
      <el-table-column label="备忘录" prop="text"></el-table-column>
      <el-table-column label="提醒时间" prop="time"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button @click="deleteMemo(scope.$index)" type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { Message } from 'element-ui';
import axios from 'axios';

export default {
  data() {
    return {
      newMemo: '', // 新备忘录内容
      reminderTime: '', // 提醒时间
      memos: [], // 储存备忘录
    };
  },
  methods: {
    // 添加备忘录
    addMemo() {
      if (!this.newMemo || !this.reminderTime) {
        Message.warning('请输入备忘录和提醒时间');
        return;
      }

      // 调用后端 API 将提醒内容和时间发送到后端
      axios.post('http://localhost:8888/api/memo', {
        memoText: this.newMemo,
        reminderTime: this.reminderTime
      }).then(response => {
        Message.success('备忘录已添加');
        this.newMemo = '';
        this.reminderTime = '';
        this.getMemos(); // 每次添加新备忘录后，刷新备忘录列表
      }).catch(error => {
        Message.error('添加失败');
      });
    },

    // 删除备忘录
    deleteMemo(index) {
      this.memos.splice(index, 1);
    },

    // 获取未过期的备忘录
    getMemos() {
      axios.get('http://localhost:8888/api/memos')
        .then(response => {
          // 筛选出未过期的备忘录
          const currentTime = new Date().getTime();
          this.memos = response.data.filter(memo => new Date(memo.reminderTime).getTime() > currentTime);
        })
        .catch(error => {
          Message.error('获取备忘录失败');
        });
    },
  },
  mounted() {
    this.getMemos(); // 页面加载时获取未过期的备忘录
  },
};
</script>

<style scoped>
/* 样式 */
</style>
