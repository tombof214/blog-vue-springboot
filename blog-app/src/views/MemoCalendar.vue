<template>
  <div id="customizedCalendar">
    <!-- 按钮：显示/隐藏任务 -->
    <el-button @click="toggleTasks" type="primary" round size="mini">
      {{ showTasks ? '隐藏任务' : '显示任务' }}
    </el-button>

    <el-calendar :first-day-of-week="7" v-model="value">
      <template slot="dateCell" slot-scope="{ date, data }">
        <div
          slot="reference"
          class="div-Calendar"
          style="position: relative; z-index: 10"
        >
          <p>{{ data.day.split('-').slice(2).join('-') }}</p>

          <!-- 任务列表，根据 showTasks 控制是否显示 -->
          <div v-if="showTasks && tasks[data.day]" class="task-list">
            <div
              v-for="(task, index) in tasks[data.day].split(',')"
              :key="index"
              @click="showTaskDetail(task)"
              class="task-name"
            >
              <a style="cursor: pointer; color: #4caf50; text-decoration: underline;">
                {{ task }}
              </a>
            </div>
          </div>
        </div>
        <div v-if="data.isSelected" id="selectP"></div>
      </template>
    </el-calendar>

    <div id="button">
      <el-button @click="skip('preYear')" type="primary" round size="mini"><i class="el-icon-arrow-left"></i>年</el-button>
      <el-button @click="skip('preMonth')" type="warning" round size="mini"><i class="el-icon-arrow-left"></i>月</el-button>
      <el-button @click="skip('preDay')" type="success" round size="mini"><i class="el-icon-arrow-left"></i>日</el-button>
      <el-button @click="skip('today')" type="info" round size="mini">今天</el-button>
      <el-button @click="skip('postDay')" type="success" round size="mini">日<i class="el-icon-arrow-right"></i></el-button>
      <el-button @click="skip('postMonth')" type="warning" round size="mini">月<i class="el-icon-arrow-right"></i></el-button>
      <el-button @click="skip('postYear')" type="primary" round size="mini">年<i class="el-icon-arrow-right"></i></el-button>
    </div>

    <!-- 弹出框，显示任务详情 -->
    <el-dialog
      :visible.sync="dialogVisible"
      title="任务详情"
      width="50%"
      @close="resetDialog">
      <div v-if="currentTask">
        <h3>{{ currentTask.title }}</h3>
        <p><strong>详细内容：</strong></p>
        <p>{{ currentTask.body }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import moment from 'moment';
import axios from 'axios';

export default {
  name: "Calendar",
  data() {
    return {
      value: new Date(),
      tasks: {}, // 用来存储日期和任务的映射
      showTasks: false, // 控制任务的显示/隐藏
      currentTask: null, // 当前选中的任务
      dialogVisible: false, // 控制弹出框的显示
    }
  },
  computed: {
    selectDate() {
      return moment(this.value).format("YYYY-MM-DD");
    }
  },
  methods: {
    skip(flag) {
      if (flag === 'preYear') this.value = new Date(this.value.setFullYear(this.value.getFullYear() - 1));
      else if (flag === 'preMonth') this.value = new Date(this.value.setMonth(this.value.getMonth() - 1));
      else if (flag === 'preDay') this.value = new Date(this.value.setDate(this.value.getDate() - 1));
      else if (flag === 'today') this.value = new Date();
      else if (flag === 'postDay') this.value = new Date(this.value.setDate(this.value.getDate() + 1));
      else if (flag === 'postMonth') this.value = new Date(this.value.setMonth(this.value.getMonth() + 1));
      else if (flag === 'postYear') this.value = new Date(this.value.setFullYear(this.value.getFullYear() + 1));
    },

    // 从后端获取备忘录，并将任务根据日期添加到 tasks 对象中
    getMemos() {
      axios.get('http://localhost:8888/api/memos') // 修改为你自己后端的 API 地址
        .then(response => {
          // 遍历备忘录，按照截止日期（dueDate）将任务添加到 tasks 对象中
          response.data.forEach(memo => {
            const formattedDate = moment(memo.dueDate).format('YYYY-MM-DD');  // 格式化日期
            if (!this.tasks[formattedDate]) {
              this.tasks[formattedDate] = memo.title;
            } else {
              this.tasks[formattedDate] += `, ${memo.title}`;
            }
          });
        })
        .catch(error => {
          console.error("获取备忘录失败:", error);
        });
    },

    // 切换任务显示/隐藏
    toggleTasks() {
      this.showTasks = !this.showTasks; // 切换 showTasks 的值
    },

    // 显示任务详情弹框
    showTaskDetail(task) {
      // 在弹框中显示任务详情
      this.currentTask = {
        title: task, // 假设任务就是标题，具体细节可以扩展
        body: '任务的详细内容（可以从后端获取并展示）'
      };
      this.dialogVisible = true; // 显示弹框
    },

    // 重置弹框内容
    resetDialog() {
      this.currentTask = null; // 清空当前任务
    }
  },
  mounted() {
    this.getMemos(); // 组件加载时获取备忘录
  }
}
</script>

<style lang="scss" scoped>
#customizedCalendar {
  width: 100%;
  height: 100%;

  #button {
    margin-top: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  #selectP {
    width: 30px;
    height: 30px;
    background-color: purple; /* 设置选中的日期为紫色 */
    position: absolute;
    border-radius: 50%;
    opacity: 0.6;
  }

  ::v-deep .el-calendar__header {
    background-color: #3c4b63;
    padding: 3px 5px;
    border: none;
    display: flex;
    justify-content: center;

    .el-calendar__button-group {
      display: none;
    }

    .el-calendar__title {
      color: white !important;
      font-size: 18px;
      font-weight: bolder;
    }
  }

  ::v-deep .el-calendar__body {
    padding: 0;
  }

  ::v-deep .el-calendar-table {
    thead {
      th {
        padding: 0;
        background-color: #3c4b63;
        color: white;
      }
    }

    .is-selected {
      .el-calendar-day {
        p {
          color: black;
        }
      }
    }

    .el-calendar-day {
      padding: 0;
      height: 40px;
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;

      p {
        color: black;
        z-index: 1;
        user-select: none;
      }
    }
  }

  ::v-deep .el-calendar-table__row {
    .prev, .next {
      .el-calendar-day {
        p {
          color: #f0d9d5;
        }
      }
    }

    td {
      &:first-child, &:last-child {
        background-color: #f5f5f5;
      }
    }
  }

  .task-list {
    position: absolute;
    top: 5px;
    left: 15px;
    font-size: 12px;
    color: #4caf50;
    display: block; /* Ensure tasks are displayed block level */
  }

  .task-name {
    margin-bottom: 3px; /* Add space between tasks */
  }

  .task-name a {
    color: #4caf50;
    text-decoration: none;
  }

  .task-name a:hover {
    color: #388e3c;
    text-decoration: none;
  }

  button {
    padding: 3px 10px;
  }
}
</style>
