package com.mobileheros.camera

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.drake.net.NetConfig
import com.drake.net.interceptor.LogRecordInterceptor
import com.drake.net.okhttp.setConverter
import com.drake.net.okhttp.setDebug
import com.drake.net.time.Interval
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.mobileheros.camera.databinding.ActivityMainBinding
import com.mobileheros.camera.ui.dialog.UpgradeDialog
import com.mobileheros.camera.ui.dialog.UpgradeType
import com.mobileheros.camera.utils.Constants
import com.mobileheros.camera.utils.Global
import com.mobileheros.camera.utils.PlayBillingHelper
import com.mobileheros.camera.utils.SerializationConverter
import com.mobileheros.camera.utils.getData
import com.mobileheros.camera.utils.localConfig
import com.mobileheros.camera.utils.putData
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var showAd = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
//        getAppConfig()

        val navView: BottomNavigationView = binding.navView
        navView.itemIconTintList = null

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val list = listOf(
            R.id.navigation_setting,
            R.id.navigation_web,
            R.id.navigation_subscribe_single
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (list.contains(destination.id)) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }

        }
        navView.setupWithNavController(navController)

        NetConfig.initialize("https://api.replicate.com", this) {
            // 超时配置, 默认是10秒, 设置太长时间会导致用户等待过久
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            addInterceptor(LogRecordInterceptor(BuildConfig.DEBUG))
            setDebug(BuildConfig.DEBUG)
            setConverter(SerializationConverter())
        }
        Global.isVip = localConfig.getData("isVip", false)

//        Interval(5, TimeUnit.SECONDS).subscribe {
//            if (showAd && (application as MyApplication).isAdAvailable()) {
//                showAd = false
//                (application as MyApplication).showAdIfAvailable(this@MainActivity, object :
//                    MyApplication.OnShowAdCompleteListener{
//                    override fun onShowAdComplete() {
//
//                    }
//                })
//            }
//        }.start()
    }

    fun setSelectItem(id: Int) {
        binding.navView.selectedItemId = id
    }

    override fun onResume() {
        super.onResume()
        PlayBillingHelper.getInstance(application).queryPurchases(this)
    }


    private fun getAppConfig() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        if (BuildConfig.DEBUG) {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 60
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
        }
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val result = remoteConfig.getString("appConfig")
                    val temp = JSONObject(result)
                    val value = temp.optInt(Constants.AD_OPEN_APP)
                    Global.showOpenAd = value == 1
                    localConfig.putData(Constants.AD_OPEN_APP, value)
                }
            }
    }
}