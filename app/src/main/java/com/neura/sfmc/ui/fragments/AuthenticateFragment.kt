package com.neura.sfmc.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast

import com.neura.sfmc.R
import com.neura.sfmc.neura.AuthListener
import com.neura.sfmc.neura.NeuraPrefs
import com.neura.sfmc.salesforce.SalesForceManager
import com.neura.sfmc.ui.activities.MainActivity

import kotlinx.android.synthetic.main.authenticate_fragment.*
import java.security.Permission

class AuthenticateFragment : BaseFragment(), AuthListener {

    companion object {

        fun newInstance(): AuthenticateFragment {
            return AuthenticateFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.authenticate_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connect_btn.setOnClickListener {
            handleNeuraConnect()
        }
    }

    /**
     * Will disable UI, show progress bar
     * and call {@link NeuraManager#authenticate}
     */
    private fun handleNeuraConnect() {
        showLoadingUi(true)

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_FINE_LOCATION)
        } else {
            getNeuraManager().authenticate(this, external_id_et.text.toString())
        }
    }

    private fun showLoadingUi(isLoading: Boolean) {
        connect_btn.isEnabled = !isLoading
        external_id_et.isEnabled = !isLoading
        if(isLoading) {
            pb.visibility = VISIBLE
        } else {
            pb.visibility = GONE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.i("PERMISSIONS_TEST", "onRequestPermissionsResult")
        when (requestCode) {
            PERMISSION_REQUEST_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getNeuraManager().authenticate(this, external_id_et.text.toString())
                } else {
                    showLoadingUi(false)
                    Snackbar.make(this!!.view!!,
                        "Unable to continue without location granted permission",
                        Snackbar.LENGTH_LONG)
                        .show()
                }

                return
            }
        }
    }


    //---------------- NEURA AUTHENTICATION CALLBACKS ------------------
    override fun authSuccessfully() {
        getNeuraManager().subscribeMoments()
        setDefaultExternalID()
        (activity as MainActivity).loadFragment()
    }

    /**
     * For this sample app - if no external ID is supplied we will set a default external ID
     * In case it was supplied it will be passed to Neura via authentication flow.
     */
    private fun setDefaultExternalID() {
        val externalID = external_id_et.text.toString()
        if (TextUtils.isEmpty(externalID)) {
            getNeuraManager().setDefaultNeuraExternalID(context)
        } else {
            NeuraPrefs().setExternalID(context, externalID, SalesForceManager())
        }
    }

    override fun authFailed(reason: String) {
        pb.visibility = INVISIBLE
        connect_btn.isEnabled = true;
        external_id_et.isEnabled = true;
        val error = "Authentication Failed, reason: $reason"
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        Log.e("AuthenticateFragment", error)
    }
}