package com.marcoeckstein.binance.api.client.prvt

import com.marcoeckstein.binance.api.lib.jvm.InstantAsDateTimeSerializerBase
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

internal object InstantAsDateTimeSerializer :
    InstantAsDateTimeSerializerBase(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), ChronoUnit.SECONDS)
