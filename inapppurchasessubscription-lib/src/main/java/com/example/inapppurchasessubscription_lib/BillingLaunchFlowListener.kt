package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase

interface BillingLaunchFlowListener {
    fun onBillingFailed(error: String, responseCode: Int)
    fun onBillingInitiatedSuccessfully(status: String, billingResult: BillingResult)
    fun onBillingCanceled(status: String, billingResult: BillingResult)
    fun onBillingItemAlreadyOwned(status: String, billingResult: BillingResult)
    fun onProductPurchasedSuccessfully(billingResult: BillingResult, purchases: List<Purchase>?)
    fun onProductPurchasePending(billingResult: BillingResult, purchases: List<Purchase>?)
    fun onProductUnspecified(billingResult: BillingResult, purchases: List<Purchase>?)
    fun onProductFailedToPurchase(billingResult: BillingResult)
}
