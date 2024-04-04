package com.v2.okcredit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.san.app.activity.BaseActivity
import com.v2.okcredit.databinding.ActivityLanguageSelectionBinding
import com.v2.okcredit.utils.Prefs
import com.v2.okcredit.utils.Utils

class LanguageSelectionActivity : BaseActivity(), View.OnClickListener {
private var binding: ActivityLanguageSelectionBinding?=null
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      binding = DataBindingUtil.setContentView(this,R.layout.activity_language_selection)

        //Initialize views
        initViews()
    }

    private fun initViews() {
        binding?.cvEng?.setOnClickListener(this)
        binding?.cvInd?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvEng -> gotoWelcomeScreen("en")
            R.id.cvInd -> gotoWelcomeScreen("in")
        }
    }

    private fun gotoWelcomeScreen(localeName: String) {
        Prefs.save("locale", localeName)
        Utils.setLocale(this, localeName)

//        val phone = Prefs.getString("phone")
//        if (phone!!.isNotBlank()) {
//            finish()
//        } else {
        startActivity(Intent(this, WelcomeActivity::class.java))
//        }
    }
}
