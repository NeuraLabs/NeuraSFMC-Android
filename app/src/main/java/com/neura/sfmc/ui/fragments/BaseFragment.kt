package com.neura.sfmc.ui.fragments

import android.support.v4.app.Fragment
import com.neura.sfmc.NeuraApplication
import com.neura.sfmc.neura.NeuraManager

abstract class BaseFragment : Fragment(){
    val PERMISSION_REQUEST_FINE_LOCATION: Int = 1234

    /**
     * @return {@link NeuraManager} instance
     */
    fun getNeuraManager() : NeuraManager{
        return (activity?.applicationContext as NeuraApplication).getNeuraManager()
    }

}