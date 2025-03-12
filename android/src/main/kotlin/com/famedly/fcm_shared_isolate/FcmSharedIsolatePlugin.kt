package com.famedly.fcm_shared_isolate

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull
import com.google.firebase.messaging.FirebaseMessaging
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


class FcmSharedIsolatePlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private val CHANNEL = "fcm_shared_isolate"
    private lateinit var channel: MethodChannel
    private lateinit var activity: Activity

    private val fcm = try { FirebaseMessaging.getInstance() } catch (e: Exception) { null }

    fun getChannel(): MethodChannel {
        return channel
    }

    fun getActivity(): Activity {
        return activity
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(binding.binaryMessenger, CHANNEL)
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (fcm == null) {
            result.error("fcm_unavailable", null, null)
            return
        }

        if (call.method == "getToken") {
            val getToken = fcm.getToken()
            getToken.addOnSuccessListener { result.success(it) }
            getToken.addOnFailureListener { result.error("unknown", null, null) }
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

//    fun message(@NonNull data: Map<String, String>) {
//        channel.invokeMethod("message", data)
//    }
//
//    fun token(@NonNull str: String) {
//        channel.invokeMethod("token", str)
//    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {}

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {}
}
