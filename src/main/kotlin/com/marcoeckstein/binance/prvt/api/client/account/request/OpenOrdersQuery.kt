package com.marcoeckstein.binance.prvt.api.client.account.request

import com.marcoeckstein.binance.prvt.api.client.account.AccountType
import kotlinx.serialization.Serializable

@Serializable
data class OpenOrdersQuery(val accountType: AccountType)
