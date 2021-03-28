package com.example.tasktimer

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

class TaskEditorActivity : AppCompatActivity(), OnSaveClicked {

    companion object{
      //  private const val TAG = "AddEditActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragment = TaskEditorActivityFragment()
        val args = intent.extras
        //val args = Bundle()
        //args.putSerializable(Task::class.java.simpleName, task)

        fragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.fragment_addedit, fragment).commit()
    }
    override fun onSaveClicked() {
        finish()
    }
}