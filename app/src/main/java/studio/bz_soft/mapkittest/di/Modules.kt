package studio.bz_soft.mapkittest.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import studio.bz_soft.mapkittest.BuildConfig
import studio.bz_soft.mapkittest.data.db.RoomDB
import studio.bz_soft.mapkittest.data.db.dao.LocationDao
import studio.bz_soft.mapkittest.data.http.ApiInterface
import studio.bz_soft.mapkittest.data.repository.DataBase
import studio.bz_soft.mapkittest.data.repository.DataBaseRepository
import studio.bz_soft.mapkittest.data.repository.Network
import studio.bz_soft.mapkittest.data.repository.NetworkRepository
import studio.bz_soft.mapkittest.root.App
import studio.bz_soft.mapkittest.root.Constants.GEO_CODING_BASE_URL
import studio.bz_soft.mapkittest.root.Constants.TEST_API_URL
import studio.bz_soft.mapkittest.ui.MapKitVM
import java.io.File
import java.util.*

@ExperimentalCoroutinesApi
val applicationModule = module {
    single { androidApplication() as App }
}

val apiModule = module {
    fun provideGeoCodingApi(retrofit: Retrofit): ApiInterface =
        retrofit.create(ApiInterface::class.java)

    single { provideGeoCodingApi(get()) }
}

val networkModule = module {
    val logger = run {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply {
            interceptor.level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }

    val logging = HttpLoggingInterceptor()
    logging.level = (HttpLoggingInterceptor.Level.BODY)

    val connectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .tlsVersions(TlsVersion.TLS_1_2)
        .cipherSuites(
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
        )
        .build()

    fun httpClient(context: Context): OkHttpClient {
        val cacheSize = (10 * 1024 * 1024).toLong()
        val httpCacheDirectory = File(context.cacheDir, "offlineCache")
        val httpCache = Cache(httpCacheDirectory, cacheSize)

        return OkHttpClient.Builder()
            .cache(httpCache)
            .connectionSpecs(Collections.singletonList(connectionSpec))
            .addInterceptor(logging)
            .build()
    }

    fun provideRetrofit(httpClient: OkHttpClient, url: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

    single { httpClient(androidContext()) }
    single { provideRetrofit(get(), TEST_API_URL) }
}

val storageModule = module {
    factory<SharedPreferences> { androidContext().getSharedPreferences("local_storage", Context.MODE_PRIVATE) }
}

val dataBaseModule = module {
    fun provideDB(application: Application): RoomDB = RoomDB.getDataBase(application)
    fun provideLocationDao(db: RoomDB): LocationDao = db.locationDao()

    single { provideDB(androidApplication()) }
    single { provideLocationDao(get()) }
}

val repositoryModule = module {
    fun provideDataBaseRepository(locationDao: LocationDao): DataBase =
        DataBaseRepository(locationDao)

    fun provideNetworkRepository(api: ApiInterface): Network =
        NetworkRepository(api)

    single { provideDataBaseRepository(get()) }
    single { provideNetworkRepository(get()) }
}

val presenterModule = module {  }

val controllerModule = module {  }

val soundModule = module {  }

val navigationModule = module {  }

@ExperimentalCoroutinesApi
val viewModelModule = module {
    viewModel { MapKitVM(dbRepository = get(), networkRepository = get()) }
}