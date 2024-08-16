package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.BillingResult

interface BillingStateListener {
    fun onConnected(isConnected:Boolean,billingResult: BillingResult)
    fun onDisconnected(isConnected:Boolean,responseCode:Int)
}