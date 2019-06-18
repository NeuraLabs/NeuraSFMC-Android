package com.neura.sfmc

import android.app.Application
import com.neura.sfmc.neura.NeuraManager
import com.neura.sfmc.salesforce.SalesForceManager
import com.neura.standalonesdk.service.NeuraApiClient

class NeuraApplication : Application() {

    private lateinit var mNeuraManager: NeuraManager

    override fun onCreate() {
        super.onCreate()
        initNeura()
        initSalesForce()
    }

    private fun initNeura() {
        val client = NeuraApiClient.getClient(
            this,
            "{YOUR_APP_UID}",
            "{YOUR_APP_SECRET}"
        )
        mNeuraManager = NeuraManager(client)
    }

    private fun initSalesForce() {
       SalesForceManager().init(this)
    }

    fun getNeuraManager(): NeuraManager {
        return mNeuraManager
    }

}