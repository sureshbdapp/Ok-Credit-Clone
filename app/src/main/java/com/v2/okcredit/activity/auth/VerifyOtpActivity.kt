package com.v2.okcredit.activity.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.v2.okcredit.R
import com.v2.okcredit.activity.HomeActivity
import com.v2.okcredit.api.RestAPI
import com.v2.okcredit.api.RestAdapter
import com.v2.okcredit.api.callback.OtpResponse
import com.v2.okcredit.databinding.ActivityVerifyOtpBinding
import com.v2.okcredit.model.User
import com.v2.okcredit.room.KasbonRoomDb
import com.v2.okcredit.utils.Prefs
import com.v2.okcredit.utils.Tools
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class VerifyOtpActivity : AppCompatActivity() {
    lateinit var binding:ActivityVerifyOtpBinding
    private var tag = "VerifyOtpActivity"
    private var mobile: String? = null

    private lateinit var db: KasbonRoomDb

    private var api: RestAPI? = null
    private var disposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize views
        initViews()
    }

    private fun initViews() {
        mobile = intent.getStringExtra("mobile")

        binding.tvOtpDelivery.text = getString(R.string.your_otp_will_be_delivered, mobile)

        disposable = CompositeDisposable()
        api = RestAdapter.getInstance()
        db = KasbonRoomDb.getDatabase(this)

        binding.pinViewOtp.setPinViewEventListener { pinView, _ ->
            Tools.hideKeyboard(pinView)
            //verifyOtp(pinView.value)
            if (pinView.value == "123456"){
                saveUser()
            }else {
                Toast.makeText(this, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyOtp(otp: String) {
        disposable!!.add(
            api!!.verifyUser(mobile!!, otp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showLoader() }
                .doFinally { hideLoader() }
                .subscribe({ handleSuccess(it) }) { handleError(it) }
        )
    }

    private fun showLoader() {
        binding.loader.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.loader.visibility = View.GONE
    }

    private fun handleSuccess(data: OtpResponse) {
        Log.d(tag, "handleSuccess $data")

        if (data.validOTP) {
            saveUser()
        } else {
            Toast.makeText(this, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show()
        }

    }


    private fun handleError(t: Throwable?) {
        Log.d(tag, "handleError:  ${t?.localizedMessage}")
        Toast.makeText(this, "Error : ${t?.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    private fun saveUserToDb(): User {
        db.userDao().insert(User(phone = mobile))
        return db.userDao().getUser(mobile!!)
    }

    private fun saveUser() {
        disposable!!.add(
            Single.create<User> { e -> e.onSuccess(saveUserToDb()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(tag, "On Success: Data Written Successfully! $it")
                    Prefs.save("phone", it.phone)
                    gotoHome()
                }) { Log.d(tag, "On Error:  $it") }
        )
    }

    private fun gotoHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        disposable?.clear()
    }
}
