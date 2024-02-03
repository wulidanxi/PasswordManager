package com.wulidanxi.mcenter.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wulidanxi.mcenter.R
import com.wulidanxi.mcenter.databinding.ActivityAddBinding
import com.wulidanxi.mcenter.db.Content
import com.wulidanxi.mcenter.db.PasswordDatabase
import com.wulidanxi.mcenter.util.AES128Utils
import java.util.*
import kotlin.concurrent.thread

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    lateinit var channel: String
    lateinit var mItem: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_add)
        setSupportActionBar(binding.toolbar2)
        binding.toolbar2.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        binding.toolbar2.setNavigationOnClickListener {
            setResult(0)
            finish()
        }
        val intent = intent
        initData(intent)
        val contentDao = PasswordDatabase.getDatabase(this).contentDao()
        mItem = resources.getStringArray(R.array.spinner_name)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mItem)
        arrayAdapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        binding.spinnerChannel.adapter = arrayAdapter
        binding.spinnerChannel.onItemSelectedListener = ItemSelectListener()
        binding.buttonSubmit.setOnClickListener {
            val date = Date()
            val format = android.icu.text.SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
            val time: String = format.format(date)
            Log.d("当前时间", time)
            val account: String = binding.edAccount.text.toString()
            val password: String = binding.edPwd.text.toString()
            if (channel != "" && account != "" && password != "") {
                when (channel){
                    "Other" -> {
                        channel = binding.etCustomChannel.text.toString()
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
                            newContent.password = aesPwd
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
                "Other" ->  binding.etCustomChannel.visibility = View.VISIBLE
                else -> binding.etCustomChannel.visibility = View.GONE
            }
        }
    }

    private fun initData(data: Intent?) {
        val contentDao = PasswordDatabase.getDatabase(this).contentDao()
        when (data?.extras?.get("mode")) {
            0 -> {
                binding.spinnerChannel.setSelection(0)
            }
            1 -> {
                val id: Long = data.extras?.get("id") as Long
                Log.d("id", "" + id)
                thread {
                    val content = contentDao.loadContentWithId(id)
                    binding.edAccount.setText(content.account)
                    val decPwd = AES128Utils.decrypt("7701",content.password)
                    binding.edPwd.setText(decPwd)
                    when (content.channel) {
                        "QQ" -> binding.spinnerChannel.setSelection(0)
                        "微信" -> binding.spinnerChannel.setSelection(1)
                        "BiliBili" -> binding.spinnerChannel.setSelection(2)
                        else -> {
                            binding.spinnerChannel.setSelection(3)
                            binding.etCustomChannel.visibility = View.VISIBLE
                            binding.etCustomChannel.setText(content.channel)
                        }
                    }
                }
            }
        }
    }
}
