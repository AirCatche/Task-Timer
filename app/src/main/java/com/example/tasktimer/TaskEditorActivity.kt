package com.example.tasktimer

import android.os.Bundle
import android.util.Log
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity

class TaskEditorActivity : AppCompatActivity(), OnSaveClicked, DialogEvents {

    companion object{
        private const val TAG = "AddEditActivity"
        private const val DIALOG_ID_CANCEL_EDIT = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
     //   if (supportFragmentManager.findFragmentById(R.id.fragment_addedit) == null) {
            val fragment = TaskEditorActivityFragment()
            val args = intent.extras
            fragment.arguments = args
            supportFragmentManager.beginTransaction().replace(R.id.fragment_addedit, fragment).commit()
        }

    }
    override fun onSaveClicked() {
        finish()
    }

    override fun onPositiveDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onPositiveDialogResult: starts")
    }

    override fun onNegativeDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onNegativeDialogResult: starts")
        finish()
    }

    override fun onDialogCancelled(dialogId: Int) {
        Log.d(TAG, "onDialogCancelled: starts")
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragment_addedit) as TaskEditorActivityFragment
                return if (fragment.canClose()) { super.onOptionsItemSelected(item) }
                else {
                    showConfirmation()
                    true
                }
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showConfirmation () {
        val dialog = AppDialog()
        val args = Bundle()
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT)
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.editDiag_cancelMessage))
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.edinDiag_positive_caption)
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.edinDiag_negative_caption)

        dialog.arguments = args
        dialog.show(supportFragmentManager, null)
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed: starts")
        val fragment: TaskEditorActivityFragment = supportFragmentManager.findFragmentById(R.id.fragment_addedit) as TaskEditorActivityFragment
        if (fragment.canClose()) {
            super.onBackPressed()
        } else {
            showConfirmation()
        }
    }
}