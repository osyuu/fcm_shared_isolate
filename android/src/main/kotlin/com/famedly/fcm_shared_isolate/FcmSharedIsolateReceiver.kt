package com.famedly.fcm_shared_isolate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel


abstract class FcmSharedIsolateReceiver : BroadcastReceiver() {

    private val TAG = "FLTFireMsgReceiver"
    var notifications = HashMap<String?, RemoteMessage>()

    private val handler = Handler()

    abstract fun getEngine(context: Context): FlutterEngine

    private fun getPlugin(context: Context): FcmSharedIsolatePlugin {
        Log.d(TAG, "getPlugin")
        val registry = getEngine(context).plugins
        var plugin = registry.get(FcmSharedIsolatePlugin::class.java) as? FcmSharedIsolatePlugin
        if (plugin == null) {
            plugin = FcmSharedIsolatePlugin()
            registry.add(plugin)
        }
        return plugin
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "broadcast received for message")
        if (ContextHolder.getApplicationContext() == null) {
            ContextHolder.setApplicationContext(context.applicationContext)
        }

        if (intent.extras == null) {
            Log.d(
                TAG,
                "broadcast received but intent contained no extras to process RemoteMessage. Operation cancelled."
            )
            return
        }
        val remoteMessage = RemoteMessage(intent.extras)

        // Store the RemoteMessage if the message contains a notification payload.
        if (remoteMessage.notification != null) {
            notifications[remoteMessage.messageId] = remoteMessage
        }

        handler.post {
            getPlugin(context).getChannel().invokeMethod("message", remoteMessage.data)
        }
    }
}