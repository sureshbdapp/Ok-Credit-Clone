package com.v2.okcredit.base

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.v2.okcredit.R

open class BaseFragment : Fragment() {

    fun StatusBar() {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
    }

    fun hideSoftKeyboard() {
        if (requireActivity().currentFocus != null) {
            val inputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
        }
    }
    fun changeFragment_back(targetFragment: Fragment) {
        val transaction = requireFragmentManager().beginTransaction()
        transaction.setCustomAnimations(
            R.anim.anim_right,
            R.anim.anim_left,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        transaction.replace(R.id.frameLayout, targetFragment, "fragment")
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun changeFragment_back(targetFragment: Fragment, from: String) {
        val bundle = Bundle()
        bundle.putString("from", from)
        targetFragment.arguments = bundle
        val transaction = requireFragmentManager().beginTransaction()
        transaction.setCustomAnimations(
            R.anim.anim_right,
            R.anim.anim_left,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        transaction.replace(R.id.frameLayout, targetFragment, "fragment")
        transaction.addToBackStack(null)
        transaction.commit()
    }

}