package com.example.musicdownloader

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log


class MyPhoneStateListener : PhoneStateListener() {
    override fun onCallStateChanged(state: Int, incomingNumber: String) {
        when (state) {
            TelephonyManager.CALL_STATE_IDLE -> {
                Log.d("DEBUG", "IDLE")
                phoneRinging = false
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                Log.d("DEBUG", "OFFHOOK")
                phoneRinging = false
            }
            TelephonyManager.CALL_STATE_RINGING -> {
                Log.d("DEBUG", "RINGING")
                phoneRinging = true
            }
        }
    }

    companion object {
        var phoneRinging = false
    }
}