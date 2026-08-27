package com.aviad.chordstv

import android.app.Application
import com.aviad.chordstv.di.AppContainer

/**
 * Application class that owns the manual dependency container.
 * (Manual DI keeps the build simple – no kapt/ksp/Hilt required.)
 */
class ChordsTvApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
