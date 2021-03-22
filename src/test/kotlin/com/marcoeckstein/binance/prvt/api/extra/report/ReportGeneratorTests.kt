package com.marcoeckstein.binance.prvt.api.extra.report

import com.marcoeckstein.binance.prvt.api.privateApi
import com.marcoeckstein.binance.prvt.api.publicApi
import com.marcoeckstein.klib.java.math.equalsComparing
import com.marcoeckstein.klib.java.math.notEqualsComparing
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal

@ExperimentalSerializationApi
class ReportGeneratorTests {

    companion object {

        private val reportGenerator = ReportGenerator(publicApi, privateApi)

        @JvmStatic
        val assetQuantitiesReports: Map<String, AssetQuantitiesReport> by lazy {
            reportGenerator.getAssetQuantitiesReports()
        }

        @JvmStatic
        val assetHistoryReports: Map<String, AssetHistoryReport> by lazy {
            reportGenerator.getAssetHistoryReports()
        }

        @JvmStatic
        val assets
            get() = assetQuantitiesReports.keys
    }

    @ParameterizedTest
    @MethodSource("getAssets")
    fun `quantities are consistent with history`(asset: String) {
        val quantities = assetQuantitiesReports.getValue(asset)
        val history = assetHistoryReports.getValue(asset)
        val diffGross = quantities.gross - history.gross
        val diffNet = quantities.net - history.net
        if (diffGross notEqualsComparing BigDecimal.ZERO || diffNet notEqualsComparing BigDecimal.ZERO) {
            val message = "Reports for $asset are not consistent.\n\n" +
                quantities.toReportString() + "\n\n" +
                history.toReportString() + "\n\n" + """
                    = Summary =
                    Quantity gross:  ${quantities.gross.toPlainString()}
                    History gross: ${history.gross.toPlainString()}
                    Quantity net: ${quantities.net.toPlainString()}
                    History net: ${history.net.toPlainString()}
                """.trimIndent() + "\n" + (
                if (!(diffGross equalsComparing BigDecimal.ZERO))
                    "Diff gross (should be zero): ${diffGross.toPlainString()}" + "\n"
                else
                    ""
                ) + (
                if (!(diffNet equalsComparing BigDecimal.ZERO))
                    "Diff net (should be zero): ${diffNet.toPlainString()}"
                else
                    ""
                )
            throw AssertionError(message)
        }
    }
}
