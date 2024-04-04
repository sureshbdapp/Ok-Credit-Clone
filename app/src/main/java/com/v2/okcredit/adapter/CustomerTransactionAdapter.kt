package com.v2.okcredit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.v2.okcredit.R
import com.v2.okcredit.databinding.ItemCustomerTransactionBinding
import com.v2.okcredit.model.Transaction
import com.v2.okcredit.utils.Tools
//import kotlinx.android.synthetic.main.item_customer_transaction.view.*


class CustomerTransactionAdapter(
    private val transactions: MutableList<Transaction>,
    private val context: Context
) :
    RecyclerView.Adapter<CustomerTransactionAdapter.MyViewHolder>() {

    private var mListener: OnItemClickListener? = null

    class MyViewHolder(binding: ItemCustomerTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        val cvContainer: MaterialCardView = binding.cvContainer
        val llBottomContainer: LinearLayout = binding.llBottomContainer
        val tvNote: TextView = binding.tvNote
        val tvAmount: TextView = binding.tvAmount
        val tvDate: TextView = binding.tvDate
        val tvTotalAmount: TextView = binding.tvTotalAmount
        val ivIcon: ImageView = binding.ivIcon

    }

    // Define the mListener interface
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    // Define the method that allows the parent activity or fragment to define the listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCustomerTransactionBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyViewHolder(binding)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val t: Transaction = transactions[position]
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        if (t.type == "debit") {
            params.apply { gravity = Gravity.END }
            holder.tvAmount.setTextColor(context.resources.getColor(R.color.red))
        } else {
            params.apply { gravity = Gravity.START }
            holder.tvAmount.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
        }
        holder.cvContainer.layoutParams = params
        holder.llBottomContainer.layoutParams = params

        if (t.note!!.isNotEmpty()) {
            holder.tvNote.text = t.note
            holder.tvNote.visibility = View.VISIBLE
        }

        val amt = Tools.getCurrency(t.amount!!)

        holder.tvAmount.text = amt

        if (t.image.isNullOrEmpty()) {
            holder.ivIcon.visibility = View.GONE
        }
        holder.tvTotalAmount.text =
            "$amt " + if (t.type == "debit") context.getString(R.string.due) else context.getString(
                R.string.advance
            )

        holder.tvDate.text = Tools.getFormattedTime(t.createdAt, "hh:mm aa")

        holder.cvContainer.setOnClickListener { mListener?.onItemClick(it, position) }

    }


    override fun getItemCount() = transactions.size
}