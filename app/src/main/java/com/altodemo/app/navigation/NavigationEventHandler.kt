package com.altodemo.app.navigation

interface NavigationEventHandler {
    fun navigateUp()
    fun popUpToDestination(destination:String)
    fun navigationToDestination(destination: String)
}