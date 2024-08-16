package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails

interface BillingProductDetailsListener {
    fun onProductDetailsRetrieved(billingResult: BillingResult,productDetails: List<ProductDetails>)
    fun onProductDetailsRetrievalFailed(errorCode: Int, errorMessage: String)
}