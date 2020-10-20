package com.wulidanxi.mcenter.ui

import ExtractSite
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.wulidanxi.mcenter.R
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_web_address.*
import java.util.*

class WebAddressActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_address)
        setSupportActionBar(toolbar_nat)
        toolbar_nat.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar_nat.setNavigationOnClickListener {
            setResult(0)
            finish()
        }

        bt_confusion.setOnClickListener(this)
        bt_no_confusion.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bt_confusion -> {
                if (!ed_content.text.isNullOrEmpty()) {
                    if (!ed_web.text.isNullOrEmpty() && ed_not_web.text.isNullOrEmpty()) {
                        val webSite = ed_web.text.toString()
                        val msg = ed_content.text.toString()
                        val result = ExtractSite.addChinese(webSite, msg)
                        if (result == "error"){
                            ToastUtils.showShort("混淆内容存在非中文字符!")
                        }else{
                            ed_not_web.setText(result)
                        }
                    }
                }else{
                    ToastUtils.showShort("混淆内容不能为空!")
                }
            }
            R.id.bt_no_confusion -> {
                if (!ed_not_web.text.isNullOrEmpty()){
                    val confusionData = ed_not_web.text.toString()
                    val result = ExtractSite.removeChinese(confusionData)
                    ed_web.setText(result)
                }else{
                    ToastUtils.showShort("待混淆内容不能为空!")
                }
            }
        }
    }
}