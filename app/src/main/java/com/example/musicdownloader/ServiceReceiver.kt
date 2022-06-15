package com.example.musicdownloader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager


class ServiceReceiver : BroadcastReceiver() {
    var telephony: TelephonyManager? = null
    override fun onReceive(context: Context, intent: Intent?) {
        val phoneListener = MyPhoneStateListener()
        telephony = context
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        telephony!!.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    fun onDestroy() {
        telephony!!.listen(null, PhoneStateListener.LISTEN_NONE)
    }
}