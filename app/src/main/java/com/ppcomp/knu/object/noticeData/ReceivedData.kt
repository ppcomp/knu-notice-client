package com.ppcomp.knu.`object`.noticeData

import com.squareup.moshi.Json

/**
 * 서버로부터 수신된 데이터의 구조를 정의할 포맷.
 * @author 정우
 */
class ReceivedData {
    @Json(name = "count")
    var count: Int? = null

    @Json(name = "next")
    var next: String? = null

    @Json(name = "previous")
    var previous: String? = null

    @Json(name = "results")
    var results: List<Notice>? = null

}