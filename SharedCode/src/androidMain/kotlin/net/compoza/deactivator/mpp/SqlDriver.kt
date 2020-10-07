package net.compoza.deactivator.mpp

import com.squareup.sqldelight.db.SqlDriver

actual fun getSqlDriver(databaseName: String): SqlDriver {
    throw UninitializedPropertyAccessException("Call from Android module")
}
