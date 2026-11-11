package com.convx.music.playback

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LifecycleCoroutineScope
import com.convx.music.db.MusicDatabase
import com.convx.music.listentogether.ListenTogetherPlayerConnectionBridge
import com.convx.music.playback.MusicService.MusicBinder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import com.convx.music.di.ValueProvider

/** Owns the activity-bound connection between the UI and the playback service. */
class PlaybackServiceConnector(
    private val databaseProvider: ValueProvider<MusicDatabase>,
    private val listenTogetherConnectionBridge: ListenTogetherPlayerConnectionBridge,
) : ServiceConnection {
    private var isBound = false
    private var lifecycleScope: LifecycleCoroutineScope? = null
    private var onConnectionChanged: ((PlayerConnection?) -> Unit)? = null
    private var playerConnection: PlayerConnection? = null
    private var connectionJob: Job? = null

    fun bind(
        context: Context,
        lifecycleScope: LifecycleCoroutineScope,
        onConnectionChanged: (PlayerConnection?) -> Unit,
    ) {
        if (isBound) return
        this.lifecycleScope = lifecycleScope
        this.onConnectionChanged = onConnectionChanged
        isBound = context.bindService(
            Intent(context, MusicService::class.java),
            this,
            Context.BIND_AUTO_CREATE,
        )
    }

    fun unbind(context: Context) {
        connectionJob?.cancel()
        connectionJob = null
        playerConnection?.dispose()
        playerConnection = null
        onPlayerConnectionChanged(null)
        if (isBound) context.unbindService(this)
        isBound = false
        onConnectionChanged = null
        lifecycleScope = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (service !is MusicBinder) return

        createPlayerConnection(service)
    }

    private fun createPlayerConnection(service: MusicBinder) {
        val scope = lifecycleScope ?: return
        connectionJob?.cancel()
        playerConnection?.dispose()
        playerConnection = null
        connectionJob = scope.launch {
            try {
                service.service.isPlayerReady.first { it }
                if (!isBound) return@launch
                val connection = PlayerConnection(
                    context = service.service,
                    binder = service,
                    database = databaseProvider.get(),
                    scope = scope,
                )
                playerConnection = connection
                onPlayerConnectionChanged(connection)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Timber.tag(TAG).e(error, "Failed to create PlayerConnection")
            }
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        connectionJob?.cancel()
        connectionJob = null
        onPlayerConnectionChanged(null)
        playerConnection?.dispose()
        playerConnection = null
    }

    private fun onPlayerConnectionChanged(connection: PlayerConnection?) {
        listenTogetherConnectionBridge.update(connection)
        onConnectionChanged?.invoke(connection)
    }

    private companion object {
        const val TAG = "PlaybackServiceConnector"
    }
}
