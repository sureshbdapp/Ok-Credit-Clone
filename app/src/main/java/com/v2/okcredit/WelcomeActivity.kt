package com.v2.okcredit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.v2.okcredit.activity.auth.LoginActivity
//import com.v2.okcredit.adapter.ImageSlider
import com.v2.okcredit.databinding.ActivityWelcomeBinding
import com.v2.okcredit.utils.Prefs
//import kotlinx.android.synthetic.main.activity_welcome.*
import java.util.*


class WelcomeActivity : AppCompatActivity() {
  private var binding : ActivityWelcomeBinding?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Initialize views
        initViews()
    }

    private fun initViews() {
//        val adapter = ImageSlider(this)
       // binding!!.imageSlider.sliderAdapter = adapter
        Log.e("TAG", "Get local : : " + Locale.getDefault())

        val locale = Prefs.getString("locale")

        if (locale == "en") {
//            binding?.tvTerms?.makeLinks(
//                Pair(
//                    "Terms of service",
//                    View.OnClickListener { openLink("https://www.google.com/") }),
//                Pair("Privacy Policy", View.OnClickListener { openLink("https://www.google.com/") })
//            )
        } else {
//            binding?.tvTerms?.makeLinks(
//                Pair(
//                    "Ketentuan layanan",
//                    View.OnClickListener { openLink("https://www.google.com/") }),
//                Pair(
//                    "Kebijakan Privasi",
//                    View.OnClickListener { openLink("https://www.google.com/") })
//            )
        }

        binding?.btnVerify?.setOnClickListener { goToLogin() }
    }

    private fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun openLink(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
