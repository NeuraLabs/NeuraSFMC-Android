package com.neura.sfmc.neura

interface AuthListener {

    /**
     * Indicate a successful Authentication
     */
    fun authSuccessfully()

    /**
     *  Indicate an Auth failure
     *  @param reason human readble string of the cause
     */
    fun authFailed(reason: String)
}