package com.example.tasktimer

interface OnTaskClickListener {
    fun onEditClick(task: Task)
    fun onDeleteTask(task: Task)
}