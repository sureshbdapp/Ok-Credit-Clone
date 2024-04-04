package com.v2.okcredit.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.v2.okcredit.R
import com.v2.okcredit.databinding.ItemCustomerBinding
import com.v2.okcredit.model.Customer
import com.v2.okcredit.model.Transaction
import com.v2.okcredit.model.User
import com.v2.okcredit.utils.Tools
//import kotlinx.android.synthetic.main.item_customer.view.*


class CustomerAdapter(
    private val contactList: MutableList<Customer>,
    private val context: Context
) :
    RecyclerView.Adapter<CustomerAdapter.MyViewHolder>() {
    private lateinit var transaction: Transaction
    private var mListener: OnItemClickListener? = null

    class MyViewHolder(binding: ItemCustomerBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvName: TextView = binding.tvCustomerName
        val pic: ImageView = binding.pic
        val tvBalance: TextView = binding.tvBalance
        val tvBalanceStatus: TextView = binding.tvBalanceStatus
              val dateDetails : TextView = binding.lastPayment
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
        val binding = ItemCustomerBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, pos: Int) {
        val c: Customer = contactList[pos]
        holder.tvName.text = c.name

        if (c.profileImage !== null) {
            Glide.with(context)
                .load(Uri.parse(c.profileImage))
                .apply(RequestOptions.circleCropTransform())
                .into(holder.pic)
        } else {
            Glide.with(context)
                .load(R.drawable.ic_account_125dp)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.pic)
        }
        holder.tvBalanceStatus.text = c.balanceType.toUpperCase()
        holder.tvBalance.text = Tools.getCurrency(c.balance)
        if (c.balanceType == context.getString(R.string.due)) {
            holder.tvBalance.setTextColor(ContextCompat.getColor(context, R.color.red))
        } else {
            holder.tvBalance.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }
//        for (i in allUsers.indices){
//            if (allUsers[i].customers.equals(c)){
//                holder.dateDetails.text = c.transactions[i].createdAt
//            }
//        }
        Log.e("transaction", c.transactions.toString())
        if (!c.transactions.isNullOrEmpty()){
            holder.dateDetails.text = Tools.getFormattedTime(c.transactions[c.transactions.size-1].createdAt, "dd MMM yyyy, hh:mm a")
        }
       // = Tools.getFormattedTime(transaction.createdAt, "dd MMM yyyy, hh:mm a")
        holder.itemView.setOnClickListener { mListener?.onItemClick(it, pos) }

    }

    override fun getItemCount() = contactList.size
}