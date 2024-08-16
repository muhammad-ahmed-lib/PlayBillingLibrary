package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.InAppMessageResult

interface InAppBillingMessaging {
    fun onNoActionNeeded(inAppMessageResult: InAppMessageResult)
    fun onSubscriptionStatusUpdated(inAppMessageResult: InAppMessageResult)
    fun onFailedToReceiveMessages(error:Int)
}