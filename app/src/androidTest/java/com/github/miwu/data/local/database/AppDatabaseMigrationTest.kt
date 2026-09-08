package com.github.miwu.data.local.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
    )

    @Test
    fun migrate2To3KeepsFavoriteAndDropsToken() {
        helper.createDatabase(DB_NAME, 2).apply {
            execSQL(
                """
                INSERT INTO favorite_device (
                    bssid, comFlag, did, freqFlag, isOnline, latitude, longitude, mac,
                    model, name, orderTime, permitLevel, pid, token, uid
                ) VALUES ('', 0, 'did-1', 0, 1, '', '', '', 'model', 'name', 7, 0, 0, 'secret', 42)
                """.trimIndent()
            )
            execSQL(
                "INSERT INTO favorite_device_metadata(uid, did, sort_index) VALUES (42, 'did-1', 3)"
            )
            close()
        }

        helper.runMigrationsAndValidate(DB_NAME, 3, true, AppDatabase.MIGRATION_2_3).use { db ->
            db.query("SELECT did, orderTime FROM favorite_device WHERE uid = 42").use { cursor ->
                cursor.moveToFirst()
                assertEquals("did-1", cursor.getString(0))
                assertEquals(7, cursor.getInt(1))
            }
            db.query("PRAGMA table_info(favorite_device)").use { cursor ->
                val columns = buildSet {
                    while (cursor.moveToNext()) add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
                }
                assertFalse("token" in columns)
            }
        }
    }

    private companion object {
        const val DB_NAME = "migration-test"
    }
}
