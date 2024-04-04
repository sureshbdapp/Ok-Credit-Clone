package com.v2.okcredit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.util.Log
import com.san.app.activity.BaseActivity
import com.v2.okcredit.activity.HomeActivity
import com.v2.okcredit.utils.Prefs
import com.v2.okcredit.utils.Utils
import p32929.easypasscodelock.Utils.EasyLock
import p32929.easypasscodelock.Utils.LockscreenHandler
import java.util.logging.Handler

@SuppressLint("CustomSplashScreen")
class SplashActivity : LockscreenHandler() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)



        //Check if user is logged in or not

        val handler = android.os.Handler(Looper.getMainLooper())
        handler.postDelayed({
            checkLogin()
        },1500)
    }


    private fun checkLogin() {
        val phone = Prefs.getString("phone")
        Log.d("SplashActivity", "SignIn status phone is => $phone")
        if (phone.isNullOrEmpty()) gotoWelcome()
        else gotoHome()
    }

    private fun gotoHome() {
        Utils.setLocale(this, Prefs.getString("locale"))

        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun gotoWelcome() {
        Utils.setLocale(this, "en")
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }


}
