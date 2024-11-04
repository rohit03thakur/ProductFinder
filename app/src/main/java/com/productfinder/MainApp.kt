package com.productfinder

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApp : Application() {
    companion object {
        lateinit var mainApp: MainApp
    }


    override fun onCreate() {
        super.onCreate()
        mainApp = this

    }

}


