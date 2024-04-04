package com.v2.okcredit.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.v2.okcredit.R
import com.v2.okcredit.databinding.ItemPhoneContactBinding
import com.v2.okcredit.model.PhoneContact
//import kotlinx.android.synthetic.main.item_phone_contact.view.*


class PhoneContactsAdapter(
    private val contactList: MutableList<PhoneContact>,
    private val context: Context
) :
    RecyclerView.Adapter<PhoneContactsAdapter.MyViewHolder>() {

    private var mListener: OnItemClickListener? = null

    class MyViewHolder(view: ItemPhoneContactBinding) : RecyclerView.ViewHolder(view.root) {
        val tvName: TextView = view.tvContactName
        val tvPhone: TextView = view.tvContactNumber
        val ivPhoto: ImageView = view.ciContactPhoto
        val addContact: FrameLayout = view.flAddContactBtn
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
        val binding = ItemPhoneContactBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyViewHolder(binding)

    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact: PhoneContact = contactList[position]
        holder.tvName.text = contact.name
        holder.tvPhone.text = contact.phone

        if (contact.profileImage !== null) {
            Glide.with(context)
                .load(Uri.parse(contact.profileImage))
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivPhoto)
        } else {
            Glide.with(context)
                .load(R.drawable.ic_customer)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivPhoto)
        }

        holder.addContact.setOnClickListener {
            mListener?.onItemClick(it, position)
        }

    }

    override fun getItemCount() = contactList.size
}