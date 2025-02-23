<template>
  <div class="calendar-container">
    <el-calendar v-model="value" id="calendar">
      <!-- Custom render for each day in the calendar -->
      <template
        slot="dateCell"
        slot-scope="{ date, data }">
        <div class="date-cell">
          <!-- Display the day of the month -->
          <div class="calendar-day">{{ data.day.split('-').slice(2).join('-') }}</div>

          <!-- Check for events on that day -->
          <div v-for="item in calendarData" :key="item.things">
            <div v-if="item.months.includes(data.day.split('-').slice(1)[0])">
              <div v-if="item.days.includes(data.day.split('-').slice(2).join('-'))">
                <el-tooltip class="item" effect="dark" :content="item.things" placement="right">
                  <div class="has-memo"></div>
                </el-tooltip>
              </div>
            </div>
          </div>
        </div>
      </template>
    </el-calendar>

    <div v-if="showMemo" class="memo-details">
      <h3>备忘录：{{ currentDate }}</h3>
      <ul>
        <li v-for="memo in memosForSelectedDate" :key="memo.id">{{ memo.text }} ({{ memo.reminderTime }})</li>
      </ul>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "MemoCalendar",
  data() {
    return {
      calendarData: [
        { months: ['09', '11'], days: ['15'], things: '看电影' },
        { months: ['10', '11'], days: ['02'], things: '去公园野炊' },
        { months: ['11'], days: ['02'], things: '看星星' },
        { months: ['11'], days: ['02'], things: '看月亮' }
      ],
      value: new Date(),
      memos: [],
      showMemo: false,
      currentDate: "",
      memosForSelectedDate: []
    };
  },
  created() {
    this.fetchMemos();
  },
  methods: {
    fetchMemos() {
      axios
        .get('/api/memos')
        .then(response => {
          this.memos = response.data;
        })
        .catch(error => {
          console.error("获取备忘录失败:", error);
        });
    },

    disabledDate(date) {
      const currentTime = new Date().getTime();
      return date.getTime() < currentTime;
    },

    dateCellRender(date) {
      const dateStr = this.formatDate(date);
      const memosForDate = this.memos.filter(memo => memo.reminderTime === dateStr);
      return memosForDate.length > 0 ? (
        <el-tooltip content="有备忘录" placement="top">
          <div class="has-memo"></div>
        </el-tooltip>
      ) : null;
    },

    formatDate(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    },

    handleDateChange(date) {
      const selectedDate = this.formatDate(date);
      this.showMemo = true;
      this.memosForSelectedDate = this.memos.filter(memo => memo.reminderTime === selectedDate);
    }
  }
};
</script>

<style scoped>
.calendar-container {
  position: relative;
  padding: 20px;
}

.date-cell {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80px;
}

.calendar-day {
  text-align: center;
  color: #202535;
  line-height: 30px;
  font-size: 12px;
}

.is-selected {
  color: #F8A535;
  font-size: 10px;
  margin-top: 5px;
}

.has-memo {
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 12px;
  height: 12px;
  background-color: red;
  border-radius: 50%;
  border: 2px solid #fff; /* White border for better contrast */
}

.memo-details {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  border: 1px solid #ccc;
}

.memo-details h3 {
  margin-bottom: 15px;
  font-size: 1.2em;
  color: #333;
}

#calendar .el-button-group > .el-button:not(:first-child):not(:last-child):after {
  content: '当月';
}
</style>
