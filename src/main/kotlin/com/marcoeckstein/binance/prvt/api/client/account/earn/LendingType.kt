package com.marcoeckstein.binance.prvt.api.client.account.earn

enum class LendingType {

    /**
     * Aka flexible
     */
    DAILY,

    /**
     * Aka activities
     */
    REGULAR,

    /**
     * Aka locked
     */
    CUSTOMIZED_FIXED,

    /**
     * Aka savings trial fund
     */
    EXPERIENCE_COUPON,
}
