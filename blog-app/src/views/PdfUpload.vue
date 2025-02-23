<template>
  <div class="container">
    <h2 class="title">PDF文件转换</h2>
    <form @submit.prevent="handleSubmit" class="form">
      <div class="form-group">
        <label for="pdfFile" class="file-label">
          <span v-if="!file">选择PDF文件</span>
          <span v-else>{{ file.name }}</span>
        </label>
        <input type="file" id="pdfFile" ref="fileInput" @change="handleFileChange" required class="file-input" />
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
      file: null,
      outputDir: "D:", // 默认输出目录设置为D:
      format: "jpg",
      statusMessage: "",
      uploadedFilePath: "",  // 保存返回的文件路径
    };
  },
  methods: {
    // 处理文件选择
    handleFileChange(event) {
      this.file = event.target.files[0];
      console.log("选择的文件:", this.file); // 打印选择的文件
    },

    // 处理文件上传并返回文件路径
    async handleFileUpload() {
      console.log("开始上传文件...");
      const formData = new FormData();
      formData.append("file", this.file);

      try {
        const response = await axios.post("http://localhost:8888/pdf/upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
        console.log(response);
        console.log("上传成功，返回的文件路径:", response.data); // 打印返回的文件路径

        if (response.data) {
          this.uploadedFilePath = response.data; // 获取返回的文件路径
        } else {
          this.statusMessage = "文件上传失败";
          console.log("文件上传失败，没有返回路径"); // 打印上传失败信息
        }
      } catch (error) {
        this.statusMessage = "文件上传失败: " + error.message;
        console.error("上传文件时出错:", error); // 打印上传错误信息
      }
    },

    // 处理提交表单
    async handleSubmit() {
      if (!this.file) {
        this.statusMessage = "请先选择一个PDF文件!";
        console.log("没有选择文件"); // 打印没有选择文件的提示
        return;
      }

      console.log("开始上传文件并获取文件路径...");
      await this.handleFileUpload(); // 上传文件

      if (!this.uploadedFilePath) {
        this.statusMessage = "文件上传失败，无法继续转换!";
        console.log("文件上传失败，无法继续转换"); // 打印上传失败的提示
        return;
      }

      const formData = new FormData();
      formData.append("pdfFile", this.uploadedFilePath);  // 发送文件路径给后台进行转换
      formData.append("outputDir", this.outputDir);
      formData.append("format", this.format);

      this.statusMessage = "正在转换，请稍候...";

      try {
        console.log("发送转换请求...");
        const response = await axios.post("http://localhost:8888/pdf/convert", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });

        console.log("转换成功，返回的结果:", response.data); // 打印转换成功的结果
        this.statusMessage = response.data;
      } catch (error) {
        console.error("转换失败:", error); // 打印转换失败的错误信息
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
</style>
