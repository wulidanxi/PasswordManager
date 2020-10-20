package com.wulidanxi.mcenter.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.text.format.Formatter
import com.blankj.utilcode.util.Utils
import com.wulidanxi.mcenter.util.java.FreezeUtils
import java.io.File

object PackageUtils {

    /**
     * Get packageInfo
     * @param info ApplicationInfo
     * @param flag Flag mask
     * @return PackageInfo
     * @throws PackageManager.NameNotFoundException
     */
    @Throws(PackageManager.NameNotFoundException::class)
    fun getPackageInfo(info: ApplicationInfo, flag: Int = 0): PackageInfo {
        return getPackageInfo(info.packageName, flag)
    }

    /**
     * Get packageInfo
     * @param packageName Package name string
     * @param flag Flag mask
     * @return PackageInfo
     * @throws PackageManager.NameNotFoundException
     */
    @Throws(PackageManager.NameNotFoundException::class)
    fun getPackageInfo(packageName: String, flag: Int = 0): PackageInfo {
        val packageInfo = Utils.getApp().packageManager.getPackageInfo(
            packageName, FreezeUtils.PM_FLAGS_GET_APP_INFO or flag
        )
        if (FreezeUtils.isAppFrozen(packageInfo.applicationInfo)) {
            val pmFlag =
                PackageManager.MATCH_DISABLED_COMPONENTS
            val info = Utils.getApp().packageManager.getPackageInfo(packageInfo.packageName, 0)

            return Utils.getApp().packageManager.getPackageArchiveInfo(info.applicationInfo.sourceDir, pmFlag or flag)?.apply {
                applicationInfo.sourceDir = info.applicationInfo.sourceDir
                applicationInfo.nativeLibraryDir = info.applicationInfo.nativeLibraryDir
            } ?: throw PackageManager.NameNotFoundException()
        }
        return packageInfo
    }

    /**
     * Get all installed apps in device
     * @return list of apps
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getInstallApplications(): List<ApplicationInfo> {
        return try {
            val timer = TimeRecorder()
            timer.start()
            val list = Utils.getApp().packageManager?.getInstalledApplications(PackageManager.GET_SHARED_LIBRARY_FILES) ?: listOf()
            timer.end()
            list
        } catch (e: Exception) {
            throw Exception()
        }
    }

    /**
     * Get version code of an app
     * @param packageInfo PackageInfo
     * @return version code as Long Integer
     */
    fun getVersionCode(packageInfo: PackageInfo): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.longVersionCode
        }
    }

    /**
     * Get version string of an app ( 1.0.0(1) )
     * @param packageInfo PackageInfo
     * @return version code as String
     */
    fun getVersionString(packageInfo: PackageInfo): String {
        return try {
            "${packageInfo.versionName ?: "null"}(${getVersionCode(packageInfo)})"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    /**
     * Get version string of an app ( 1.0.0(1) )
     * @param versionName Version name
     * @param versionCode Version code
     * @return version code as String
     */
    fun getVersionString(versionName: String, versionCode: Long): String {
        return "${versionName}(${versionCode})"
    }

    /**
     * Get target api string of an app ( API 30 )
     * @param packageInfo PackageInfo
     * @return version code as String
     */
    fun getTargetApiString(packageInfo: PackageInfo): String {
        return try {
            "API ${packageInfo.applicationInfo.targetSdkVersion}"
        } catch (e: PackageManager.NameNotFoundException) {
            "API ?"
        }
    }

    /**
     * Judge that whether an app uses split apks
     * @param packageInfo PackageInfo
     * @return true if it uses split apks
     */
    fun isSplitsApk(packageInfo: PackageInfo): Boolean {
        try {
            val path = packageInfo.applicationInfo.sourceDir
            File(path.substring(0, path.lastIndexOf("/"))).listFiles()?.let {
                for (file in it) {
                    if (file.name.startsWith("split_config.")) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }

    /**
     * Format size number to string
     * @param size Size of file
     * @return String of size number (100KB)
     */
    fun sizeToString(size: Long): String {
        return "(${Formatter.formatFileSize(Utils.getApp(), size)})"
    }
}