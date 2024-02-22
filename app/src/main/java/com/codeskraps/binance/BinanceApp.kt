package com.codeskraps.binance

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.codeskraps.core.domain.pnl.PnLTaskReceiver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class BinanceApp : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        PnLTaskReceiver.setPnLAlarm(this)
        binanceApp = this
    }

    companion object {
        private var binanceApp: BinanceApp? = null

        fun getBinanceApp(): BinanceApp {
            return binanceApp!!
        }
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build();
}