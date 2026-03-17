package com.krayapp.buffercompanion.bargen.domain.type

import com.krayapp.buffercompanion.bargen.R

enum class SortType(val labelRes: Int) {
    NAME(R.string.by_name), DATE_ASC(R.string.by_date_asc), DATE_DESC(R.string.by_date_desc), USAGE(
        R.string.by_usage
    )
}