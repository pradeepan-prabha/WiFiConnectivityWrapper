package com.android.wificonnectivitywrapper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.iotcm.WifiConnectivityWrapper


class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_LOCATION: Int = 200
    private var context: Context? = null
    private var wifiConnectivityWrapper: WifiConnectivityWrapper =
        WifiConnectivityWrapper(this, ::scanSuccessResult, ::scanFailureResult)

    private fun scanSuccessResult(result: List<ScanResult>) {
        Toast.makeText(this@MainActivity,
            "scanSuccess **************${result}",
            Toast.LENGTH_SHORT).show()
        println("scanSuccess **************= ${result}")
    }

    private fun scanFailureResult(result: List<ScanResult>) {
        Toast.makeText(
            this@MainActivity,
            "scanFailure ****************${result}",
            Toast.LENGTH_SHORT
        ).show()
        println("scanFailure *************= ${result}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        // get reference to button
        val startScanBtn = findViewById<Button>(R.id.startScan)
        // set on-click listener
        startScanBtn.setOnClickListener {
            // your code to perform when the user clicks on the button
//            Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            //Check in current device WIFI is turn ON/OFF
            if (wifiConnectivityWrapper.checkDeviceWifiStatus()) {
                wifiConnectivityWrapper.registerBroadcastWifiManager()
                wifiConnectivityWrapper.scanWifi()
            }
        }
        // get reference to button
        val stopScanBtn = findViewById<Button>(R.id.stopScan)
        // set on-click listener
        stopScanBtn.setOnClickListener {
            // your code to perform when the user clicks on the button
//            Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            wifiConnectivityWrapper.unRegisterBroadcastWifiManager()
        }

        //Check Location Permission and If not Request permission
        hasLocationPermissions()

        wifiConnectivityWrapper.createWifiInst()
        //Check in current device WIFI is turn ON/OFF
        if (wifiConnectivityWrapper.checkDeviceWifiStatus()) {
            wifiConnectivityWrapper.registerBroadcastWifiManager()
            wifiConnectivityWrapper.scanWifi()
        }

    }

    private fun hasLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            println("*******Permission is not granted")
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                println("*******Show an explanation to the user *asynchronously* -- don't block")

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
                println("*******RequestPermissions")
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    println("*******permission was granted, yay!")

                    wifiConnectivityWrapper.scanWifi()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    println("******* permission denied, boo!")

                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
