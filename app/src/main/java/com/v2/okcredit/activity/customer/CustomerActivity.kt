package com.v2.okcredit.activity.customer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.v2.okcredit.R
import com.v2.okcredit.TransactionDateListener
import com.v2.okcredit.adapter.CustomerTransactionAdapter
import com.v2.okcredit.databinding.ActivityCustomerBinding
import com.v2.okcredit.databinding.ActivityHomeBinding
import com.v2.okcredit.model.Customer
import com.v2.okcredit.model.Transaction
import com.v2.okcredit.model.User
import com.v2.okcredit.room.KasbonRoomDb
import com.v2.okcredit.utils.Prefs
import com.v2.okcredit.utils.Tools
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import kotlin.math.abs


class CustomerActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val tag: String = "CustomerActivity"

        const val START_ACTIVITY_2_REQUEST_CODE = 2
        const val START_ACTIVITY_3_REQUEST_CODE = 3
    }


    private lateinit var transactions: MutableList<Transaction>
    private lateinit var transactionAdapter: CustomerTransactionAdapter
    private lateinit var dateListener : TransactionDateListener
    private lateinit var db: KasbonRoomDb
    private var disposable: CompositeDisposable? = null

    private lateinit var customer: Customer
    private lateinit var user: User
    private lateinit var binding: ActivityCustomerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize views
        initViews()

        //Set Customer data
        setCustomerData()
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun initViews() {
        user = intent.getParcelableExtra("user")!!
        customer = intent.getParcelableExtra("customer")!!
        Log.d(tag, "customer===> $customer")

        disposable = CompositeDisposable()
        db = KasbonRoomDb.getDatabase(this)

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        initializeTransactionRecyclerView()

        binding.callBtn.setOnClickListener(this)
        binding.reminderBtn.setOnClickListener(this)
       binding. btnAcceptPayment.setOnClickListener(this)
        binding.btnGiveCredit.setOnClickListener(this)
    }

    private fun initializeTransactionRecyclerView() {
        transactions = customer.transactions.toMutableList()
        transactionAdapter = CustomerTransactionAdapter(transactions, this)
        transactionAdapter.setOnItemClickListener(object :
            CustomerTransactionAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val t = transactions[position]
                dateListener.getDate(t)
                gotoTransactionScreen(t)
            }
        })
        binding.rvTransactions.adapter = transactionAdapter
        transactionAdapter.notifyDataSetChanged()
        setupTransactionUI()
        calculateBalance()
    }

    private fun setupTransactionUI() {
        if (transactions.isNullOrEmpty()) {
            binding.rvTransactions.visibility = View.GONE
            binding.totalAmtContainer.visibility = View.GONE
           binding. bottomButtonContainer.visibility = View.GONE
           binding. emptyLayout.mainConst.visibility = View.VISIBLE
           binding.emptyLayout.tvEmptyList.text = Tools.getSpannedText(getString(R.string.safe_secure_trans, customer.name))
        } else {
           binding. emptyLayout.mainConst.visibility = View.GONE
           binding. rvTransactions.visibility = View.VISIBLE
           binding. totalAmtContainer.visibility = View.VISIBLE
           binding. bottomButtonContainer.visibility = View.VISIBLE
        }
    }

    private fun gotoTransactionScreen(t: Transaction) {
        val intent = Intent(this, TransactionActivity::class.java)
        intent.putExtra("customer", customer)
        intent.putExtra("transaction", t)
        startActivityForResult(
            intent,
            START_ACTIVITY_2_REQUEST_CODE
        )
    }

    private fun gotoAddTransactionScreen(type: String) {
        val intent = Intent(this, AddTransactionActivity::class.java)
        intent.putExtra("customer", customer)
        intent.putExtra("type", type)
        startActivityForResult(
            intent,
            START_ACTIVITY_3_REQUEST_CODE
        )
    }

    private fun setCustomerData() {
        if (customer.profileImage !== null) {
            Glide.with(this)
                .load(Uri.parse(customer.profileImage))
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfile)
        } else {
            Glide.with(this)
                .load(R.drawable.ic_account_125dp)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfile)
        }
        binding.tvName.text = customer.name
    }

    private fun handleError(t: Throwable?) {
        Log.d(tag, "handleError:  ${t?.localizedMessage}")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.call_btn -> {
                val phoneIntent =
                    Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", customer.phone, null))
                startActivity(phoneIntent)
            }
            R.id.reminder_btn -> {
                Toast.makeText(this, "WIP", Toast.LENGTH_SHORT).show()
            }
            R.id.btnAcceptPayment -> {
                gotoAddTransactionScreen("credit")
            }
            R.id.btnGiveCredit -> {
                gotoAddTransactionScreen("debit")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_ACTIVITY_3_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                customer = data?.getParcelableExtra("addTransaction")!!
                transactions = customer.transactions.toMutableList()
                Log.d(tag, "customer got it===> $customer")
                updateCustomer()
            }
        } else if (requestCode == START_ACTIVITY_2_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val t: Transaction? = data!!.getParcelableExtra("transaction")
                val delTransaction = data.getBooleanExtra("transaction_del", false)
                if (delTransaction) {
                    transactions.remove(t)
                    customer.transactions = transactions
                }
                Log.d(tag, "Delete Tran $delTransaction & new transaction list $transactions")
                updateCustomer()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getUserFromDb(): User {
        val phone = Prefs.getString("phone")
        return db.userDao().getUser(phone!!)
    }

    private fun updateDb(): User {
        Log.d(tag, "Customer updated successfully inDb1 ${user.customers[0].balance}")
        user.customers.find { it.id == customer.id }?.apply {
            balance = customer.balance
            balanceType = customer.balanceType
            transactions = customer.transactions
        }
        Log.d(tag, "Customer updated successfully inDb2 ${user.customers[0].balance}")
        db.userDao().updateUser(user)
        return getUserFromDb()
    }

    private fun updateCustomer() {
        calculateBalance()
        disposable!!.add(
            Single.create<User> { e -> e.onSuccess(updateDb()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    user = it
                    initializeTransactionRecyclerView()
                    Log.d(tag, "Customer updated successfully $user")
                }) { handleError(it) }
        )
    }

    @SuppressLint("SetTextI18n")
    private fun calculateBalance() {
        var debitedAmt = 0.0
        var creditAmt = 0.0

        for (t in transactions) {
            if (t.type == "debit") debitedAmt += t.amount?.toDouble()!!
            else creditAmt += t.amount?.toDouble()!!
        }

        val bal = creditAmt - debitedAmt
        val temp = NumberFormat.getInstance().format(abs(bal))

        Log.d(tag, "bal --- $bal  ---temp--- $temp")

        binding.tvTotalBalance.text = "₹$temp"
        customer.balance = abs(bal).toString()
        if (bal >= 0.0) {
            binding.tvTotalBalance.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            binding.tvBalanceType.text = getString(R.string.advance)
            customer.balanceType = getString(R.string.advance)
        } else {
            binding.tvTotalBalance.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.tvBalanceType.text = getString(R.string.due)
            customer.balanceType = getString(R.string.due)
        }
        Log.d(tag, "Customer balance --> $customer")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop called from Home")
        disposable?.clear()
    }
}
