package com.neura.sfmc.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.neura.standalonesdk.events.NeuraEvent
import com.neura.standalonesdk.events.NeuraPushCommandFactory
import com.google.firebase.messaging.RemoteMessage
import com.neura.standalonesdk.events.NeuraEventCallBack
import com.salesforce.marketingcloud.MarketingCloudSdk


class NeuraEventsService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage?) {
        val isNeuraPush = NeuraPushCommandFactory.getInstance()
            .isNeuraPush(applicationContext, message!!.data, NeuraEventCallBack { event: NeuraEvent? ->
                val eventText = event?.toString() ?: "couldn't parse data"
                // handle Neura Event
            })

        if (!isNeuraPush) {
            //Handle non neura push here
            MarketingCloudSdk.getInstance()!!.pushMessageManager.handleMessage(message)
        }
    }
}