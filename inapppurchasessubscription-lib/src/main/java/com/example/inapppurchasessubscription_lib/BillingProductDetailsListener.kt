package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails

/**
 * Interface for handling the callbacks related to retrieving product details
 * for in-app purchases and subscriptions.
 */
interface BillingProductDetailsListener {

    /**
     * Called when product details are successfully retrieved.
     *
     * @param billingResult Contains the result of the product details retrieval process.
     * @param productDetails A list of product details retrieved from the BillingClient.
     */
    fun onProductDetailsRetrieved(billingResult: BillingResult, productDetails: List<ProductDetails>)

    /**
     * Called when the retrieval of product details fails.
     *
     * @param errorCode An integer representing the error code for the failure.
     * @param errorMessage A descriptive error message explaining the failure.
     */
    fun onProductDetailsRetrievalFailed(errorCode: Int, errorMessage: String)
}
