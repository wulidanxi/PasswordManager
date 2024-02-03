package com.wulidanxi.mcenter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wulidanxi.mcenter.databinding.ActivityAccountDetailsBinding

class AccountDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}