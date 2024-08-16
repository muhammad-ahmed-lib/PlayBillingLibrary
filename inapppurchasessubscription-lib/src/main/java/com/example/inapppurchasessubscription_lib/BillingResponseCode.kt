package com.example.inapppurchasessubscription_lib

enum class BillingResponseCode(val code: Int, val message: String) {
    PRODUCT_NOT_FOUND(1001, "No product details found for product. Please try again and add callback to " +
            "getProductDetails(productIds: List<String>, " +
            "listener: BillingProductDetailsListener) before calling this methode"),
    PURCHASE_FAILED(1002, "Purchase failed. Please try again later."),
    USER_CANCELED(1003, "User canceled the purchase."),
    ITEM_ALREADY_OWNED(1004, "Item is already owned."),
    SERVICE_DISCONNECTED(1005, "Billing service is disconnected."),
    UNKNOWN_ERROR(9999, "An unknown error occurred.");

    companion object {
        fun fromCode(code: Int): BillingResponseCode {
            return values().find { it.code == code } ?: UNKNOWN_ERROR
        }
    }
}
