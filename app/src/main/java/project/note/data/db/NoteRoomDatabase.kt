package project.note.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import project.note.data.NoteDto

@Database(entities = [NoteDto::class], version = 3, exportSchema = false)
abstract class NoteRoomDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteRoomDatabase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create the new table
                db.execSQL(
                    "CREATE TABLE note_table_new (title TEXT NOT NULL, content TEXT NOT NULL, id INTEGER NULL, PRIMARY KEY(id))")

                // Copy the data
                db.execSQL(
                    "INSERT INTO note_table_new (title, content, id) SELECT title, content, id FROM note_table")

                // Remove the old table
                db.execSQL("DROP TABLE note_table")

                // Change the table name to the correct one
                db.execSQL("ALTER TABLE note_table_new RENAME TO note_table");
            }
        }

        fun getDatabase(context: Context): NoteRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteRoomDatabase::class.java,
                    "note_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}