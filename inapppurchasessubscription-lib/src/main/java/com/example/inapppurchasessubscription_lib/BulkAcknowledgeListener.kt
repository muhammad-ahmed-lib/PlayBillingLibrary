package com.example.inapppurchasessubscription_lib


/**
 * Interface for handling bulk purchase acknowledgment callbacks.
 * Implement this interface to respond to the outcomes of acknowledging multiple purchases at once.
 */
interface BulkAcknowledgeListener {

    /**
     * Called when a bulk acknowledgment operation completes.
     * This provides individual results for each purchase acknowledgment attempt.
     *
     * @param results A list of [AcknowledgeResult] objects containing the outcome
     *                for each purchase in the bulk operation.
     */
    fun onBulkAcknowledgeComplete(results: List<AcknowledgeResult>)

    /**
     * Called when all purchases in a bulk operation are already acknowledged.
     * This indicates that no new acknowledgments were needed as all purchases
     * were previously acknowledged.
     */
    fun onAllAlreadyAcknowledged()
}
