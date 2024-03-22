package com.codeskraps.feature.symbol

import com.github.mikephil.charting.data.Entry

data class SuperGuppy(
    val fast: List<Entry>,
    val med: List<Entry>,
    val slow: List<Entry>,
    val colFinal: Int,
    val colFinal2: Int
)
