package com.room.database.dao

import androidx.room.*
import com.room.database.bean.Door

/**
 * @author: finaly
 * @date: 2023/11/19 21:36
 * @desc: 数据库dao
 */
@Dao
interface DoorDao {
        @Query("SELECT * FROM Door")
         fun getAll(): MutableList<Door>

        @Insert
         fun insertDoorData(door: Door)

        @Query("DELETE FROM Door")
         fun deleteAllDoor()

    @Query("SELECT * FROM Door where time = :timeId")
     fun getDoorTimeId(timeId: String): Door

}