package com.example.tasktimer.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Database class.
 * The only class that should use DB is [Provider].
 */

class Database private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {

    companion object{
        const val TAG = "Database"
        const val DATABASE_NAME = "TaskTimer.db"
        const val DATABASE_VERSION = 1

        private var instance: Database? = null
        operator fun invoke(context: Context) = synchronized(this) {
            if (instance == null) {
                instance = Database(context)
            }
            instance
        }

    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "onCreate: starts")
        val sql = "CREATE TABLE ${TaskContract.TABLE_NAME} (" +
                "${TaskContract.Columns._ID} INTEGER PRIMARY KEY NOT NULL, " +
                "${TaskContract.Columns.TASKS_NAME} TEXT NOT NULL, " +
                "${TaskContract.Columns.TASKS_DESCRIPTION} TEXT, " +
                "${TaskContract.Columns.TASKS_SORT_ORDER} INTEGER );"

        Log.d(TAG, sql)
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade: starts")
        when(oldVersion) {
            1 ->{
                //hehehe
            }
            else -> {
                //ne hehehe
            }
        }
        Log.d(TAG, "onUpgrade: ends")
    }
}