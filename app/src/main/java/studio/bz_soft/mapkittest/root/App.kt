package studio.bz_soft.mapkittest.root

import android.app.Application
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import studio.bz_soft.mapkittest.di.*

@ExperimentalCoroutinesApi
class App : Application() {

    private lateinit var instance: App

    override fun onCreate() {
        super.onCreate()
//        AndroidThreeTen.init(this)
        instance = this
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(
                listOf(
                    applicationModule,
                    apiModule,
                    networkModule,
                    storageModule,
                    dataBaseModule,
                    repositoryModule,
                    presenterModule,
                    controllerModule,
                    soundModule,
                    navigationModule,
                    viewModelModule
                )
            )
        }
    }
}