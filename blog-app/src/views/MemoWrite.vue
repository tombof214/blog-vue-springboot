<template>
  <div id="write" v-title :data-title="title">
    <el-container>
      <base-header :simple="true">
        <el-col :span="4" :offset="2">
          <div class="me-write-info">{{ isEdit ? '编辑备忘录' : '写备忘录' }}</div>
        </el-col>
        <el-col :span="4" :offset="6">
          <div class="me-write-btn">
            <el-button round @click="saveMemo">{{ isEdit ? '更新' : '发布' }}</el-button>
            <el-button round @click="cancel">取消</el-button>
          </div>
        </el-col>
      </base-header>

      <el-container class="me-area me-write-box">
        <el-main class="me-write-main">
          <div class="me-write-title">
            <el-input resize="none"
                      type="textarea"
                      autosize
                      v-model="articleForm.title"
                      placeholder="请输入标题"
                      class="me-write-input">
            </el-input>
          </div>

          <markdown-editor :editor="articleForm.editor" class="me-write-editor"></markdown-editor>

          <!-- Add Memo Date and Expiry Date -->
          <div class="memo-dates">
            <el-date-picker
              v-model="articleForm.createdDate"
              type="date"
              placeholder="选择备忘录日期"
              style="width: 100%; margin-bottom: 20px;">
            </el-date-picker>

            <el-date-picker
              v-model="articleForm.dueDate"
              type="date"
              placeholder="选择过期日期"
              style="width: 100%; margin-bottom: 20px;">
            </el-date-picker>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import BaseHeader from '@/views/BaseHeader'
import MarkdownEditor from '@/components/markdown/MarkdownEditor'
import axios from 'axios'

export default {
  name: 'MemoWrite',
  mounted() {
    const memoId = this.$route.query.memoId;
    if (memoId) {
      this.isEdit = true;
      this.getMemoById(memoId);
    } else {
      this.isEdit = false;
    }
  },
  data() {
    return {
      isEdit: false, // Track if we are editing an existing memo
      articleForm: {
        title: '',
        summary: '',
        createdDate: new Date(), // Memo creation date
        dueDate: '', // Memo expiry date
        editor: {
          value: '',
          ref: '', // save mavonEditor instance
        }
      }
    }
  },
  computed: {
    title () {
      return this.isEdit ? '编辑备忘录 - For Fun' : '写备忘录 - For Fun';
    }
  },
  methods: {
    // 保存备忘录
    saveMemo() {
      // 检查输入内容
      if (!this.articleForm.title || !this.articleForm.editor.value || !this.articleForm.dueDate) {
        this.$message({message: '请填写完整信息', type: 'warning'});
        return;
      }

      const createTime = new Date(this.articleForm.createdDate).toISOString();
      console.log(createTime);
      const reminderTime = new Date(this.articleForm.dueDate).toISOString();
      const memoData = {
        title: this.articleForm.title,
        body: this.articleForm.editor.value,
        createdDate: createTime, // 传递创建时间
        dueDate: reminderTime, // 传递过期时间
      };

      if (this.isEdit) {
        // 更新备忘录
        const memoId = this.$route.query.memoId;
        axios.put(`http://localhost:8888/api/memo/${memoId}`, memoData)
          .then(() => {
            this.$message({message: '备忘录已更新', type: 'success'});
            this.$router.push('/');
          })
          .catch(error => {
            this.$message({message: '更新失败', type: 'error'});
            console.error('Error:', error);
          });
      } else {
        // 新建备忘录
        axios.post('http://localhost:8888/api/memo', memoData)
          .then(() => {
            this.$message({message: '备忘录已发布', type: 'success'});
            this.$router.push('/');
          })
          .catch(error => {
            this.$message({message: '发布失败', type: 'error'});
            console.error('Error:', error);
          });
      }
    },

    // 获取备忘录内容
    getMemoById(id) {
      axios.get(`http://localhost:8888/api/memo/${id}`)
        .then(response => {
          const memo = response.data;
          this.articleForm.title = memo.title;
          this.articleForm.editor.value = memo.body;
          this.articleForm.createdDate = memo.createdDate;
          this.articleForm.dueDate = memo.reminderTime;
        })
        .catch(error => {
          this.$message({message: '获取备忘录失败', type: 'error'});
          console.error('Error:', error);
        });
    },

    cancel() {
      this.$confirm('备忘录未保存，是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$router.push('/');
      });
    },
  },
  components: {
    'base-header': BaseHeader,
    'markdown-editor': MarkdownEditor,
  }
}
</script>

<style scoped>
/* 样式 */
.el-header {
  position: fixed;
  z-index: 1024;
  min-width: 100%;
  box-shadow: 0 2px 3px hsla(0, 0%, 7%, .1), 0 0 0 1px hsla(0, 0%, 7%, .1);
}

.me-write-info {
  line-height: 60px;
  font-size: 18px;
  font-weight: 600;
}

.me-write-btn {
  margin-top: 10px;
}

.me-write-box {
  max-width: 700px;
  margin: 80px auto 0;
}

.me-write-main {
  padding: 0;
}

.me-write-title {
}

.me-write-input textarea {
  font-size: 32px;
  font-weight: 600;
  height: 20px;
  border: none;
}

.me-write-editor {
  min-height: 650px !important;
}

.memo-dates {
  margin-bottom: 20px;
}

.me-header-left {
  margin-top: 10px;
}

.me-title img {
  max-height: 2.4rem;
  max-width: 100%;
}

.me-write-toolbar-fixed {
  position: fixed;
  width: 700px !important;
  top: 60px;
}

.v-note-op {
  box-shadow: none !important;
}

.auto-textarea-input, .auto-textarea-block {
  font-size: 18px !important;
}
</style>
