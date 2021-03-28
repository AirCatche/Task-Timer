package com.example.tasktimer

import android.os.Bundle

interface DialogEvents {
    fun onPositiveDialogResult(dialogId: Int, args: Bundle)
    fun onNegativeDialogResult(dialogId: Int, args: Bundle)
    fun onDialogCancelled(dialogId: Int)
}