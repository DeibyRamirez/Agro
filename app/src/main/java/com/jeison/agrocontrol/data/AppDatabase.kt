package com.jeison.agrocontrol.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Animal::class,
        SyncOutbox::class,
        SyncMetadata::class,
        PotreroLocal::class,
        PendingTaskLocal::class,
        ProductionLocal::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun animalDao(): AnimalDao
    abstract fun syncDao(): SyncDao
    abstract fun localCacheDao(): LocalCacheDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE animales ADD COLUMN serverId TEXT")
                db.execSQL("ALTER TABLE animales ADD COLUMN syncState TEXT NOT NULL DEFAULT 'SYNCED'")
                db.execSQL("ALTER TABLE animales ADD COLUMN updatedAtEpoch INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS sync_outbox (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        entity TEXT NOT NULL,
                        operation TEXT NOT NULL,
                        localId TEXT NOT NULL,
                        serverId TEXT,
                        payloadJson TEXT NOT NULL,
                        createdAtEpoch INTEGER NOT NULL,
                        sent INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS sync_metadata (
                        key TEXT NOT NULL PRIMARY KEY,
                        value TEXT NOT NULL
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS potreros (
                        serverId TEXT NOT NULL PRIMARY KEY,
                        nombre TEXT NOT NULL,
                        areaHectareas REAL,
                        estado TEXT NOT NULL,
                        especiesJson TEXT NOT NULL,
                        updatedAtEpoch INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS pending_tasks (
                        serverId TEXT NOT NULL PRIMARY KEY,
                        titulo TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        status TEXT NOT NULL,
                        vencimiento TEXT NOT NULL,
                        referencia TEXT NOT NULL,
                        updatedAtEpoch INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS production_records (
                        serverId TEXT NOT NULL PRIMARY KEY,
                        productType TEXT NOT NULL,
                        cantidad REAL NOT NULL,
                        unidad TEXT NOT NULL,
                        fecha TEXT NOT NULL,
                        notas TEXT NOT NULL,
                        updatedAtEpoch INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
            }
        }

        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agrocontrol.db",
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
