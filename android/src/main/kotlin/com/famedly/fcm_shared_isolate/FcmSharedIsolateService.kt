package com.famedly.fcm_shared_isolate

import android.content.Context
import android.os.Handler
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor

abstract class FcmSharedIsolateService : FirebaseMessagingService() {
    abstract fun getEngine(): FlutterEngine

    private val handler = Handler()

    private fun getPlugin(): FcmSharedIsolatePlugin {
        val registry = getEngine().plugins
        var plugin = registry.get(FcmSharedIsolatePlugin::class.java) as? FcmSharedIsolatePlugin
        if (plugin == null) {
            plugin = FcmSharedIsolatePlugin()
            registry.add(plugin)
        }
        return plugin
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // Added for commenting purposes;
        // We don't handle the message here as we already handle it in the receiver and don't want to duplicate.
    }

    override fun onNewToken(token: String) {
        handler.post {
            getPlugin().getChannel().invokeMethod("token", token)
        }
    }
}
