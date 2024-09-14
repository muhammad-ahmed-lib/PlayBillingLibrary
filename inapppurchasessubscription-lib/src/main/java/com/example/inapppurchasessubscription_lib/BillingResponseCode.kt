package com.example.inapppurchasessubscription_lib

/**
 * Enum class representing various billing response codes and their corresponding messages.
 * This can be used to handle different error or success cases during the billing process.
 *
 * @param code The integer code representing the billing response.
 * @param message A descriptive message associated with the billing response.
 */
enum class BillingResponseCode(val code: Int, val message: String) {

    /**
     * Error when no product details are found for the given product ID(s).
     *
     * @param code 1001 The error code indicating the product was not found.
     * @param message Detailed message with instructions for resolving the issue.
     */
    PRODUCT_NOT_FOUND(1001, "No product details found for product. Please try again and add callback to " +
            "getProductDetails(productIds: List<String>, " +
            "listener: BillingProductDetailsListener) before calling this method"),

    /**
     * Error when a purchase fails due to unknown or technical reasons.
     *
     * @param code 1002 The error code indicating the purchase failed.
     * @param message Message suggesting the user try again later.
     */
    PURCHASE_FAILED(1002, "Purchase failed. Please try again later."),

    /**
     * Error when the user cancels the purchase process.
     *
     * @param code 1003 The error code indicating the user canceled the purchase.
     * @param message Message explaining the cancellation.
     */
    USER_CANCELED(1003, "User canceled the purchase."),

    /**
     * Error when the user tries to purchase an item they already own.
     *
     * @param code 1004 The error code indicating the item is already owned.
     * @param message Message explaining the item is already owned.
     */
    ITEM_ALREADY_OWNED(1004, "Item is already owned."),

    /**
     * Error when the billing service is disconnected.
     *
     * @param code 1005 The error code indicating the billing service is disconnected.
     * @param message Message explaining the service disconnection.
     */
    SERVICE_DISCONNECTED(1005, "Billing service is disconnected."),

    /**
     * Default error for unknown or unexpected issues.
     *
     * @param code 9999 The error code for unknown errors.
     * @param message General message indicating an unknown error.
     */
    UNKNOWN_ERROR(9999, "An unknown error occurred.");

    companion object {

        /**
         * Retrieve the [BillingResponseCode] corresponding to the given code.
         *
         * @param code The integer code to look up the corresponding [BillingResponseCode].
         * @return The matching [BillingResponseCode], or [UNKNOWN_ERROR] if the code is not found.
         */
        fun fromCode(code: Int): BillingResponseCode {
            return values().find { it.code == code } ?: UNKNOWN_ERROR
        }
    }
}
