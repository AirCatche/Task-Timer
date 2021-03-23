package com.example.tasktimer

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

class TaskEditorActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "AddEditActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}