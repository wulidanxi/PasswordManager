package com.wulidanxi.mcenter.ui

/**
 * update by wulidanxi on 2020/10/20
 */

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.navigation.NavigationView
import com.stars.permissionx.PermissionEasier
import com.wulidanxi.mcenter.R
import com.wulidanxi.mcenter.adapter.KtBaseAdapter
import com.wulidanxi.mcenter.adapter.MyAdapter
import com.wulidanxi.mcenter.db.Content
import com.wulidanxi.mcenter.db.PasswordDatabase
import com.wulidanxi.mcenter.util.ExcelUtils
import com.wulidanxi.mcenter.util.java.fingerprint.FingerprintCallback
import com.wulidanxi.mcenter.util.java.fingerprint.FingerprintVerifyManager
import com.wulidanxi.mcenter.view.RecyclerViewDivider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener {

    private var mList: MutableList<Content> = mutableListOf()
    private var mAdapter: MyAdapter = MyAdapter(mList, R.layout.item_show)
    private var filePath =
        "/storage/emulated/0/Android/data/com.wulidanxi.mcenter/"
    private val permissions: Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private lateinit var builder : FingerprintVerifyManager.Builder


    override fun onCreate(savedInstanceState: Bundle?) {
        initDarkMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        builder = FingerprintVerifyManager.Builder(this@MainActivity)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, 0, 0)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        // 侧滑栏部分
        val headerView = navigation_view.getHeaderView(0)
        val webUtil = navigation_view.menu.findItem(R.id.web_util)
        navigation_view.setNavigationItemSelectedListener(this)
        webUtil.setOnMenuItemClickListener(this)
        footer_item_out.setOnClickListener(this)
        footer_item_setting.setOnClickListener(this)
        // 动态权限获取
        PermissionEasier.INSTANCE.requestNoDenied(this, permissions)
        if (Build.VERSION.SDK_INT >= 30) {
            PermissionEasier.INSTANCE.requestNoDenied(
                this,
                arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            )
        }
        // 数据库实例化
        val contentDao = PasswordDatabase.getDatabase(this).contentDao()
        // RecyclerView 适配器设置
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_content.layoutManager = layoutManager
        thread {
            for ((index, content) in contentDao.loadAllContent().withIndex()) {
                mList.add(index, content)
            }
            mAdapter = MyAdapter(mList, R.layout.item_show)
            val resId: Int = resources.getColor(R.color.defaultBackground, theme)
            val decoration = RecyclerViewDivider(
                this,
                DividerItemDecoration.VERTICAL,
                15,
                resId
            )
            rv_content.addItemDecoration(decoration)
            rv_content.post {
                rv_content.adapter = mAdapter
                if (intent.extras != null) {
                    rv_content.scrollBy(0, intent.extras!!.getInt("scrollY", 0))
                    Log.d("接收的距离", intent.extras!!.getInt("scrollY", 0).toString())
                }
            }

            mAdapter.setItemLongClickListener(object : KtBaseAdapter.ItemLongClick {
                override fun onItemLongClick(v: View, position: Int) {
                    showPopMenu(v, position)
                }
            })
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            footer_item_setting -> {
                Log.d("TAG", "onClick: 进入设置")
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            footer_item_out -> {
                Log.d("TAG", "onClick:退出 ")
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val contentDao = PasswordDatabase.getDatabase(this).contentDao()
        if (resultCode == 1) {
            thread {
                mList.clear()
                for ((index, content1) in contentDao.loadAllContent().withIndex()) {
                    mList.add(index, content1)
                }
                runOnUiThread {
                    mAdapter.updateData(mList)
                    rv_content.adapter = mAdapter
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (menu.javaClass.simpleName.equals("MenuBuilder", ignoreCase = true)) {
            try {
                val method = menu.javaClass.getDeclaredMethod(
                    "setOptionalIconsVisible",
                    java.lang.Boolean.TYPE
                )
                method.isAccessible = true
                method.invoke(menu, true)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("mode", 0)
                startActivityForResult(intent, 1)
            }

            R.id.output_excel -> {
                val excelFileName = "PasswordBackups.xls"
                filePath += excelFileName
                val file = File(filePath)
                if (!file.exists()) {
                    file.createNewFile()
                }
                val title: List<String> = listOf("渠道", "账号", "密码", "时间")
                val excelList: MutableList<Content> = mutableListOf()
                Log.d("文件路径", "onOptionsItemSelected: $filePath")
                val contentDao = PasswordDatabase.getDatabase(this).contentDao()
                thread {
                    for ((index, content) in contentDao.loadAllContent().withIndex()) {
                        excelList.add(index, content)
                        if (excelList.equals("")) {
                            runOnUiThread {
                                ToastUtils.showShort("没有可导出的数据")
                            }
                        }
                    }
                    runOnUiThread {
                        ExcelUtils.initExcel(filePath, title)
                        ExcelUtils.writeObjListToExcel(excelList, filePath, this)
                        filePath = "/storage/emulated/0/Android/data/com.wulidanxi.mcenter/"
                    }
                }
            }
            R.id.read_excel -> {
                val contentDao = PasswordDatabase.getDatabase(this).contentDao()
                //val path = "/storage/emulated/0/PasswordManager/PasswordBackups.xls"
                filePath += "PasswordBackups.xls"
                val file = File(filePath)
                if (!file.exists()) {
                    ToastUtils.showShort("文件不存在")
                } else {
                    val tempList = mList
                    mList.clear()
                    if (ExcelUtils.readExcel(file, this) == null) {
                        ToastUtils.showShort("文件为空")
                        return false
                    }
                    mList = ExcelUtils.readExcel(file, this) as MutableList<Content>
                    if (mList.equals("")) {
                        ToastUtils.showShort("文件无内容")
                        return false
                    }
                    if (mList != tempList) {
                        thread {
                            for (i in 0 until mList.size) {
                                contentDao.insertContent(mList[i])
                            }
                        }
                        Toast.makeText(this, "导入完成", Toast.LENGTH_SHORT).show()
                    } else {
                        ToastUtils.showShort("无需重复导入")
                        return false
                    }
                    filePath = "/storage/emulated/0/Android/data/com.wulidanxi.mcenter/"
                    thread {
                        mList.clear()
                        for ((index, content1) in contentDao.loadAllContent().withIndex()) {
                            mList.add(index, content1)
                        }
                        runOnUiThread {
                            mAdapter.updateData(mList)
                            rv_content.adapter = mAdapter
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initDarkMode() {
        val sharedPreferences = getSharedPreferences("darkMode", Context.MODE_PRIVATE)
        when (sharedPreferences.getInt("open", 2)) {
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun showPopMenu(view: View, position: Int) {
        val list: List<Content>? = mAdapter.getData()
        val content: Content? = list?.get(position)
        val id: Long? = content?.id
        val menu = PopupMenu(this, view)
        val contentDao = PasswordDatabase.getDatabase(this).contentDao()
        menu.menuInflater.inflate(R.menu.popu_menu, menu.menu)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    if (id != null) {
                        thread {
                            contentDao.deleteContentById(id)
                            mList.clear()
                            for ((index, content1) in contentDao.loadAllContent().withIndex()) {
                                mList.add(index, content1)
                            }
                            runOnUiThread {
                                mAdapter.updateData(mList)
                                rv_content.adapter = mAdapter
                            }
                        }
                    }
                }
                R.id.modify -> {
                    if (id != null) {
                        val intent = Intent(applicationContext, AddActivity::class.java)
                        intent.putExtra("mode", 1)
                        intent.putExtra("id", id)
                        builder.callback(fingerprintCallback)
                            .state(1)
                            .intent(intent)
                            .enableAndroidP(true)
                            .build()
                    }
                }
            }
            true
        }
        menu.setOnDismissListener {}
        menu.gravity = Gravity.END
        menu.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return false
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.web_util -> {
                val intent = Intent(applicationContext, WebAddressActivity::class.java)
                builder.callback(fingerprintCallback)
                    .state(0)
                    .intent(intent)
                    .enableAndroidP(true)
                    .build()
            }
        }
        return false
    }

    private val fingerprintCallback: FingerprintCallback = object : FingerprintCallback {
        override fun onSucceeded(state: Int, intent: Intent) {
            when(state){
                0 -> {
                    startActivity(intent)
                }
                1 -> {
                    startActivityForResult(intent, 2)
                }
            }
        }

        override fun onFailed() {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.biometricprompt_verify_failed),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onUsepwd() {
            TODO("Not yet implemented")
        }

        override fun onCancel() {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.fingerprint_cancel),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onHwUnavailable() {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.biometricprompt_finger_hw_unavailable),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onNoneEnrolled() {
            //弹出提示框，跳转指纹添加页面
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle(getString(R.string.biometricprompt_tip))
                .setMessage(getString(R.string.biometricprompt_finger_add))
                .setCancelable(false)
                .setNegativeButton(
                    getString(R.string.biometricprompt_finger_add_confirm)
                ) { _: DialogInterface?, _: Int ->
                    val intent = Intent(Settings.ACTION_FINGERPRINT_ENROLL)
                    startActivity(intent)
                }
                .setPositiveButton(
                    getString(R.string.biometricprompt_cancel)
                ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                .create().show()
        }
    }
}
