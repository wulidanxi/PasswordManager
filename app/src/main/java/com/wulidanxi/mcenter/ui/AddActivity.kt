package com.wulidanxi.mcenter.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.wulidanxi.mcenter.R
import com.wulidanxi.mcenter.db.Content
import com.wulidanxi.mcenter.db.PasswordDatabase
import com.wulidanxi.mcenter.util.AES128Utils
import kotlinx.android.synthetic.main.activity_add.*
import java.util.*
import kotlin.concurrent.thread

class AddActivity : AppCompatActivity() {

    lateinit var channel: String
    lateinit var mItem: Array<String>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setSupportActionBar(toolbar2)
        toolbar2.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar2.setNavigationOnClickListener {
            setResult(0)
            finish()
        }
        val intent = intent
        initData(intent)
        val contentDao = PasswordDatabase.getDatabase(this).contentDao()
        mItem = resources.getStringArray(R.array.spinner_name)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mItem)
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_channel!!.adapter = arrayAdapter
        spinner_channel.onItemSelectedListener = ItemSelectListener()
        button_submit.setOnClickListener {
            val date = Date()
            val format = android.icu.text.SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
            val time: String = format.format(date)
            Log.d("当前时间", time)
            val account: String = ed_account.text.toString()
            val password: String = ed_pwd.text.toString()
            if (channel != "" && account != "" && password != "") {
                when (channel){
                    "Other" -> {
                        channel = et_custom_channel.text.toString()
                        if (channel == ""){
                            channel = "Other"
                        }
                    }
                }
                val aesPwd : String  = AES128Utils.encrypt("7701",password)!!
                val content = Content(channel, account, aesPwd, time)
                when (intent.extras?.get("mode")) {
                    0 -> {
                        thread {
                            contentDao.insertContent(content)
                        }
                    }
                    1 -> {
                        val id: Long = intent.extras!!.get("id") as Long
                        thread {
                            val newContent = contentDao.loadContentWithId(id)
                            newContent.channel = channel
                            newContent.account = account
                            newContent.password = password
                            newContent.date = time
                            contentDao.updateContent(newContent)
                        }
                    }
                }
                setResult(1)
                finish()
            } else {
                Toast.makeText(this, "请填写完整信息！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    internal inner class ItemSelectListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            channel = mItem[position]
            when (channel){
                "Other" ->  et_custom_channel.visibility = View.VISIBLE
                else -> et_custom_channel.visibility = View.GONE
            }
        }
    }

    private fun initData(data: Intent?) {
        val contentDao = PasswordDatabase.getDatabase(this).contentDao()
        when (data?.extras?.get("mode")) {
            0 -> {
                spinner_channel.setSelection(0)
            }
            1 -> {
                val id: Long = data.extras?.get("id") as Long
                Log.d("id", "" + id)
                thread {
                    val content = contentDao.loadContentWithId(id)
                    ed_account.setText(content.account)
                    val decPwd = AES128Utils.decrypt("7701",content.password)
                    ed_pwd.setText(decPwd)
                    when (content.channel) {
                        "QQ" -> spinner_channel.setSelection(0)
                        "微信" -> spinner_channel.setSelection(1)
                        "BiliBili" -> spinner_channel.setSelection(2)
                        else -> {
                            spinner_channel.setSelection(3)
                            et_custom_channel.visibility = View.VISIBLE
                            et_custom_channel.setText(content.channel)
                        }
                    }
                }
            }
        }
    }
}
