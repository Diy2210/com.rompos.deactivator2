package net.compoza.deactivator.mpp

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import net.compoza.deactivator.databes.Servers

actual fun getSqlDriver(databaseName: String): SqlDriver = NativeSqliteDriver(Servers.Schema, databaseName)
