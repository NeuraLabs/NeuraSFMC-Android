package com.neura.sfmc.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast

import com.neura.sfmc.R
import com.neura.sfmc.neura.DisconnectListener
import com.neura.sfmc.neura.NeuraIdListener
import com.neura.sfmc.neura.NeuraPrefs
import com.neura.sfmc.ui.activities.MainActivity
import kotlinx.android.synthetic.main.connected_fargment.*

class ConnectedFragment : BaseFragment(), DisconnectListener {

    companion object {
        fun newInstance(): ConnectedFragment {
            return ConnectedFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.connected_fargment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disconnect_btn.setOnClickListener {
            handleDisconnect()
        }
        setUserId()
        setExternalID()
    }

    /**
     * Disable disconnect_btn, show progress bar and call {@link NeuraManager#disconnect}
     */
    private fun handleDisconnect() {
        disconnect_btn.isEnabled = false
        pb.visibility = VISIBLE
        getNeuraManager().disconnect(this)
    }

    /**
     * extracting the user id using {@link NeuraManager#getNeuraId}
     */
    private fun setUserId() {
        getNeuraManager().getNeuraId(object : NeuraIdListener {
            override fun onSuccess(neuraId: String) {
                neura_id.text = "Neura ID: $neuraId"
            }

            override fun onFailure(reason: String) {
                Log.e("ConnectedFragment", reason)
            }
        })

    }

    private fun setExternalID(){
        var externalID = NeuraPrefs().getExternalID(context)
        if(externalID != null) {
            external_id.text = "External ID: $externalID"
        }
    }

    // --------- Neura CallBack -------------
    override fun disconnectedSuccessfully() {
        NeuraPrefs().removeExternalID(context)
        (activity as MainActivity).loadFragment()
    }

    override fun disconnectionFailed() {
        disconnect_btn.isEnabled = true
        pb.visibility = INVISIBLE
        Toast.makeText(context, "Disconnection Failed", Toast.LENGTH_LONG).show()
    }
}