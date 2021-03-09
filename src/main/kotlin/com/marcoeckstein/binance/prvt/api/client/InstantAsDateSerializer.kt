package com.marcoeckstein.binance.prvt.api.client

import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsDateSerializerBase
import java.time.format.DateTimeFormatter

internal object InstantAsDateSerializer :
    InstantAsDateSerializerBase(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
