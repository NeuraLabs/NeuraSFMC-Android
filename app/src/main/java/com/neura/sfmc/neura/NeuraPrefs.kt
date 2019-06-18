package com.neura.sfmc.neura

import android.content.Context
import android.content.SharedPreferences
import com.neura.sfmc.salesforce.SalesForceManager

class NeuraPrefs {

    private val mNeuraPrefs = "NEURA_PREFS"
    private val mExternalIdKey = "EXTERNAL_ID"

    /**
     * Will store the external ID in prefs, will set this value as a contact key if salesForceManager instance is passed.
     *
     * @param context - component context
     * @param externalId The External ID value
     * @param salesforceManager a {@link SalesForceManager} instance, will be used to set the external ID as a contact key
     */
    fun setExternalID(context: Context?, externalId: String, salesforceManager : SalesForceManager?) {
        if (context == null) {
            return
        }
        getPrefs(context).edit().putString(mExternalIdKey, externalId).apply()
        salesforceManager?.setContactKey(externalId)
    }

    fun getExternalID(context: Context?): String? {
        if (context == null) {
            return null
        }
        return getPrefs(context).getString(mExternalIdKey, null)
    }

    fun removeExternalID(context: Context?) {
        if (context == null) {
            return
        }
        getPrefs(context).edit().remove(mExternalIdKey).apply()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(mNeuraPrefs, Context.MODE_PRIVATE)
    }
}