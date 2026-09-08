package com.github.miwu.domain.repository

interface SettingsRepository {
    var selectedHomeId: Long
    var selectedOwnerId: Long
    var hasPendingCrash: Boolean
}
