package com.altodemo.app.navigation

sealed class NavigationEvent {
    object NavigateUp :NavigationEvent()
    data class PopUpTo(override val destination: String) : Destination()
    data class Navigate(override val destination: String) : Destination()
}

sealed class Destination : NavigationEvent(){
    abstract val destination: String
}