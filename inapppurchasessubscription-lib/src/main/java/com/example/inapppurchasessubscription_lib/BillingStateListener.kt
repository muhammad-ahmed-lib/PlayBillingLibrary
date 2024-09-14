package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.BillingResult

/**
 * Interface for handling billing service connection state changes.
 * Implement this interface to respond to billing connection and disconnection events.
 */
interface BillingStateListener {

    /**
     * Called when the billing service is successfully connected.
     *
     * @param isConnected A boolean indicating if the service is connected.
     * @param billingResult Contains the result of the connection attempt, including status details.
     */
    fun onConnected(isConnected: Boolean, billingResult: BillingResult)

    /**
     * Called when the billing service is disconnected.
     *
     * @param isConnected A boolean indicating if the service is disconnected (will always be false).
     * @param responseCode An integer representing the disconnection response code.
     */
    fun onDisconnected(isConnected: Boolean, responseCode: Int)
}
