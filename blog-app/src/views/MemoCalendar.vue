<template>
  <div id="customizedCalendar">
    <el-calendar :first-day-of-week="7" v-model="value">
      <template slot="dateCell" slot-scope="{ date, data }">
        <div
          slot="reference"
          class="div-Calendar"
          @click="clickCalendar(data)"
          style="position: relative; z-index: 10"
        >
          <p>{{ data.day.split('-').slice(2).join('-') }}</p>
          <!-- Task input displayed below date -->
          <div v-if="tasks[data.day]" class="task-list">
            <el-tooltip class="item" effect="dark" :content="tasks[data.day]" placement="top">
              <div class="task-name">{{ tasks[data.day] }}</div>
            </el-tooltip>
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
  </div>
</template>

<script>
import moment from 'moment'
import PubSub from 'pubsub-js'

export default {
  name: "Calendar",
  data() {
    return {
      value: new Date(),
      tasks: {
        '2025-01-09': 'Task 1 - Meet with John',
        '2025-01-17': 'Task 2 - Meeting with team',
        '2025-01-23': 'Task 3 - Project deadline'
      },
      taskInput: '',
      editingDay: null
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
    clickCalendar(data) {
      // Here, we redirect to an edit page when a date cell is clicked
      window.location.href = `/edit-task?date=${data.day}`;  // Simple redirection for now, can be replaced with routing
    }
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
    background-color: purple; /* Purple color for selected day circle */
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
    bottom: 5px;
    left: 5px;
    font-size: 12px;
    color: #4caf50;
  }

  .task-name {
    cursor: pointer;
    &:hover {
      text-decoration: underline;
    }
  }

  button {
    padding: 3px 10px;
  }
}
</style>
