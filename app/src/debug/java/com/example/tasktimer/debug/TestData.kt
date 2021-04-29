package com.example.tasktimer.debug

import android.content.ContentResolver
import android.content.ContentValues
import com.example.tasktimer.database.TaskContract
import com.example.tasktimer.database.TimingContract
import java.util.*
import kotlin.math.roundToInt

class TestData  {

    companion object {


        fun generateTestData (contentResolver: ContentResolver) {
            val SECS_IN_DAYS = 86400
            val LOWER_BOUND = 10
            val UPPER_BOUND = 50
            val MAX_DURATION = SECS_IN_DAYS / 6

            val projection: Array<String> = arrayOf(TaskContract.Columns.ID)
            val uri = TaskContract.CONTENT_URI
            val cursor = contentResolver.query(uri, projection, null, null, null)

            if ((cursor != null) && (cursor.moveToNext())) {
                do {
                    val taskId: Long = cursor.getLong(cursor.getColumnIndex(TaskContract.Columns.ID))

                    //generate timings between UPPER and LOWER bounds

                    val loopCount = LOWER_BOUND + getRandomInt(UPPER_BOUND - LOWER_BOUND)

                    for (i in 0 until loopCount) {
                        val randomDate: Long = randomDateTime()

                        val duration: Long = getRandomInt(MAX_DURATION).toLong()

                        val testTiming = TestTiming(taskId, randomDate, duration)

                        saveCurrentTiming(contentResolver, testTiming)
                    }
                } while (cursor.moveToNext())
                    cursor.close()
            }
        }
        private fun getRandomInt(max: Int): Int {
            return (Math.random() * max).roundToInt()
        }
        private fun randomDateTime(): Long {
            val startYear = 2020
            val endYear = 2021

            val sec = getRandomInt(59)
            val min = getRandomInt(59)
            val hour = getRandomInt(23)
            val month = getRandomInt(11)
            val year = startYear + getRandomInt(endYear - startYear)

            val gc: GregorianCalendar = GregorianCalendar(year, month, 1)
            val day = 1 + getRandomInt(gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) - 1)

            gc.set(year,month,hour,min,sec)
            return gc.timeInMillis
        }
        private fun saveCurrentTiming(contentResolver: ContentResolver, currentTiming: TestTiming) {
            val values = ContentValues()
            values.put(TimingContract.Columns.TIMINGS_TASK_ID, currentTiming.taskId)
            values.put(TimingContract.Columns.TIMINGS_START_TIME, currentTiming.startTime)
            values.put(TimingContract.Columns.TIMINGS_DURATION, currentTiming.duration)

            contentResolver.insert(TimingContract.CONTENT_URI, values)
        }


    }


}