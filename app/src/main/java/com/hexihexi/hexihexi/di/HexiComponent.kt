package com.hexihexi.hexihexi.di

import com.hexihexi.hexihexi.ChartActivity
import com.hexihexi.hexihexi.DeviceDetailsActivity
import com.hexihexi.hexihexi.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created by yurii on 10/16/17.
 */

@Singleton
@Component(modules = arrayOf(HexiModule::class))
interface HexiComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(mainActivity: DeviceDetailsActivity)
    fun inject(chartActivity: ChartActivity)
}
