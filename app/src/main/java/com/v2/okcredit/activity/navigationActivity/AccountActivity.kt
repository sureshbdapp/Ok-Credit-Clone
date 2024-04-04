package com.v2.okcredit.activity.navigationActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.san.app.activity.BaseActivity
import com.v2.okcredit.R
import com.v2.okcredit.activity.customer.CustomerActivity
import com.v2.okcredit.databinding.ActivityAccountBinding
import com.v2.okcredit.databinding.ActivityCustomerBinding
import com.v2.okcredit.model.User
import com.v2.okcredit.room.KasbonRoomDb
import com.v2.okcredit.utils.Prefs
import com.v2.okcredit.utils.Tools
import io.reactivex.disposables.CompositeDisposable
//import kotlinx.android.synthetic.main.activity_account.*
//import kotlinx.android.synthetic.main.view_action_bar.*
import java.text.NumberFormat
import kotlin.math.abs

class AccountActivity : BaseActivity() {
    private var TAG: String = "AccountActivity"

    private lateinit var user: User
    private lateinit var db: KasbonRoomDb
    private var disposable: CompositeDisposable? = null

    private lateinit var binding: ActivityAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize views
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(binding.rootToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.rootToolbar.toolbar.setNavigationOnClickListener { onBackPressed() }

        disposable = CompositeDisposable()
        db = KasbonRoomDb.getDatabase(this)

        binding.accountSummary.setOnClickListener { gotoAccSummary() }
        binding.languageContainer.setOnClickListener { gotoLanguage() }
    }

    private fun setProfileData() {
        user = intent.getParcelableExtra("user")!!
        Log.d(TAG, "User===> $user")

        if (user.name.isEmpty()) supportActionBar?.title = user.phone
        else supportActionBar?.title = user.name

        binding.customerCount.text = user.customers.size.toString()

        binding.balance.text = Tools.getCurrency("0")

        calculateBalance()

        val locale = Prefs.getString("locale")
        if (locale == "en") {
            binding.activeLanguage.text = "English"
        } else {
            binding.activeLanguage.text = "Indonesian"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateBalance() {
        var debitedAmt = 0.0
        var creditAmt = 0.0

        for (c in user.customers) {
            if (c.balanceType == getString(R.string.due)) debitedAmt += c.balance.toDouble()
            else creditAmt += c.balance.toDouble()
        }

        Log.d(CustomerActivity.tag, "creditAmt --- $creditAmt  ---debitedAmt--- $debitedAmt")
        val bal = creditAmt - debitedAmt
        val temp = NumberFormat.getInstance().format(abs(bal))

        Log.d(CustomerActivity.tag, "bal --- $bal  ---temp--- $temp")

        binding.balance.text = "₹$temp"

        if (bal >= 0.0) binding.balance.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        else binding.balance.setTextColor(ContextCompat.getColor(this, R.color.red))
    }

    private fun gotoLanguage() {
        //startActivity(Intent(this, LanguageSelectionActivity::class.java))
    }

    private fun gotoAccSummary() {
        //startActivity(Intent(this, StatementActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Called on Account")
        // set profile data
        setProfileData()
    }

    override fun onStop() {
        super.onStop()
        disposable?.clear()
    }
}
