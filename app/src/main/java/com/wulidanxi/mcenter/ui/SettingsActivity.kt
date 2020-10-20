package com.wulidanxi.mcenter.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.wulidanxi.mcenter.R
import com.wulidanxi.mcenter.util.PackageUtils
import kotlinx.android.synthetic.main.settings_activity.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initDarkMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        setSupportActionBar(toolbar_setting)
        toolbar_setting.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar_setting.setNavigationOnClickListener {
            setResult(0)
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
        private lateinit var mContext : Activity
        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = requireActivity()
        }
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val info = PackageUtils.getPackageInfo("com.wulidanxi.mcenter")
            val version = PackageUtils.getVersionString(info)
            Log.d("版本号", "onCreatePreferences: $version")
            findPreference<Preference>("version")?.summary = version
            findPreference<Preference>("theme")?.onPreferenceChangeListener = this
        }

        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
            if (preference?.key == "theme"){
                val date = preference.summary.toString()
                Log.d("监听", "onPreferenceChange: 进入监听")
                Log.d("当前值", "onPreferenceChange:$date")
                if (newValue.toString() == "light"){
                    Log.d("模式", "onPreferenceChange: 浅色模式")
                    Log.d("新的值", "onPreferenceChange:${newValue.toString()} ")
                    val sharedPreferences = mContext.getSharedPreferences(
                        "darkMode",
                        Context.MODE_PRIVATE
                    )
                    val mode = sharedPreferences?.getInt("open", -1)
                    Log.d("当前模式", "onPreferenceChange:$mode ")
                    val editor = sharedPreferences?.edit()
                    editor?.putInt("open", 2)
                    editor?.apply()
                    val intent = Intent(mContext, SettingsActivity::class.java)
                    startActivity(intent)
                    mContext.overridePendingTransition(R.anim.start_anim, R.anim.out_anim)
                    mContext.finish()
                }
                if (newValue.toString() == "dark"){
                    Log.d("模式", "onPreferenceChange: 深色模式")
                    Log.d("新的值", "onPreferenceChange:${newValue.toString()} ")
                    val sharedPreferences = mContext.getSharedPreferences(
                        "darkMode",
                        Context.MODE_PRIVATE
                    )
                    val mode = sharedPreferences?.getInt("open", -1)
                    Log.d("当前模式", "onPreferenceChange:$mode ")
                    val editor = sharedPreferences?.edit()
                    editor?.putInt("open", 1)
                    editor?.apply()
                    val intent = Intent(mContext, SettingsActivity::class.java)
                    startActivity(intent)
                    mContext.overridePendingTransition(R.anim.start_anim, R.anim.out_anim)
                    mContext.finish()
                }
                if (newValue.toString() == "follow"){
                    Log.d("模式", "onPreferenceChange: 跟随系统")
                    Log.d("新的值", "onPreferenceChange:${newValue.toString()} ")
                    val sharedPreferences = mContext.getSharedPreferences(
                        "darkMode",
                        Context.MODE_PRIVATE
                    )
                    val mode = sharedPreferences?.getInt("open", -1)
                    Log.d("当前模式", "onPreferenceChange:$mode ")
                    val editor = sharedPreferences?.edit()
                    editor?.putInt("open", 0)
                    editor?.apply()
                    val intent = Intent(mContext, SettingsActivity::class.java)
                    startActivity(intent)
                    mContext.overridePendingTransition(R.anim.start_anim, R.anim.out_anim)
                    mContext.finish()
                }

            }
            return true
        }
    }
}
