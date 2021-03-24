package com.marcoeckstein.binance.api.client.prvt.account.request

import com.marcoeckstein.binance.api.client.prvt.account.AccountType
import kotlinx.serialization.Serializable

@Serializable
data class OpenOrdersQuery(val accountType: AccountType)
