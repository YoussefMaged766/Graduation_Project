package com.example.graduationproject

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.graduationproject.utils.networkCallback
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application: Application() {

    companion object {
        lateinit var instance: Application
            private set
    }
    private lateinit var connectivityManager: ConnectivityManager
    override fun onCreate() {
        super.onCreate()
        instance = this
        initNetworkConnectivity()
        // Enable Logger in logcat
//        if (BuildConfig.DEBUG) {
//            Logger.isEnabled = true
//        }

    }

    private fun initNetworkConnectivity() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager =
            applicationContext.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onTerminate() {
        super.onTerminate()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

}