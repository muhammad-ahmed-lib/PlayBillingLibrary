package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.Purchase

interface BillingPurchaseListener {
    fun onRestoreBillingFinished(
        isAppPurchased:Boolean,
        productDetails: MutableList<Purchase>
    )
    fun onRestoreBillingFailed(billingError:Int)

}