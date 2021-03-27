package com.marcoeckstein.binance.api.extra.report

import com.marcoeckstein.binance.api.facade
import com.marcoeckstein.klib.java.math.equalsComparing
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@ExperimentalSerializationApi
class ReportGeneratorTests {

    companion object {

        private val reportGenerator = ReportGenerator(facade)

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
        val errorMarginGross =
            if (asset == "BNB") getBnbErrorMarginGross(quantities, history) else BigDecimal.ZERO
        val errorMarginNet =
            if (asset == "BNB") getBnbErrorMarginNet(quantities, history) else BigDecimal.ZERO
        if (diffGross.abs() > errorMarginGross || diffNet.abs() > errorMarginNet) {
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

    private fun getBnbErrorMarginGross(quantities: AssetQuantitiesReport, history: AssetHistoryReport) =
        (quantities.gross.abs() + history.gross.abs()) / BigDecimal.valueOf(10_000_000)

    private fun getBnbErrorMarginNet(quantities: AssetQuantitiesReport, history: AssetHistoryReport) =
        (quantities.net.abs() + history.net.abs()) / BigDecimal.valueOf(10_000_000)
}
