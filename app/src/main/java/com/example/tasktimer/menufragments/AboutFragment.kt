package com.example.tasktimer.menufragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tasktimer.BuildConfig
import com.example.tasktimer.R

class AboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_about, container, false)
        val tvAboutVersion = view.findViewById<TextView>(R.id.tv_about_version)
        tvAboutVersion.text = "v ${BuildConfig.VERSION_NAME}"

        return view
    }
}