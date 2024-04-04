package com.v2.okcredit.activity.customer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.san.app.activity.BaseActivity
import com.v2.okcredit.R
import com.v2.okcredit.api.RestAPI
import com.v2.okcredit.api.RestAdapter
import com.v2.okcredit.databinding.ActivityAddTransactionBinding
import com.v2.okcredit.databinding.ActivityHomeBinding
import com.v2.okcredit.model.Customer
import com.v2.okcredit.model.Transaction
import com.v2.okcredit.utils.Prefs
import com.v2.okcredit.utils.Tools
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class AddTransactionActivity : BaseActivity(), View.OnClickListener {
    private var tag: String = "CustomerActivity"

    private var customer: Customer? = null
    private var type: String? = null
    private var isDataValid: Boolean = false

    private var redColor: Int = 0
    private var greenColor: Int = 0

    private var imageURI: String = ""

    private var transact: Transaction? = null

    private var api: RestAPI? = null
    private var disposable: CompositeDisposable? = null
    private lateinit var binding: ActivityAddTransactionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize views
        initViews()

        setCustomerData()
    }

    private fun initViews() {
        customer = intent.getParcelableExtra("customer")
        type = intent.getStringExtra("type")
        Log.d(tag, "customer===> $customer")

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        disposable = CompositeDisposable()
        api = RestAdapter.getInstance()

        redColor = ContextCompat.getColor(this, R.color.red)
        greenColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        binding.etAmount.afterTextChanged { onAmountTextChange(it) }

        binding.dateContainer.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.btnAddPhoto.setOnClickListener(this)
        binding.removeImg.setOnClickListener(this)
    }

    @SuppressLint("SimpleDateFormat")
    private fun setCustomerData() {
        if (customer?.profileImage !== null) {
            Glide.with(this)
                .load(Uri.parse(customer?.profileImage))
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfile)
        } else {
            Glide.with(this)
                .load(R.drawable.ic_account_125dp)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfile)
        }
        binding.tvName.text = customer?.name

        if (customer?.balanceType == getString(R.string.due)) {
            binding.tvHeaderAmount.setTextColor(redColor)
        } else {
            binding.tvHeaderAmount.setTextColor(greenColor)
        }

        binding.tvHeaderAmount.text = Tools.getCurrency(customer!!.balance)
        binding.tvDate.text = SimpleDateFormat("dd MMM yyyy").format(Calendar.getInstance().time)

        if (type == "debit") {
            binding.etAmount.setTextColor(redColor)
            binding.tvCurrencySymbol.setTextColor(redColor)
        } else {
            binding.etAmount.setTextColor(greenColor)
            binding.tvCurrencySymbol.setTextColor(greenColor)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dateContainer -> {
                openCalender()
            }
            R.id.btnAddPhoto -> {
                requestStoragePermission()
            }
            R.id.removeImg -> {
                removeImage()
            }
            R.id.btnSubmit -> {
                addTransaction()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun openCalender() {
        val cal = Calendar.getInstance()

        val dpd = DatePickerDialog(
            this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                binding.tvDate.text = SimpleDateFormat("dd MMM yyyy").format(cal.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
    }

    private fun addTransaction() {
        val amount = binding.etAmount.text.toString().trim()
        if (amount.isEmpty()) {
            isDataValid = false
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = getString(R.string.valid_amount)
            return
        }
        val note = binding.etNote.text.toString().trim()

        transact = Transaction(
            amount,
            type,
            Timestamp(System.currentTimeMillis()).toString(),
            note,
            image = imageURI
        )

        customer?.transactions?.add(transact!!)
        val intent = Intent().apply {
            putExtra("addTransaction", customer)
        }
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()

//        createTransact()
    }

    private fun createTransact() {
        val mobile = Prefs.getString("phone")
        val time = System.currentTimeMillis()
        disposable!!.add(
            api!!.createTransact(
                "1",
                mobile!!,
                transact?.amount!!,
                time.toString(),
                transact?.note!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showLoader() }
                .doFinally { hideLoader() }
                .subscribe({ handleSuccess(it) }) { handleError(it) }
        )
    }

    @SuppressLint("RestrictedApi")
    private fun showLoader() {
        binding.btnSubmit.visibility = View.GONE
        binding.transactLoader.visibility = View.VISIBLE
    }

    @SuppressLint("RestrictedApi")
    private fun hideLoader() {
        binding.transactLoader.visibility = View.GONE
        binding.btnSubmit.visibility = View.VISIBLE

    }

    private fun handleSuccess(data: ResponseBody) {
        Log.d(tag, "handleSuccess $data")
        customer?.transactions?.add(transact!!)
        val intent = Intent().apply {
            putExtra("addTransaction", customer)
        }
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()

    }


    private fun handleError(t: Throwable?) {
        Log.d(tag, "handleError:  ${t?.localizedMessage}")
        Toast.makeText(this, "Error : ${t?.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    private fun onAmountTextChange(s: String) {
        if (s.isNotEmpty()) {
            binding.dateContainer.visibility = View.VISIBLE
        } else {
            binding.dateContainer.visibility = View.GONE
        }
    }


    private fun removeImage() {
        binding.imageView.setImageDrawable(null)
        binding.imageContainer.visibility = View.GONE
        imageURI = ""
    }


    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(tag, "onActivityResult $requestCode $resultCode $data")
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) run {
                //for getting image from Camera
                binding.imageContainer.visibility = View.VISIBLE
                Glide.with(this)
                    .load(Uri.parse(mCurrentPhotoPath).path)
                    .into(binding.imageView)

                imageURI = Uri.parse(mCurrentPhotoPath).path.toString()
            } else
                if (requestCode == SELECT_FILE) run {
                    //for getting image from file
                    val uri = data!!.data
                    val type = uri?.let { getMimeType(this, it) }
                    if (type != null && type.isNotEmpty() && type != "null") {
                        if (type.toLowerCase().contains("jpg")
                            || type.toLowerCase().contains("png")
                            || type.toLowerCase().contains("jpeg")
                        ) {
                            binding.imageContainer.visibility = View.VISIBLE
                            Glide.with(this)
                                .load(uri)
                                .into(binding.imageView)

                            imageURI = uri.toString()

                        } else {
                            Toast.makeText(this, "Please provide valid image.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this, "Please provide valid image.", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else
                    if (requestCode == 101) run {
                        //for runtime permission
                        requestStoragePermission()
                    }
        }
    }
}
