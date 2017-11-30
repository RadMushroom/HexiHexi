package com.hexihexi.hexihexi.di

import android.content.Context
import com.hexihexi.hexihexi.HexiApp
import com.hexihexi.hexihexi.bluetooth.BluetoothServiceImpl
import com.hexihexi.hexihexi.bluetooth.HexiBluetoothService
import com.hexihexi.hexihexi.discovery.DiscoveryService
import com.hexihexi.hexihexi.discovery.DiscoveryServiceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by yurii on 10/16/17.
 */

@Module
class HexiModule {

    @Singleton
    @Provides
    internal fun provideContext(): Context = HexiApp.appContext()

    @Singleton
    @Provides
    internal fun provideDiscoveryService(context: Context): DiscoveryService = DiscoveryServiceImpl(context)

    @Singleton
    @Provides
    internal fun provideluetoothService(context: Context): HexiBluetoothService = BluetoothServiceImpl(context)
}
