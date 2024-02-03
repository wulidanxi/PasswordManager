package com.wulidanxi.mcenter.ui

import ExtractSite
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.wulidanxi.mcenter.R
import com.wulidanxi.mcenter.databinding.ActivityWebAddressBinding


class WebAddressActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding:ActivityWebAddressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarNat)
        binding.toolbarNat.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        binding.toolbarNat.setNavigationOnClickListener {
            setResult(0)
            finish()
        }

        binding.btConfusion.setOnClickListener(this)
        binding.btNoConfusion.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bt_confusion -> {
                if (!binding.edContent.text.isNullOrEmpty()) {
                    if (!binding.edWeb.text.isNullOrEmpty() && binding.edNotWeb.text.isNullOrEmpty()) {
                        val webSite = binding.edWeb.text.toString()
                        val msg = binding.edContent.text.toString()
                        val result = ExtractSite.addChinese(webSite, msg)
                        if (result == "error"){
                            ToastUtils.showShort("混淆内容存在非中文字符!")
                        }else{
                            binding.edNotWeb.setText(result)
                        }
                    }
                }else{
                    ToastUtils.showShort("混淆内容不能为空!")
                }
            }
            R.id.bt_no_confusion -> {
                if (!binding.edNotWeb.text.isNullOrEmpty()){
                    val confusionData = binding.edNotWeb.text.toString()
                    val result = ExtractSite.removeChinese(confusionData)
                    binding.edWeb.setText(result)
                }else{
                    ToastUtils.showShort("待混淆内容不能为空!")
                }
            }
        }
    }
}