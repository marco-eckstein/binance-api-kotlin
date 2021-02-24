package com.marcoeckstein.binance.prvt.api.lib.jvm

import java.math.BigDecimal

infix fun BigDecimal.equalsComparingTo(other: BigDecimal) =
    compareTo(other) == 0
