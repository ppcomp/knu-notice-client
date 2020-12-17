package com.ppcomp.knu.`object`.noticeData

import android.annotation.SuppressLint
import android.graphics.Color
import com.ppcomp.knu.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.math.abs

class DataUtils {
    companion object {
        private val colorMap: HashMap<String, Int> = HashMap()
        @SuppressLint("SimpleDateFormat")
        fun injectDataToNotices(noticeList: List<Notice>, highlightText: String?=null): List<Notice> {
            val nowDate: LocalDate = LocalDate.now()
            for (notice in noticeList) {
                notice.board = notice.id.split('-')[0]
                if (notice.isFixed) {
                    notice.fixedImage = R.drawable.notice_fixed_pin_icon
                }
                if (notice.author != null) {
                    notice.author = notice.author
                }
                if (notice.date != null) {
                    val sf = SimpleDateFormat("yyyy-MM-dd")
                    val diff = abs(
                        (sf.parse(nowDate.toString())!!.time - sf.parse(notice.date!!)!!.time) /
                                (24 * 60 * 60 * 1000)
                    )
                    if (diff <= 3) {
                        notice.image = R.drawable.notice_new_icon
                    }
                    val dateArr = notice.date!!.split("-")
                    val day = dateArr[2].split("T")
                    notice.date = "${dateArr[0]}년 ${dateArr[1]}월 ${day[0]}일"
                }
                if (!colorMap.containsKey(notice.board)) {
                    val hash = notice.board.hashCode()
                    val r = (hash and 0xFF0000 shr 16)
                    val g = (hash and 0x00FF00 shr 8)
                    val b = (hash and 0x0000FF)
                    val hsv = FloatArray(3)
                    Color.colorToHSV(Color.rgb(r,g,b), hsv)
                    hsv[1] += (100F-hsv[1])/5
                    val color = Color.HSVToColor(hsv)
                    notice.color = color
                    colorMap[notice.board] = color
                } else {
                    notice.color = colorMap[notice.board]!!
                }
                if (highlightText != null) { // 검색어(키워드)가 존재하는 경우
                    val dividedSearchQuery: List<String> =
                        if (highlightText.contains("+")) { // 키워드가 2개 이상인 경우
                            highlightText.split("+")
                        } else { // 단일 키워드인경우
                            listOf(highlightText)
                        }
                    for (i in dividedSearchQuery.indices) {
                        notice.title = notice.title!!.replace( // 강조
                            dividedSearchQuery[i],
                            "<u><strong>" + dividedSearchQuery[i] + "</strong></u>"
                        )
                    }

                }
            }
            return noticeList
        }
    }
}