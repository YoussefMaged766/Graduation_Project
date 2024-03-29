package com.example.graduationproject.utils

object NetworkState {

    @Volatile
    private var isOnline: Boolean = false

    fun setNetworkState(isOnline: Boolean) {
        synchronized(this) {
            NetworkState.isOnline = isOnline
        }
    }

    fun isOnline(): Boolean {
        synchronized(this) {
            return isOnline
        }
    }
}