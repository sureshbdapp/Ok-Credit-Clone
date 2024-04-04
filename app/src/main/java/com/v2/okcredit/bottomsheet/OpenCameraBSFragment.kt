package com.v2.okcredit.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.v2.okcredit.R
import com.v2.okcredit.databinding.GalleryTypePickerBottomSheetBinding

class OpenCameraBSFragment : BottomSheetDialogFragment() {
   lateinit var binding : GalleryTypePickerBottomSheetBinding
    var bottomSheetDialog: Dialog? = null
    var mContext: Context? = null
    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.bottomSheetDialog)
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        //Get the content View
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.gallery_type_picker_bottom_sheet,null,false)
       // val contentView = View.inflate(context, R.layout.gallery_type_picker_bottom_sheet, null)
        dialog.setContentView(binding.root)
        bottomSheetDialog = dialog
        mContext = activity
        val bottomSheet = bottomSheetDialog!!.window!!.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        bottomSheet.setBackgroundResource(R.drawable.corner_top_white)
        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED

        //Set the coordinator layout behavior
        val params = (binding.root).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior

        prepareView()
        setOnClickListner(binding)
        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
    }

    private fun prepareView() {
        if (arguments != null) {
            Log.e("***********", ""+arguments?.getString("title"))

        }

    }

     fun setOnClickListner(binding: GalleryTypePickerBottomSheetBinding) {

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.camera.setOnClickListener {

            dismiss() // add validation


        }
        binding.gallery.setOnClickListener {
            dismiss()
        }
    }


}
