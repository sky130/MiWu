package com.github.miwu.data.settings

import com.github.miwu.domain.repository.SettingsRepository

class SettingsRepositoryImpl : SettingsRepository {
    override var selectedHomeId: Long
        get() = AppSettings.homeId.value
        set(value) {
            AppSettings.homeId.value = value
        }

    override var selectedOwnerId: Long
        get() = AppSettings.ownerId.value
        set(value) {
            AppSettings.ownerId.value = value
        }

    override var hasPendingCrash: Boolean
        get() = AppSettings.isCrash.value
        set(value) {
            AppSettings.isCrash.value = value
        }
}
