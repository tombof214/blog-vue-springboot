package com.shimh.entity;

public class MemoDetails {
    private String memoText;
    private String reminderTime;

    // 构造函数
    public MemoDetails(String memoText, String reminderTime) {
        this.memoText = memoText;
        this.reminderTime = reminderTime;
    }

    // Getter和Setter方法
    public String getMemoText() {
        return memoText;
    }

    public void setMemoText(String memoText) {
        this.memoText = memoText;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }
}
