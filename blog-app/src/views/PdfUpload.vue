<template>
  <div class="container">
    <h2 class="title">PDF文件批量转换</h2>
    <form @submit.prevent="handleSubmit" class="form">
      <div class="form-group">
        <label for="pdfFiles" class="file-label">
          <span v-if="!files || files.length === 0">选择PDF文件</span>
          <span v-else>{{ files.length }} 个文件</span>
        </label>
        <input type="file" id="pdfFiles" ref="fileInput" @change="handleFileChange" multiple required class="file-input" />

        <!-- 拖拽区域 -->
        <div class="drag-drop-zone" @drop.prevent="handleDrop" @dragover.prevent>
          <p v-if="files.length === 0">或者将文件拖拽到这里</p>
          <p v-else>{{ files.map(file => file.name).join(', ') }}</p> <!-- 显示拖拽文件名 -->
        </div>
      </div>
      <div class="form-group">
        <label for="outputDir">输出目录:</label>
        <input type="text" v-model="outputDir" id="outputDir" placeholder="请输入输出目录路径" required />
      </div>
      <div class="form-group">
        <label for="format">选择转换格式:</label>
        <select v-model="format" required>
          <option value="jpg">JPG</option>
          <option value="png">PNG</option>
        </select>
      </div>
      <div class="form-group">
        <button type="submit" class="submit-btn">开始转换</button>
      </div>
    </form>

    <div v-if="statusMessage" class="status-message" :class="{'error': statusMessage.includes('失败'), 'success': !statusMessage.includes('失败')}">
      <p>{{ statusMessage }}</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: 'PdfUpload',
  data() {
    return {
      files: [], // 存储选中的多个文件
      outputDir: "D:", // 默认输出目录设置为D:
      format: "jpg",
      statusMessage: "",
    };
  },
  methods: {
    // 处理文件选择
    handleFileChange(event) {
      this.files = Array.from(event.target.files); // 将选中的文件转换为数组
      this.checkFiles(); // 检查文件类型
      console.log("选择的文件:", this.files); // 打印选择的文件列表
    },

    // 拖拽处理
    handleDrop(event) {
      const droppedFiles = Array.from(event.dataTransfer.files);
      this.files = [...this.files, ...droppedFiles]; // 合并拖拽和选择的文件
      this.checkFiles(); // 检查文件类型
      console.log("拖拽的文件:", this.files);
    },

    // 文件格式检查
    checkFiles() {
      const invalidFiles = this.files.filter(file => !file.name.endsWith('.pdf'));
      if (invalidFiles.length > 0) {
        alert("请确保所有文件为PDF格式");
        this.files = this.files.filter(file => file.name.endsWith('.pdf')); // 只保留PDF文件
      }
    },

    // 批量上传文件
    async handleFilesUpload() {
      console.log("开始批量上传文件...");
      const formData = new FormData();
      this.files.forEach(file => formData.append("files", file)); // 添加多个文件到 FormData 中

      try {
        const response = await axios.post("http://localhost:8888/pdf/upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });

        console.log("上传成功，返回的文件路径:", response.data);
        if (response.data) {
          this.statusMessage = `文件上传成功，返回文件路径: ${response.data}`;
        } else {
          this.statusMessage = "文件上传失败";
          console.log("文件上传失败，没有返回路径");
        }
      } catch (error) {
        this.statusMessage = "文件上传失败: " + error.message;
        console.error("上传文件时出错:", error);
      }
    },

    // 处理提交表单
    async handleSubmit() {
      if (this.files.length === 0) {
        this.statusMessage = "请先选择至少一个PDF文件!";
        console.log("没有选择文件");
        return;
      }

      console.log("开始上传文件并获取文件路径...");
      await this.handleFilesUpload(); // 上传文件

      this.statusMessage = "正在转换，请稍候...";

      try {
        // 批量转换
        const formData = new FormData();
        formData.append("outputDir", this.outputDir);
        formData.append("format", this.format);

        // 传递多个文件路径到后端
        this.files.forEach(file => {
          formData.append("pdfFiles", file);
        });

        const response = await axios.post("http://localhost:8888/pdf/convert", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });

        console.log("转换成功，返回的结果:", response.data);
        this.statusMessage = response.data;
      } catch (error) {
        console.error("转换失败:", error);
        this.statusMessage = "转换失败: " + error.message;
      }
    },
  },
};
</script>

<style scoped>
/* 样式优化 */
.container {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  font-size: 14px;
  margin-bottom: 8px;
  color: #555;
}

input[type="file"],
input[type="text"],
select {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

input[type="file"] {
  display: none;
}

.file-label {
  display: inline-block;
  background-color: #007bff;
  color: #fff;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  text-align: center;
}

.file-label:hover {
  background-color: #0056b3;
}

input[type="text"]:focus,
select:focus {
  border-color: #0056b3;
  outline: none;
}

.submit-btn {
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease;
}

.submit-btn:hover {
  background-color: #0056b3;
}

.status-message {
  margin-top: 20px;
  padding: 15px;
  background-color: #eafaf1;
  border-radius: 5px;
  color: #155724;
  font-size: 16px;
  border: 1px solid #d4edda;
}

.status-message.error {
  background-color: #f8d7da;
  border-color: #f5c6cb;
  color: #721c24;
}

.status-message.success {
  background-color: #d4edda;
  border-color: #c3e6cb;
  color: #155724;
}

/* 拖拽区域样式 */
.drag-drop-zone {
  border: 2px dashed #007bff;
  padding: 30px;
  text-align: center;
  color: #007bff;
  cursor: pointer;
  border-radius: 4px;
  margin-top: 20px;
}

.drag-drop-zone:hover {
  background-color: #f0f8ff;
}
</style>
