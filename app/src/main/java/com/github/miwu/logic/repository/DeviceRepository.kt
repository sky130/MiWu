package com.github.miwu.logic.repository

import com.github.miwu.logic.database.AppDatabase
import com.github.miwu.logic.database.model.MiwuDatabaseDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class DeviceRepository : KoinComponent {

    private val database = get<AppDatabase>()
    private val dao get() = database.device()

    val deviceList = dao.getListFlow()

    suspend fun replaceList(list: ArrayList<MiwuDatabaseDevice>) = withContext(Dispatchers.IO) {
        dao.deleteAll()
        for (i in 0..<list.size) {
            list[i].position = i
        }
        dao.insert(list)
    }
}