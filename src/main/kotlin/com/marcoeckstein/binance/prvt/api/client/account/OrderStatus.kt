package com.marcoeckstein.binance.prvt.api.client.account

enum class OrderStatus {

    NEW,
    PARTIAL_FILL,
    FILLED,
    CANCELED,
    PENDING_CANCEL,
    REJECTED,
    EXPIRED,
}
