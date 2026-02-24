package com.zen.alchan.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zen.alchan.helper.TimeUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.compareTo

abstract class BaseViewModel<S, E>(
    initialState: S,
    protected val dispatcher: Dispatcher,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<E>()
    val effect: SharedFlow<E> = _effect.asSharedFlow().throttleFirst(800)

    protected fun updateState(updater: (currentState: S) -> S) {
        _state.update(updater)
    }

    protected fun sendNewEffect(newEffect: E) {
        viewModelScope.launch(dispatcher.ui) {
            _effect.emit(newEffect)
        }
    }

    private fun <T> SharedFlow<T>.throttleFirst(periodMillis: Long): SharedFlow<T> {
        require(periodMillis > 0)
        return flow {
            var lastTime = 0L
            collect { value ->
                val currentTime = TimeUtils.getCurrentMillis()
                if (currentTime - lastTime >= periodMillis) {
                    lastTime = currentTime
                    emit(value)
                }
            }
        }.shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    }
}