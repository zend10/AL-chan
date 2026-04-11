package com.zen.alchan

object DeeplinkHandler {
    var deeplinkListener: DeeplinkListener? = null

    fun handleDeeplink(deeplink: String) {
        deeplinkListener?.onDeeplinkReceived(deeplink)
    }
}

interface DeeplinkListener {
    fun onDeeplinkReceived(deeplink: String)
}


