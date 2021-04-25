package com.example.tasktimer.debug

data class TestTiming(var taskId: Long, var startTime: Long, var duration: Long) {

    init {
        startTime /= 1000
    }
}