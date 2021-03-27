package com.marcoeckstein.binance.api.lib.guava

import com.google.common.base.CaseFormat

internal fun String.convert(pair: Pair<CaseFormat, CaseFormat>): String =
    convert(pair.first, pair.second)

internal fun String.convert(from: CaseFormat, to: CaseFormat): String =
    from.to(to, this)
