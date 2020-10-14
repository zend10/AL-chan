package com.zen.alchan.notifications

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zen.alchan.data.repository.UserRepository
import org.koin.android.ext.android.inject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val userRepository: UserRepository by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        userRepository.sendFirebaseToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val intent = Intent(applicationContext, PushNotificationsService::class.java)
        PushNotificationsService().enqueueWork(applicationContext, intent)
    }
}