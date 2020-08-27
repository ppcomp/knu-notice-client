package com.ppcomp.knu.`object`.NoticeData

import android.annotation.SuppressLint
import com.ppcomp.knu.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.math.abs

class DataUtils {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun injectDataToNotices(noticeList: List<Notice>): List<Notice> {
            val nowDate: LocalDate = LocalDate.now()
            for (notice in noticeList) {
                notice.board = notice.id.split('-')[0]
                if (notice.isFixed) {
                    notice.fixedImage = R.drawable.notice_fixed_pin_icon
                }
                if (notice.author != null) {
                    notice.author = "작성자: ${notice.author}"
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
                    notice.date = "게시일: ${dateArr[0]}년 ${dateArr[1]}월 ${day[0]}일"
                }
            }
            return noticeList
        }
    }
}