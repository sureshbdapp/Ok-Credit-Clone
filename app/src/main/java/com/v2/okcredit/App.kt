package com.v2.okcredit

import android.app.Application
import com.v2.okcredit.utils.Prefs

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs.init(this)
    }
}