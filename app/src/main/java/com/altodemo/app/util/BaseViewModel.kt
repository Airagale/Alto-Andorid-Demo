package com.altodemo.app.util

import androidx.lifecycle.ViewModel
import com.altodemo.app.navigation.NavigationEventHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber
import javax.inject.Inject

open class BaseViewModel : ViewModel() {

    /**
     * Abstraction hell can effectively hide this from descendant view models, but no worth it for this example.
     */
    @Inject
    protected lateinit var navigator: NavigationEventHandler

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        Timber.e(t)
    }
}