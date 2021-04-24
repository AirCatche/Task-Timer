package com.example.tasktimer.ui

import com.example.tasktimer.database.entity.Task

interface OnTaskClickListener {
    fun onEditClick(task: Task)
    fun onDeleteTask(task: Task)
    fun onTaskLongClick(task: Task)
}