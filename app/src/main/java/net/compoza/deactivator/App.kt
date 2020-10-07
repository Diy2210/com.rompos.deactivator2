package net.compoza.deactivator

import android.app.Application
import com.squareup.sqldelight.android.AndroidSqliteDriver
import net.compoza.deactivator.databes.Servers
import net.compoza.deactivator.mpp.base.initApplication

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initApplication(
            AndroidSqliteDriver(
                Servers.Schema,
                applicationContext,
                "servers.db"
            )
        )
    }
}
