package com.github.miwu.logic.repository

import com.github.miwu.logic.database.AppDatabase
import com.github.miwu.logic.database.model.MiwuDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DeviceRepository {

    private val database get() = AppDatabase.instance
    private val dao get() = database.deviceDAO()

    val flow = dao.getList()

    suspend fun replaceList(list: ArrayList<MiwuDevice>) = withContext(Dispatchers.IO) {
        dao.deleteAll()
        for (i in 0..< list.size) {
            list[i].index = i
        }
        dao.insert(list)
    }
}