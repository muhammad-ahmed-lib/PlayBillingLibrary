package com.example.inapppurchasessubscription_lib

import com.android.billingclient.api.Purchase


/**
 * Data class representing the result of an individual purchase acknowledgment attempt.
 * This is used in bulk acknowledgment operations to provide detailed outcomes.
 *
 * @property purchase The purchase object that was attempted to be acknowledged.
 * @property success Indicates whether the acknowledgment was successful (true) or failed (false).
 * @property message A descriptive message about the acknowledgment result.
 */
data class AcknowledgeResult(
    val purchase: Purchase,
    val success: Boolean,
    val message: String
)