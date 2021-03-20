package com.example.tasktimer

import android.nfc.Tag
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

class AddEditActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "AddEditActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        setSupportActionBar(findViewById(R.id.toolbar))

    }
}