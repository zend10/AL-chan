package com.zen.alchan.ui.base

import kotlinx.coroutines.CoroutineDispatcher

interface Dispatcher {
    val ui: CoroutineDispatcher
    val io: CoroutineDispatcher
}