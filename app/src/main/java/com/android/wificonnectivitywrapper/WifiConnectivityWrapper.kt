package com.android.iotcm

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.provider.Settings
import android.widget.Toast


public class WifiConnectivityWrapper constructor(private val wrapperContext: Activity,
                                                 val scanSuccessResult: ( result:List<ScanResult>) -> Unit,
                                                 val scanFailureResult: (result:List<ScanResult>)  -> Unit) {
    private lateinit var wifiScanReceiver: BroadcastReceiver
    private lateinit var wifiManager: WifiManager
    private val results: List<ScanResult>? = null
    private val arrayList: ArrayList<String> = ArrayList()

    fun createWifiInst() {
        wifiManager =wrapperContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        registerBroadcastWifiManagerInstance()
    }

    private fun registerBroadcastWifiManagerInstance(): BroadcastReceiver {
        wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success) {
                    scanSuccess()
                } else {
                    scanFailure()
                }
            }
        }
        return wifiScanReceiver
    }

     fun registerBroadcastWifiManager() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        wrapperContext.registerReceiver(wifiScanReceiver, intentFilter)
    }

     fun unRegisterBroadcastWifiManager() {
        println("******************UnRegisterBroadcastWifiManager**************")
         wrapperContext?.unregisterReceiver(wifiScanReceiver)
    }



    fun checkDeviceWifiStatus(): Boolean {
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(
                wrapperContext,
                "WiFi is disabled ... We need to enable it",
                Toast.LENGTH_LONG
            ).show()
//            wifiManager.isWifiEnabled = true
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
//                wrapperContext.startActivity(panelIntent)
//            }
            wrapperContext.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        } else {
            return true
        }
        return false
    }

     fun scanWifi() {
        val success = wifiManager.startScan()
        if (!success) {
//             scan failure handling
            scanFailure()
        }
        Toast.makeText(wrapperContext, "Scanning WiFi ...", Toast.LENGTH_SHORT).show()
    }

    private fun scanSuccess() {
//        unRegisterBroadcastWifiManager()

        val results = wifiManager.scanResults
//        println("scanSuccess **************= ${results}")
        scanSuccessResult(results)
        for (scanResult in results) {
            arrayList.add(scanResult.SSID + " - " + scanResult.capabilities)
        }
    }

    private fun scanFailure() {
//        unRegisterBroadcastWifiManager()

        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager.scanResults
        scanFailureResult(results)
//        println("scanFailure *************= ${results}")
    }
}