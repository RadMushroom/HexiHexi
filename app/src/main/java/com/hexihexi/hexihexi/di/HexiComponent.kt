package com.hexihexi.hexihexi.di

import com.hexihexi.hexihexi.activities.*
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
    fun inject(loginActivity: LoginActivity)
    fun inject(registerActivity: RegisterActivity)
    fun inject(restorePassActivity: RestorePassActivity)
    fun inject(usersListActivity: UsersListActivity)
}
