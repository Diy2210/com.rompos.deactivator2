package net.compoza.deactivator.mpp.base

import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.HttpClient
import net.compoza.deactivator.mpp.getSqlDriver
import net.compoza.deactivator.mpp.repositories.ServersRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

class CoreApp(sqlDriver: SqlDriver) {
    var kodein = DI {
        bind() from singleton { ServersRepository(sqlDriver) }
        bind() from singleton { HttpClient() }
    }
}

var isInitialized = false
//    private set
lateinit var myApp: CoreApp
//    private set

fun initApplication(sqlDriver: SqlDriver? = null) {
    if (!isInitialized) {
        myApp =
            CoreApp(sqlDriver ?: getSqlDriver("servers.db"))
        isInitialized = true
    }
}
