package com.example.apptest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log

class CodeScannerBroadcastReceiver(private val onCodeRead: (code: String) -> Unit) :
    BroadcastReceiver() {

    companion object {
        // SUNMI constants
        const val SUNMI_BRAND = "SUNMI"
        const val SUNMI_ACTION_CODE_RECEIVED = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED"
        const val SUNMI_SCAN_EXTRA_DATA_STRING = "data"

        // Zebra constants
        const val ZEBRA_BRAND = "Zebra"
        const val DATAWEDGE_ACTION_CODE_RECEIVED = "com.example.apptest.SCAN"
        const val DATAWEDGE_SEND_ACTION = "com.symbol.datawedge.api.ACTION"
        const val DATAWEDGE_RETURN_ACTION = "com.symbol.datawedge.api.RESULT_ACTION"
        const val DATAWEDGE_SCAN_EXTRA_DATA_STRING = "com.symbol.datawedge.data_string"
        const val DATAWEDGE_SCAN_EXTRA_LABEL_TYPE = "com.symbol.datawedge.label_type"
        const val DATAWEDGE_SEND_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE"
        const val DATAWEDGE_SEND_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG"
        const val DATAWEDGE_SEND_GET_VERSION = "com.symbol.datawedge.api.GET_VERSION_INFO"
        const val DATAWEDGE_RETURN_VERSION = "com.symbol.datawedge.api.RESULT_GET_VERSION_INFO"
        const val DATAWEDGE_RETURN_VERSION_DATAWEDGE = "DATAWEDGE"
        const val DATAWEDGE_PROFILE_NAME = "Pelican DH"
        const val DATAWEDGE_PROFILE_INTENT_START_ACTIVITY = "0"
        const val DATAWEDGE_PROFILE_INTENT_START_SERVICE = "1"
        const val DATAWEDGE_PROFILE_INTENT_BROADCAST = "2"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (isZebra()) {
            validateDWVersionAndCreateProfile(intent, context)
        }

        val scanData = when {
            isSUNMI() -> intent.getStringExtra(SUNMI_SCAN_EXTRA_DATA_STRING)
            isZebra() -> intent.getStringExtra(DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            else -> null
        }

        if (scanData != null && scanData.isNotEmpty()) {
            onCodeRead.invoke(scanData)
        }
    }


    fun registerReceiver(context: Context) {
        val intentFilter = IntentFilter()
        if (isSUNMI()) {
            //  Register broadcast receiver to listen for responses from SUNMI scanning system
            intentFilter.addAction(SUNMI_ACTION_CODE_RECEIVED)
            context.registerReceiver(this, intentFilter)
        } else if (isZebra()) {
            //  Register broadcast receiver to listen for responses from Zebra DW API
            intentFilter.addAction(DATAWEDGE_RETURN_ACTION)
            intentFilter.addAction(DATAWEDGE_ACTION_CODE_RECEIVED)
            context.registerReceiver(this, intentFilter)
            // Send get version info event once broadcast is registered
            sendCommandString(context, DATAWEDGE_SEND_GET_VERSION, "")
        }
    }

    fun unregister(context: Context) {
        try {
            context.unregisterReceiver(this)
        } catch (e: Exception) {
            Log.e(
                this.javaClass.name,
                "Error trying to unregistering code scanner broadcast receiver"
            )
        }
    }

    private fun isSUNMI() = Build.BRAND.equals(SUNMI_BRAND, true)

    private fun isZebra() = !Build.BRAND.equals(ZEBRA_BRAND, true)


    // region Data Wedge Zebra


    private fun validateDWVersionAndCreateProfile(intent: Intent, context: Context) {
        if (intent.hasExtra(DATAWEDGE_RETURN_VERSION)) {
            val version = intent.getBundleExtra(DATAWEDGE_RETURN_VERSION)
            val dataWedgeVersion = version.getString(DATAWEDGE_RETURN_VERSION_DATAWEDGE)
            if (dataWedgeVersion != null && dataWedgeVersion >= "6.5") {
                createDataWedgeProfile(context)
            }
        }
    }

    private fun createDataWedgeProfile(context: Context) {
        //  Create and configure the DataWedge profile associated with this application
        sendCommandString(context, DATAWEDGE_SEND_CREATE_PROFILE, DATAWEDGE_PROFILE_NAME)

        // Set configurations
        val profileConfig = Bundle()
        profileConfig.putString("PROFILE_NAME", DATAWEDGE_PROFILE_NAME)
        profileConfig.putString("PROFILE_ENABLED", "true") //  These are all strings
        profileConfig.putString("CONFIG_MODE", "UPDATE")

        val barcodeConfig = Bundle()
        barcodeConfig.putBundle("PARAM_LIST", Bundle())
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE")
//        barcodeConfig.putString(
//            "RESET_CONFIG",
//            "true"
//        )
//        //  This is the default but never hurts to specify

        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig)

        val appConfig = Bundle()
        //  Associate the profile with this app
        appConfig.putString("PACKAGE_NAME", context.packageName)
        appConfig.putStringArray("ACTIVITY_LIST", arrayOf("*"))
        profileConfig.putParcelableArray("APP_LIST", arrayOf(appConfig))
        sendCommandBundle(context, DATAWEDGE_SEND_SET_CONFIG, profileConfig)

        //  You can only configure one plugin at a time in some versions of DW, now do the intent output
        profileConfig.remove("PLUGIN_CONFIG")
        val intentConfig = Bundle()
        intentConfig.putString("PLUGIN_NAME", "INTENT")
        intentConfig.putString("RESET_CONFIG", "true")
        val intentProps = Bundle()
        intentProps.putString("intent_output_enabled", "true")
        intentProps.putString("intent_action", DATAWEDGE_ACTION_CODE_RECEIVED)

        intentProps.putString("intent_delivery", DATAWEDGE_PROFILE_INTENT_BROADCAST)
        intentConfig.putBundle("PARAM_LIST", intentProps)
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig)
        sendCommandBundle(context, DATAWEDGE_SEND_SET_CONFIG, profileConfig)
    }


    private fun sendCommandString(context: Context, command: String, parameter: String) {
        val dwIntent = Intent()
        dwIntent.action = DATAWEDGE_SEND_ACTION
        dwIntent.putExtra(command, parameter)
        context.sendBroadcast(dwIntent)
    }

    private fun sendCommandBundle(context: Context, command: String, parameter: Bundle) {
        val dwIntent = Intent()
        dwIntent.action = DATAWEDGE_SEND_ACTION
        dwIntent.putExtra(command, parameter)
        context.sendBroadcast(dwIntent)
    }

    //endregion


}
