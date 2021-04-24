package com.example.tasktimer.database.entity

import android.util.Log
import java.io.Serializable
import java.util.*

/**
 *  Simple timing object
 *  Sets start time when task created calculate time since creation, when setDuration called
 *
 */

data class Timing(private val id: Long? = null, private val task: Task, private var startTime: Long? = null, private var duration: Long? = null ): Serializable {
    companion object {
        private const val serialVersionUID: Long = 20210424
        private const val TAG = "Timing"
    }

    init {
        val currentTime = Date()
        startTime = currentTime.time / 1000
        duration = 0
    }

    fun setDuration() {
        val currentTime = Date()
        startTime?.let {
            duration = (currentTime.time / 1000) - it
        }
        Log.d(TAG, "setDuration: ${task._id} - start time: $startTime | Duration: $duration")
    }


}