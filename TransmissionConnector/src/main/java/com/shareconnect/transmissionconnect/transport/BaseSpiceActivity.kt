package com.shareconnect.transmissionconnect.transport

import android.os.Bundle
import com.shareconnect.transmissionconnect.BaseActivity
import com.shareconnect.transmissionconnect.TransmissionRemote
import com.shareconnect.transmissionconnect.TransmissionRemote.OnActiveServerChangedListener
import com.shareconnect.transmissionconnect.server.Server
import com.shareconnect.transmissionconnect.transport.okhttp.OkHttpTransportManager

abstract class BaseSpiceActivity : BaseActivity() {

    private var transportManager: TransportManager? = null
    private val activeServerListener = OnActiveServerChangedListener { newServer: Server? ->
        transportManager = updatedTransportManager(newServer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = application as TransmissionRemote
        app.addOnActiveServerChangedListener(activeServerListener)
        transportManager = updatedTransportManager(app.getActiveServer())
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        (transportManager as? SpiceTransportManager)?.start(this)
        super.onStart()
    }

    override fun onStop() {
        (transportManager as? SpiceTransportManager)?.apply {
            if (isStarted) {
                cancelAllRequests()
                shouldStop()
            }
        }
        super.onStop()
    }

    override fun onDestroy() {
        val app = application as TransmissionRemote
        app.removeOnActiveServerChangedListener(activeServerListener)
        super.onDestroy()
    }

    fun getTransportManager(): TransportManager {
        return checkNotNull(transportManager) { "Transport manager is not initialized yet" }
    }

    private fun updatedTransportManager(server: Server?): TransportManager? {
        val app = TransmissionRemote.instance
        return if (app.featureManager.useOkHttp()) {
            server?.let { OkHttpTransportManager(server) }
        } else {
            ((transportManager as? SpiceTransportManager) ?: SpiceTransportManager()).apply {
                setServer(server)
            }
        }
    }
}
