package com.v2.okcredit.activity.customer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.san.app.activity.BaseActivity
import com.v2.okcredit.R
import com.v2.okcredit.databinding.ActivityTransactionBinding
import com.v2.okcredit.model.Customer
import com.v2.okcredit.model.Transaction
import com.v2.okcredit.utils.Tools

//import kotlinx.android.synthetic.main.activity_transaction.*


class TransactionActivity : BaseActivity(), View.OnClickListener {
    private var tag: String = "CustomerActivity"
    lateinit var binding: ActivityTransactionBinding
    private lateinit var customer: Customer
    private lateinit var transaction: Transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction)
        //setContentView(R.layout.activity_transaction)


        //Initialize views
        initViews()

        setCustomerData()
    }

    private fun initViews() {
        customer = intent.getParcelableExtra("customer")!!
        transaction = intent.getParcelableExtra("transaction")!!
        Log.d(tag, "transaction===> $transaction")

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.btnWpShare.setOnClickListener(this)
        binding.btnDelete.setOnClickListener(this)
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
        if (!transaction.image.isNullOrBlank()) {
            Glide.with(this)
                .load(transaction.image)
                .into(binding.ivReceipt)
        }

        binding.tvName.text = customer.name
        binding.tvAmount.text = Tools.getCurrency(transaction.amount!!)
        binding.tvCalDate.text = Tools.getFormattedTime(transaction.createdAt, "dd MMM yyyy")
        binding.tvCreatedAt.text = Tools.getFormattedTime(transaction.createdAt, "dd MMM yyyy, hh:mm a")

        if (transaction.note!!.isNotEmpty()) {
            binding.noteContainer.visibility = View.VISIBLE
            binding.tvNote.text = transaction.note
        }

        if (transaction.type == "debit") {
            binding.tvAmount.setTextColor(resources.getColor(R.color.red))
            binding.tvDelete.text = getString(R.string.delete_credit)
        } else {
            binding.tvAmount.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            binding.tvDelete.text = getString(R.string.delete_payment)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnWpShare -> {
                Toast.makeText(this, "WIP", Toast.LENGTH_SHORT).show()
            }

            R.id.btnDelete -> {
                showDeleteDialog()
            }
        }
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(this)
        val title = if (transaction.type == "debit") {
            getString(R.string.delete_credit)
        } else {
            getString(R.string.delete_payment)
        }
        builder.setTitle(title)
            .setMessage(R.string.delete_msg)
            .setPositiveButton(R.string.delete) { dialog, _ ->
                dialog.dismiss()
                val intent = Intent().apply {
                    putExtra("transaction", transaction)
                    putExtra("transaction_del", true)
                }
                setResult(Activity.RESULT_OK, intent)
                onBackPressed()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        builder.create()
        builder.show()
    }

    private fun sendDataBackToHomeActivity() {
        val intent = Intent().apply { putExtra("transaction", transaction) }
        setResult(Activity.RESULT_OK, intent)
    }

}
