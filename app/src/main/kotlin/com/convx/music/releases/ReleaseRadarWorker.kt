/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.releases

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.convx.music.constants.ReleaseRadarEnabledKey
import com.convx.music.playback.ReleaseRadarSync
import com.convx.music.playback.ReleaseStore
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import kotlinx.coroutines.CancellationException
import org.koin.core.context.GlobalContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ReleaseRadarWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if (!applicationContext.dataStore.get(ReleaseRadarEnabledKey, true)) return Result.success()
        val koin = GlobalContext.get()
        val store = koin.get<ReleaseStore>()
        if (!store.read().isDue(System.currentTimeMillis())) return Result.success()
        return try {
            val found = koin.get<ReleaseRadarSync>().run(System.currentTimeMillis())
            Timber.tag(TAG).i("looked for new releases: $found found")
            Result.success()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Timber.tag(TAG).w(error, "release look failed")
            if (runAttemptCount < MAX_ATTEMPTS) Result.retry() else Result.success()
        }
    }

    companion object {
        private const val TAG = "ReleaseRadar"
        private const val WORK_NAME = "release_radar"
        private const val MAX_ATTEMPTS = 3

        /** Every three days on any network; an artist page is small. */
        fun schedule(context: Context) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<ReleaseRadarWorker>(3, TimeUnit.DAYS)
                    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                    .build(),
            )
        }
    }
}
