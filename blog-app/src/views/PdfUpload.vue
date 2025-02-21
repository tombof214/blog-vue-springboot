<template>
  <div>
    <h2>PDF文件转换</h2>
    <form @submit.prevent="handleSubmit">
      <div>
        <label for="pdfFile">选择PDF文件:</label>
        <input type="file" id="pdfFile" ref="fileInput" @change="handleFileChange" required />
      </div>
      <div>
        <label for="outputDir">输出目录:</label>
        <input type="text" v-model="outputDir" id="outputDir" placeholder="请输入输出目录路径" required />
      </div>
      <div>
        <label for="format">选择转换格式:</label>
        <select v-model="format" required>
          <option value="jpg">JPG</option>
          <option value="png">PNG</option>

        </select>
      </div>
      <div>
        <button type="submit">开始转换</button>
      </div>
    </form>

    <div v-if="statusMessage" class="status-message">
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
      outputDir: "",
      format: "jpg",
      statusMessage: "",
    };
  },
  methods: {
    handleFileChange(event) {
      this.file = event.target.files[0];
    },
    async handleSubmit() {
      if (!this.file) {
        this.statusMessage = "请先选择一个PDF文件!";
        return;
      }

      const formData = new FormData();
      formData.append("pdfFile", this.file);
      formData.append("outputDir", this.outputDir);
      formData.append("format", this.format);

      this.statusMessage = "正在转换，请稍候...";

      try {
        const response = await axios.post("http://localhost:8888/pdf/convert", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });

        this.statusMessage = response.data;
      } catch (error) {
        this.statusMessage = "转换失败: " + error.message;
      }
    },
  },
};
</script>

<style scoped>
.status-message {
  margin-top: 20px;
  color: green;
}
</style>
