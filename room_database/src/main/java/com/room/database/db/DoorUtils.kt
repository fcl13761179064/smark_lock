package com.room.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.room.database.bean.Door
import com.room.database.dao.DoorDao
import com.room.database.repository.DoorRepository


/**
 * @author: finaly
 * @date: 2023/7/15 21:47
 * @desc:
 */

@Database(entities = [Door::class], version = 1)
abstract class AlsoDataBase : RoomDatabase() {
    abstract fun alsoAddSongDao(): DoorDao
}

class DoorUtils private constructor(context: Context) {
    private val database: AlsoDataBase by lazy {
        val build = Room.databaseBuilder(
            context, AlsoDataBase::class.java, "add-door"
        ).fallbackToDestructiveMigration()
        build.build()
    }


    private val songRepository: DoorRepository by lazy {
        DoorRepository(database.alsoAddSongDao())
    }


    companion object {
        @Volatile
        private var instance: DoorUtils? = null

        fun getInstance(context: Context): DoorUtils {
            return instance ?: synchronized(this) {
                instance ?: DoorUtils(context).also { instance = it }
            }
        }
    }


    fun getAllDoor(): MutableList<Door> {
        return songRepository.getAllSongs()
    }


    fun isExitSong(timeId: String): Boolean {
        return songRepository.getSongById(timeId) != null
    }

    fun insertAllData(door: Door) {
        if (!isExitSong(door.time)) {
            songRepository.insertDoorData(door)
        }
    }

    fun clearData() {
        songRepository.deleteAllSongs()
    }

}