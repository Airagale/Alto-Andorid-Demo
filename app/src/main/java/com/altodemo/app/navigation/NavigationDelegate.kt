package com.altodemo.app.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class NavigationDelegate : NavigationEventReceiver, NavigationEventHandler {

    private val _eventFlow = MutableStateFlow<NavigationEvent?>(null)
    override fun invoke(): Flow<NavigationEvent> = _eventFlow.filterNotNull()

    override fun popUpToDestination(destination: String) {
        _eventFlow.value = NavigationEvent.PopUpTo(
            destination = destination
        )
    }

    override fun navigationToDestination(destination: String) {
        _eventFlow.value = NavigationEvent.Navigate(
            destination = destination
        )
    }

    override fun navigateUp() {
        _eventFlow.value = NavigationEvent.NavigateUp
    }
}