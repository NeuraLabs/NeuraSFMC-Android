package com.neura.sfmc.ui.activities

import android.support.v7.app.AppCompatActivity
import com.neura.sfmc.NeuraApplication
import com.neura.sfmc.neura.NeuraManager
import com.neura.standalonesdk.service.NeuraApiClient

abstract class BaseActivity : AppCompatActivity(){

    /**
     * return {@link NeuraManager} instance
     */
    fun getNeuraManager() : NeuraManager{
        return (applicationContext as NeuraApplication).getNeuraManager()
    }

}