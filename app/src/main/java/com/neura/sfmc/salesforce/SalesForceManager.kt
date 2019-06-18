package com.neura.sfmc.salesforce

import android.content.Context
import android.util.Log
import com.neura.sfmc.R
import com.salesforce.marketingcloud.MarketingCloudConfig
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions

class SalesForceManager {

    /**
     * Initialize Sales force SDK - call from Application class onCreate
     */
    fun init(context: Context) {
        MarketingCloudSdk.init(context, MarketingCloudConfig.builder().apply {
            setApplicationId(context.getString(R.string.salesforce_application_id))
            setAccessToken(context.getString(R.string.salesforce_access_token))
            setSenderId(context.getString(R.string.fire_base_sender_id))
            setMarketingCloudServerUrl(context.getString(R.string.marketing_cloud_server_url))
            setMid(context.getString(R.string.salesforce_mid))
            setNotificationCustomizationOptions(
                NotificationCustomizationOptions.create(
                    R.drawable.neura_sdk_notification_status_icon
                )
            )
        }.build(context)) { status ->
            val state = if (status.isUsable) "is ready to use" else "is not ready"
            Log.i("SalesForceManager", "Sales force $state")
        }
    }


    /**
     *  Sets the passed contact ket to MarketingCloudSDK - in this example we set this value also to
     *  Neura External ID
     */
    fun setContactKey(contactKey: String) {
        MarketingCloudSdk.requestSdk { sdk ->
            val registrationManager = sdk.registrationManager

            // Set Contact Key
            registrationManager.edit().run {
                setContactKey(contactKey)
                commit()
            }
        }
    }

}