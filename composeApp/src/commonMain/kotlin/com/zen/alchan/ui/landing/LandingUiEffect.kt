package com.zen.alchan.ui.landing

sealed interface LandingUiEffect {
    object NavigateToLogin : LandingUiEffect
    object NavigateToMain : LandingUiEffect
}