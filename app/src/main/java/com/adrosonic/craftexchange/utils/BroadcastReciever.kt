package com.adrosonic.craftexchange.utils

/**
 * Created by 'Rital Naik on 09/02/18.
 */
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * Generic registerReceiver extension to reduce boilerplate
 *
 * Call this like so:
 * val myReceiver = registerReceiver(IntentFilter(BROADCAST_SOMETHING_HAPPENED)) {
 *     when (intent?.action) {
 *         BROADCAST_SOMETHING_HAPPENED -> handleSomethingHappened()
 *     }
 * }
 *
 * Call this extension from your Activity's onStart(), keep a reference
 * to the returned receiver and unregister it in onStop()
 *
 * Note: If you support devices on Honeycomb or earlier,
 * then you must call this in onResume() and unregister in onPause()
 */
fun Context.registerReceiver(
  intentFilter: IntentFilter,
  onReceive: (intent: Intent?) -> Unit): BroadcastReceiver {
  val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
      onReceive(intent)
    }
  }
  this.registerReceiver(receiver, intentFilter)
  return receiver
}

fun Context.registerLocalReceiver(intentFilter: IntentFilter, onReceive: (intent: Intent?) -> Unit): BroadcastReceiver {
  val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
      Handler(Looper.getMainLooper()).run { onReceive(intent) }
    }
  }
  LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
  return receiver
}

fun Context.unregisterLocalReceiver(receiver: BroadcastReceiver) {
  LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
}

/**
 * Register a BroadcastReceiver which is guaranteed to
 * receive change events when the network connects and disconnects
 *
 * Call this extension from your Activity's onStart(), keep a reference
 * to the returned receiver and unregister it in onStop()
 *
 * Note: If you support devices on Honeycomb or earlier,
 * then you must call this in onResume() and unregister in onPause()
 */
fun Context.registerNetworkChangeReceiver(
  onNetworkConnected: () -> Unit,
  onNetworkDisconnected: () -> Unit
): BroadcastReceiver {
  val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
      if (intent == null || intent.extras == null) {
        return
      }

      with(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) {
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting) {
//          (context.applicationContext as? App)?.coordinator?.performLocallyAvailableActions()
          onNetworkConnected()
        } else {
          onNetworkDisconnected()
        }
      }
    }
  }
  this.registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
  return receiver
}