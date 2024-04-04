package com.v2.okcredit.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.san.app.activity.BaseActivity
import com.v2.okcredit.R
import com.v2.okcredit.api.RestAPI
import com.v2.okcredit.api.RestAdapter
import com.v2.okcredit.databinding.ActivityLoginBinding
import com.v2.okcredit.utils.Tools
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.util.*


class LoginActivity : BaseActivity() {
    private var tag = "LoginActivity"
  lateinit var binding : ActivityLoginBinding
    private lateinit var mobileNo: String

    private var api: RestAPI? = null
    private var disposable: CompositeDisposable? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      binding = DataBindingUtil.setContentView(this,R.layout.activity_login)

        //Initialize views
        initViews()
    }

    private fun initViews() {

        val locale: String = Locale.getDefault().country
        Log.d(tag, "countryID $locale")

        disposable = CompositeDisposable()
        api = RestAdapter.getInstance()

        binding.tvPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().trim().length == 10) {
                    binding.btnOK.backgroundTintList =
                        ContextCompat.getColorStateList(this@LoginActivity, R.color.green_dark)
                    binding.btnOK.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.white))
                    binding.btnOK.iconTint =
                        ContextCompat.getColorStateList(this@LoginActivity, R.color.white)
                } else {
                    binding.btnOK.backgroundTintList =
                        ContextCompat.getColorStateList(this@LoginActivity, R.color.white)
                    binding.btnOK.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.black_88))
                    binding.btnOK.iconTint =
                        ContextCompat.getColorStateList(this@LoginActivity, R.color.black_88)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.btnOK.setOnClickListener {
            mobileNo = binding.tvPhone.text.toString().trim()
            if (Tools.isValidMobile(mobileNo)) {
                Tools.hideKeyboard(binding.tvPhone)
                //requestOtp()
                goToVerifyScreen()
            } else {
                Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestOtp() {
        disposable!!.add(
            api!!.createUser("91", mobileNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showLoader() }
                .doFinally { hideLoader() }
                .subscribe({ handleSuccess(it) }) { handleError(it) }
        )
    }

    private fun showLoader() {
        binding.btnOK.visibility = View.GONE
        binding.loader.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.loader.visibility = View.GONE
        binding.btnOK.visibility = View.VISIBLE
    }

    private fun handleSuccess(body: ResponseBody) {
        Log.d(tag, "handleSuccess ${body.string()}")
        goToVerifyScreen()
    }

    private fun goToVerifyScreen() {
        val intent = Intent(this, VerifyOtpActivity::class.java)
        intent.putExtra("mobile", mobileNo)
        startActivity(intent)
    }


    private fun handleError(t: Throwable?) {
        Log.d(tag, "handleError:  ${t?.localizedMessage}")
    }

    override fun onStop() {
        super.onStop()
        disposable?.clear()
    }

}
