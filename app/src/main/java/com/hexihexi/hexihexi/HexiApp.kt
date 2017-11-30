package com.hexihexi.hexihexi

import android.app.Application
import android.content.Context

import com.hexihexi.hexihexi.di.DaggerHexiComponent
import com.hexihexi.hexihexi.di.HexiComponent
import com.hexihexi.hexihexi.di.HexiModule

/**
 * Created by yurii on 10/16/17.
 */

class HexiApp : Application() {

    companion object {

        private lateinit var innerContext: Context
        private lateinit var hexiComponent: HexiComponent

        fun appContext(): Context = innerContext
        fun hexiComponent(): HexiComponent = hexiComponent
    }

    override fun onCreate() {
        super.onCreate()
        innerContext = this
        hexiComponent = DaggerHexiComponent.builder()
                .hexiModule(HexiModule())
                .build()
    }

}
