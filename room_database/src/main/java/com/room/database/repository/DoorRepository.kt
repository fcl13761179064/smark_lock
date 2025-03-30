package com.room.database.repository

import com.room.database.bean.Door
import com.room.database.dao.DoorDao

/**
 * @author: finaly
 * @date: 2023/7/15 21:36
 * @desc: 操作数据库
 */
class DoorRepository(private val doorDao: DoorDao) {

     fun getAllSongs(): MutableList<Door> {
        return doorDao.getAll()
    }

     fun insertDoorData(door: Door) {
        doorDao.insertDoorData(door)
    }
     fun getSongById(doorTimeId: String): Door {
        return doorDao.getDoorTimeId(doorTimeId)
    }

     fun deleteAllSongs() {
        doorDao.deleteAllDoor()
    }
}