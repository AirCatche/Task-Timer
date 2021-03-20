package com.example.tasktimer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AddEditActivityFragment : Fragment() {
    companion object{
        private const val TAG = "AddEditActivityFragment"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: starts")
        return inflater.inflate(R.layout.fragment_add_edit, container, false)
    }
}