package com.mobileheros.camera.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import android.widget.EditText


object CommonUtils {
    fun dp2px(ctx: Context, dp: Float): Int {
        val scale: Float = ctx.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun canVerticalScroll(editText: EditText): Boolean {
        val scrollY = editText.scrollY
        val scrollRange = editText.layout.height
        val scrollExtent =
            editText.height - editText.compoundPaddingTop - editText.compoundPaddingBottom
        val scrollDifference = scrollRange - scrollExtent
        if (scrollDifference == 0) return false
        return scrollY > 0 || scrollY < scrollDifference - 1
    }


    fun openGooglePlay(context: Context, packageName: String) {
        var marketFound = false
        val ratingIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        val resolveInfoList = context.packageManager.queryIntentActivities(ratingIntent, 0)
        for (resolveInfo in resolveInfoList) {
            if (resolveInfo.activityInfo.applicationInfo.packageName == "com.android.vending") {
                val activityInfo = resolveInfo.activityInfo
                val componentName =
                    ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name)
                ratingIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                ratingIntent.component = componentName
                context.startActivity(ratingIntent)
                marketFound = true
                break
            }
        }
        if (!marketFound) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            webIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(webIntent)
        }
    }

    fun goSetting(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + context.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun isNetworkConnected(context: Context) : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

}