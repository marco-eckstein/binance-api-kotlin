package com.marcoeckstein.binance.prvt.api.client

import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsDateTimeSerializerBase
import java.time.format.DateTimeFormatter

internal object InstantAsDateTimeSerializer :
    InstantAsDateTimeSerializerBase(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
