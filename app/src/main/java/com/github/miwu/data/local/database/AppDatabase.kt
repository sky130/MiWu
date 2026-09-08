package com.github.miwu.data.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.miwu.data.local.database.dao.CrashDao
import com.github.miwu.data.local.database.dao.FavoriteDeviceDao
import com.github.miwu.data.local.database.entity.CrashEntity
import com.github.miwu.data.local.database.entity.FavoriteDeviceEntity
import com.github.miwu.data.local.database.entity.FavoriteDeviceOrderEntity

@Database(
    version = 3,
    entities = [
        FavoriteDeviceEntity::class,
        FavoriteDeviceOrderEntity::class,
        CrashEntity::class,
    ],
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDeviceDao(): FavoriteDeviceDao
    abstract fun crashDao(): CrashDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS favorite_device_new (
                        bssid TEXT NOT NULL, cnt INTEGER, comFlag INTEGER NOT NULL,
                        did TEXT NOT NULL, freqFlag INTEGER NOT NULL, hideMode INTEGER,
                        isOnline INTEGER NOT NULL, lastOnline INTEGER, latitude TEXT NOT NULL,
                        localIp TEXT, longitude TEXT NOT NULL, mac TEXT NOT NULL,
                        model TEXT NOT NULL, name TEXT NOT NULL, orderTime INTEGER NOT NULL,
                        parentId TEXT, permitLevel INTEGER NOT NULL, pid INTEGER NOT NULL,
                        rssi INTEGER, showMode INTEGER, specType TEXT, ssid TEXT,
                        uid INTEGER NOT NULL, fwVersion TEXT, isSetPinCode INTEGER,
                        isSubGroup INTEGER, mcuVersion TEXT, pinCodeType INTEGER,
                        platform TEXT, showGroupMember INTEGER,
                        PRIMARY KEY(uid, did)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO favorite_device_new (
                        bssid, cnt, comFlag, did, freqFlag, hideMode, isOnline, lastOnline,
                        latitude, localIp, longitude, mac, model, name, orderTime, parentId,
                        permitLevel, pid, rssi, showMode, specType, ssid, uid, fwVersion,
                        isSetPinCode, isSubGroup, mcuVersion, pinCodeType, platform, showGroupMember
                    ) SELECT
                        bssid, cnt, comFlag, did, freqFlag, hideMode, isOnline, lastOnline,
                        latitude, localIp, longitude, mac, model, name, orderTime, parentId,
                        permitLevel, pid, rssi, showMode, specType, ssid, uid, fwVersion,
                        isSetPinCode, isSubGroup, mcuVersion, pinCodeType, platform, showGroupMember
                    FROM favorite_device
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE favorite_device")
                db.execSQL("ALTER TABLE favorite_device_new RENAME TO favorite_device")
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_favorite_device_uid_did " +
                        "ON favorite_device(uid, did)"
                )
            }
        }
    }
}
