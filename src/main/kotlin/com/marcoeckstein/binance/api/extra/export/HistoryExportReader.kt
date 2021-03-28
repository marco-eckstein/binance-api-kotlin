package com.marcoeckstein.binance.api.extra.export

import com.marcoeckstein.binance.api.client.prvt.account.AccountType
import com.marcoeckstein.binance.api.client.prvt.account.Distribution
import com.marcoeckstein.binance.api.client.prvt.account.FiatDepositAndWithdrawHistoryEntry
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginBorrowing
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginInterest
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginRebate
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginRepayment
import com.marcoeckstein.binance.api.client.prvt.account.IsolatedMarginTransfer
import com.marcoeckstein.binance.api.client.prvt.account.Order
import com.marcoeckstein.binance.api.client.prvt.account.Payment
import com.marcoeckstein.binance.api.client.prvt.account.PaymentType
import com.marcoeckstein.binance.api.client.prvt.account.Trade
import com.marcoeckstein.binance.api.client.prvt.account.WithdrawDirection
import com.marcoeckstein.binance.api.client.prvt.account.earn.FlexibleSavingsInterest
import com.marcoeckstein.binance.api.client.prvt.account.earn.LockedStakingInterest
import com.marcoeckstein.binance.api.extra.BinanceApiFacade
import kotlinx.serialization.ExperimentalSerializationApi
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi

@ExperimentalSerializationApi
@ExperimentalPathApi
class HistoryExportReader(
    private val directory: Path = Path.of("."),
) : BinanceApiFacade {

    override fun getFiatDepositAndWithdrawHistory(
        direction: WithdrawDirection
    ): List<FiatDepositAndWithdrawHistoryEntry> =
        readItems(direction)

    override fun getPaymentHistory(paymentType: PaymentType): List<Payment> =
        readItems<Payment>().filter { it.type == paymentType }

    override fun getOrderHistory(accountType: AccountType): List<Order> =
        readItems(accountType)

    override fun getTradeHistory(accountType: AccountType): List<Trade> =
        readItems(accountType)

    override fun getDistributionHistory(): List<Distribution> =
        readItems()

    override fun getFlexibleSavingsInterestHistory(): List<FlexibleSavingsInterest> =
        readItems()

    override fun getLockedStakingInterestHistory(): List<LockedStakingInterest> =
        readItems()

    override fun getIsolatedMarginBorrowingHistory(): List<IsolatedMarginBorrowing> =
        readItems()

    override fun getIsolatedMarginRepaymentHistory(): List<IsolatedMarginRepayment> =
        readItems()

    override fun getIsolatedMarginTransferHistory(): List<IsolatedMarginTransfer> =
        readItems()

    override fun getIsolatedMarginInterestHistory(): List<IsolatedMarginInterest> =
        readItems()

    override fun getIsolatedMarginRebateHistory(): List<IsolatedMarginRebate> =
        readItems()

    private inline fun <reified T : Any> readItems(): List<T> =
        readItems(getSingleItemInfo())

    private inline fun <reified T : Any> readItems(discriminator: Any): List<T> =
        readItems(getSingleItemInfo(discriminator))

    private inline fun <reified T : Any> readItems(itemInfo: ItemInfo<T>) =
        csvFileIO().readItems(directory.resolve(itemInfo.filename), itemInfo.serializer)
}
