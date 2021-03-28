package com.example.tasktimer

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException
import java.lang.IllegalArgumentException

class AppDialog : DialogFragment() {
    private var dialogEvents: DialogEvents? = null
    companion object {
        private const val TAG = "AppDialog"
        const val DIALOG_ID = "id"
        const val DIALOG_MESSAGE: String = "message"
        const val DIALOG_POSITIVE_RID = "Positive RID"
        const val DIALOG_NEGATIVE_RID = "Negative RID"
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: Entering onAttach, activity is + $context")
        super.onAttach(context)

        if (context !is DialogEvents) {
            throw ClassCastException("$context must implement interface DialogEvents")
        }

        dialogEvents = context
    }
    override fun onDetach() {
        Log.d(TAG, "onDetach: Entering")
        super.onDetach()
        dialogEvents = null
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "onCreateDialog: starts")
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val args: Bundle? = arguments
        val dialogId: Int
        val messageString: String?
        var positiveStringId: Int
        var negativeStringId: Int

        if (args != null) {
            dialogId = args.getInt(DIALOG_ID)
            messageString = args.getString(DIALOG_MESSAGE)
            if (dialogId == 0 || messageString == null) {
                throw IllegalArgumentException("DIALOG_ID or DIALOG_MESSAGE not present in the bundle")
            }
            positiveStringId = args.getInt(DIALOG_POSITIVE_RID)
            if (positiveStringId == 0) {
                positiveStringId = R.string.ok
            }
            negativeStringId = args.getInt(DIALOG_NEGATIVE_RID)
            if (negativeStringId == 0) {
                negativeStringId = R.string.cancel
            }
        } else {
            throw IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the bundle")
        }
        builder.setMessage(messageString)
            .setPositiveButton(positiveStringId) { dialog, which ->
                dialogEvents?.onPositiveDialogResult(dialogId, args)

            }
            .setNegativeButton(negativeStringId) { dialog, which ->
                dialogEvents?.onNegativeDialogResult(dialogId, args)
            }
        return builder.create()
    }
    override fun onCancel(dialog: DialogInterface) {
        Log.d(TAG, "onCancel: called")
        if (dialogEvents != null) {
            val dialogId = arguments?.getInt(DIALOG_ID)
            dialogEvents!!.onDialogCancelled(dialogId!!)
        }
    }
    override fun onDismiss(dialog: DialogInterface) {
        Log.d(TAG, "onDismiss: called")
        super.onDismiss(dialog)
    }
}