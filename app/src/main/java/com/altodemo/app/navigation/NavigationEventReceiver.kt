package com.altodemo.app.navigation

import kotlinx.coroutines.flow.Flow


interface NavigationEventReceiver {
    operator fun invoke(): Flow<NavigationEvent>
}