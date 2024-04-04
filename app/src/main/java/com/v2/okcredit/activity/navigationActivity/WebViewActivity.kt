package com.v2.okcredit.activity.navigationActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.san.app.activity.BaseActivity
//import kotlinx.android.synthetic.main.activity_web_view.*
import android.webkit.WebViewClient
import com.v2.okcredit.R

class WebViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        //Initialize views
        initViews()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
//        val title = intent.getStringExtra("title")
//        Log.d("tag","title -- $title")
//        tvName.text = title
//        toolbar.setNavigationOnClickListener { onBackPressed() }
//
//        val url = intent.getStringExtra("url")
//        val webSettings = webview.settings
//        webSettings.javaScriptEnabled = true
//        webview.webViewClient = WebViewClient()
//        if (url != null) {
//            webview.loadUrl(url)
//        }
    }

}
