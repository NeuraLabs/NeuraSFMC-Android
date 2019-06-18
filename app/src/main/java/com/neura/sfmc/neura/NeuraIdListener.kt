package com.neura.sfmc.neura

interface NeuraIdListener {

    fun onSuccess(neuraId: String)
    fun onFailure(reason: String)
}