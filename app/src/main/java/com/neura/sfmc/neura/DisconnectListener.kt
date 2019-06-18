package com.neura.sfmc.neura

interface DisconnectListener {

    /**
     * Indicate a successful disconnection from Neura
     */
    fun disconnectedSuccessfully()

    /**
     * Indicates a Failure on Disconnection process
     */
    fun disconnectionFailed()
}