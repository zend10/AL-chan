package com.zen.alchan.ui.base

interface ViewHolderContract<T> {
    fun bind(item: T, index: Int)
}