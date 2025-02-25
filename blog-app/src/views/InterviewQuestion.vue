<template>
  <div class="chat-container">
    <h2>Chatbot</h2>
    <div v-if="response">
      <p><strong>每日一题</strong></p>
      <p>{{ response }}</p>
    </div>
    <div v-else>
      <p>等待回应...</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";
export default {
  name:"InterviewQuestion",
  data() {
    return {
      message: "根据资料库，生成一道可以口述回答的java面试题，尽量随机，不要重复",  // 预定义消息
      response: null,  // 存储返回的聊天响应
    };
  },
  mounted() {
    // 页面加载时自动发送消息
    this.sendMessage();
  },
  methods: {
    async sendMessage() {
      try {
        const response = await axios.post('http://localhost:8888/chat/send', {
          message: this.message,  // 传递预定义的消息
        });
        // 将返回的响应存储在 response 中
        console.log(response.data);
        console.log(response.data.id);
        console.log(response.data.textResponse);
        this.response = response.data.textResponse;
      } catch (error) {
        console.error("发送请求时出错", error);
      }
    },
  },
};
</script>

<style scoped>
.chat-container {
  text-align: center;
  margin-top: 20px;
}
</style>
