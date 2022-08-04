package com.zen.alchan.ui.base

interface ViewModelContract<T> {
    fun loadData(param: T)
}