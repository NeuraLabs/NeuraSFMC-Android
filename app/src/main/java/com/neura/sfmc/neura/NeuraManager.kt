package com.neura.sfmc.neura

import android.content.Context
import android.support.annotation.NonNull
import android.text.TextUtils
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.neura.resources.authentication.AnonymousAuthenticationStateListener

import com.neura.resources.authentication.AuthenticationState
import com.neura.sdk.`object`.AnonymousAuthenticationRequest
import com.neura.standalonesdk.service.NeuraApiClient
import com.neura.standalonesdk.util.SDKUtils
import com.neura.resources.authentication.AnonymousAuthenticateData
import com.neura.resources.authentication.AnonymousAuthenticateCallBack
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.neura.resources.user.UserDetails
import com.neura.resources.user.UserDetailsCallbacks

import com.neura.sdk.service.SubscriptionRequestCallbacks
import com.neura.sfmc.salesforce.SalesForceManager


/**
 * Wrapper Class for {@link NeuraApiClient} that is responsible for interact with Neura API
 */
class NeuraManager(@NonNull neuraApiClient: NeuraApiClient) :
    AnonymousAuthenticationStateListener {

    private val mNeuraApiClient = neuraApiClient
    private val mTag = "NeuraManager"
    lateinit var mAuthListener: AuthListener
    private var mNeuraUserId: String? = null

    /**
     * Array of Neura moments you would like to subscribe to
     */
    private val mMoments = arrayOf("userWokeUp", "userArrivedToWork", "userIsIdleAtHome")

    /**
     * Perform Anonymous authentication to Neura via {@link NeuraApiClient#authenticate}
     */
    fun authenticate(listener: AuthListener, externalID : String?) {
        mAuthListener = listener
        mNeuraApiClient.registerAuthStateListener(this)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val deviceToken = instanceIdResult.token
            if (TextUtils.isEmpty(deviceToken)) {
                mAuthListener.authFailed("FireBase token is Empty - please try again")
            } else {
                var request = AnonymousAuthenticationRequest(deviceToken)
                if (!TextUtils.isEmpty(externalID)) {
                    request.externalId = externalID
                }
                mNeuraApiClient.authenticate(request, object : AnonymousAuthenticateCallBack {
                    override fun onSuccess(authenticateData: AnonymousAuthenticateData) {
                        Log.i(mTag, "Successfully requested authentication with neura.")
                        mNeuraUserId = authenticateData.neuraUserId
                    }

                    override fun onFailure(errorCode: Int) {
                        // Authentication request failed.
                        mAuthListener.authFailed(SDKUtils.errorCodeToString(errorCode));
                    }
                })
            }
        }
    }

    /**
     * Subscribe to Neura Moments to the moments defined in {@link #mMoments array}
     */
     fun subscribeMoments() {
        for (moment in mMoments) {
            // YourMomentIdentifier_ is recommended to be the NeuraID of the user for follow up with customer suppport
            mNeuraApiClient.subscribeToEvent(moment,
                "NeuraSFMC_" + moment,
                object : SubscriptionRequestCallbacks {
                    override fun onSuccess(eventName: String, bundle: Bundle, s1: String) {
                        Log.i(mTag, "Successfully subscribed to event $eventName")
                    }

                    override fun onFailure(eventName: String, bundle: Bundle, i: Int) {
                        Log.e(mTag, "Failed to subscribe to event $eventName")
                    }
                })
        }
    }

    /**
     * Disconnect from Neura
     */
    fun disconnect(listener: DisconnectListener) {
        mNeuraApiClient.forgetMe(object : Handler.Callback {
            override fun handleMessage(msg: Message?): Boolean {
                if (msg?.arg1 == 1) {
                    listener.disconnectedSuccessfully()
                } else {
                    listener.disconnectionFailed()
                }
                return true
            }
        })
    }

    /**
     * calling {@link NeuraApiClient#getUserDetails} and
     * extracting the Neura ID using {@link UserDetails#data#getNeuraId}
     * caching the NeuraID for future use.
     */
    fun getNeuraId(listener: NeuraIdListener) {
        if (!TextUtils.isEmpty(mNeuraUserId)) {
            listener.onSuccess("$mNeuraUserId")
            return
        }
        mNeuraApiClient.getUserDetails(object : UserDetailsCallbacks {
            override fun onSuccess(userDetails: UserDetails?) {
                var userID = userDetails?.data?.neuraId
                if (TextUtils.isEmpty(userID)) {
                    userID = "Unknown"
                }
                listener.onSuccess("$userID")
            }

            override fun onFailure(resultData: Bundle?, errorCode: Int) {
                var error = SDKUtils.errorCodeToString(errorCode);
                Log.e(mTag, "get user details failed: $error")
                listener.onFailure(error)
            }
        })
    }

    /**
     * Will set the external ID to the default format: "sfmc_{$NeuraID}"
     * Will store the externalID using {@NeuraPrefs}
     */
    fun setDefaultNeuraExternalID(context: Context?) {
        getNeuraId(object : NeuraIdListener {
            override fun onSuccess(neuraId: String) {
                val externalID = "sfmc_$neuraId"
                mNeuraApiClient.setExternalId(externalID)
                NeuraPrefs().setExternalID(context, externalID, SalesForceManager())
            }

            override fun onFailure(reason: String) {
                Log.e(mTag, "setDefaultNeuraID() failed: $reason")
            }
        })
    }

    /**
     * check if user is Authenticated to Neura
     * @return Only if the user is currently authenticate to Neura, otherwise - False
     */
    fun isLoggedIn(): Boolean {
        return mNeuraApiClient.isLoggedIn
    }

    // ------- Neura AnonymousAuthenticationStateListener Call back ----------
    override fun onStateChanged(state: AuthenticationState?) {
        when (state) {
            AuthenticationState.AuthenticatedAnonymously -> {
                // Authentication flow completed successfully
                mNeuraApiClient.unregisterAuthStateListener()
                mAuthListener.authSuccessfully()
            }

            AuthenticationState.AccessTokenRequested -> {
                Log.i(mTag, "Access token requested successfully.")
            }

            AuthenticationState.FailedReceivingAccessToken -> {
                mNeuraApiClient.unregisterAuthStateListener()
                mAuthListener.authFailed("Failed Receiving Access token")
            }

            AuthenticationState.NotAuthenticated -> {
                mNeuraApiClient.unregisterAuthStateListener()
                mAuthListener.authFailed("NotAuthenticated, Auth process Failed.")
            }
        }
    }
}