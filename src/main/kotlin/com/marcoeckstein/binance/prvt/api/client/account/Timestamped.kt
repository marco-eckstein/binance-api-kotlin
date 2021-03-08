package com.marcoeckstein.binance.prvt.api.client.account

import java.time.Instant

interface Timestamped {

    val timestamp: Instant
}
