package com.mobileheros.camera.ui.about

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.mobileheros.camera.BuildConfig
import com.mobileheros.camera.R
import com.mobileheros.camera.databinding.FragmentSettingBinding
import com.mobileheros.camera.ui.dialog.RateDialog
import com.mobileheros.camera.ui.dialog.UpgradeDialog
import com.mobileheros.camera.ui.dialog.UpgradeType
import org.json.JSONObject

class SettingFragment: Fragment() , OnClickListener{
    private lateinit var binding : FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        binding.back.setOnClickListener(this)
        binding.rateLayout.setOnClickListener(this)
        binding.privacyLayout.setOnClickListener(this)
        binding.versionLayout.setOnClickListener(this)
        getLocalVersion()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    fun getLocalVersion() {
        context?.let {
            val name = it.packageManager.getPackageInfo(it.packageName, 0).versionName
            binding.version.text = getString(R.string.version) + "  " + name
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> findNavController().navigateUp()
            R.id.rate_layout -> context?.let { RateDialog(it).show() }
            R.id.privacy_layout -> context?.let { findNavController().navigate(R.id.navigation_web) }
            R.id.version_layout -> checkVersion()
        }
    }

    private fun checkVersion() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        if (BuildConfig.DEBUG) {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 60
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
        }
        if (this.host != null) {
            context?.let {
                remoteConfig.fetchAndActivate()
                    .addOnCompleteListener(this.requireActivity()) { task ->
                        if (task.isSuccessful) {
                            val result = remoteConfig.getString("androidVersion")
                            val temp = JSONObject(result)
                            val latestVersion = temp.optInt("latestVersion")
                            val forceUpgradeVersion = temp.optInt("forceUpgradeVersion")
                            val curVersion =
                                it.packageManager.getPackageInfo(it.packageName, 0).versionCode
                            if (curVersion < forceUpgradeVersion) {
                                UpgradeDialog(it, UpgradeType.TYPE_FORCE_VERSION).show()
                            } else if (curVersion < latestVersion) {
                                UpgradeDialog(it, UpgradeType.TYPE_NEW_VERSION).show()
                            } else {
                                UpgradeDialog(it, UpgradeType.TYPE_IS_LATEST).show()
                            }
                        } else {
                            UpgradeDialog(it, UpgradeType.TYPE_IS_LATEST).show()
                        }
                    }
            }

        }
    }
}