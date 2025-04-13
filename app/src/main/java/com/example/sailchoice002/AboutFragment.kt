package com.example.sailchoice002

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.sailchoice002.BuildConfig
import com.example.sailchoice002.R

class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val androidVersionTextView = view.findViewById<TextView>(R.id.androidVersionTextView)
        val appVersionTextView = view.findViewById<TextView>(R.id.appVersionTextView)

        val androidVersion = Build.VERSION.RELEASE
        val appVersion = BuildConfig.VERSION_NAME

        androidVersionTextView.text = "Android Version: $androidVersion"
        appVersionTextView.text = "App Version: $appVersion"
        return view
    }
}