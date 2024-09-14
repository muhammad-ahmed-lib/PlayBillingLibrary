package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.InAppMessageResult

/**
 * Interface for handling in-app billing messages and subscription status updates.
 * Implement this interface to respond to messaging events and subscription updates.
 */
interface InAppBillingMessaging {

    /**
     * Called when no action is needed for in-app messages or subscription status.
     *
     * @param inAppMessageResult Contains the result of the message handling, including status details.
     */
    fun onNoActionNeeded(inAppMessageResult: InAppMessageResult)

    /**
     * Called when the subscription status is updated, such as when a user subscribes, renews, or cancels.
     *
     * @param inAppMessageResult Contains the result of the subscription status update.
     */
    fun onSubscriptionStatusUpdated(inAppMessageResult: InAppMessageResult)

    /**
     * Called when the app fails to receive in-app messages or updates related to billing.
     *
     * @param error An integer representing the error code indicating the failure.
     */
    fun onFailedToReceiveMessages(error: Int)
}
