package com.v2.okcredit.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.san.app.activity.BaseActivity
import com.v2.okcredit.R
import com.v2.okcredit.databinding.MainActivityBinding


class MainActivity : BaseActivity() {
   private var binding : MainActivityBinding ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        navController = findNavController(R.id.fragment_nav_graph)

    }
}