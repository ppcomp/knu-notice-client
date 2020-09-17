package com.ppcomp.knu.`object`.noticeData.dataSource

import android.annotation.SuppressLint
import androidx.paging.PageKeyedDataSource
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.`object`.noticeData.DataUtils.Companion.injectDataToNotices
import com.ppcomp.knu.`object`.noticeData.Notice
import com.ppcomp.knu.utils.PreferenceHelper
import com.ppcomp.knu.utils.RestApi
import java.lang.Exception

/**
 * 데이터 소스(네트워크 서버)로부터 데이터를 받아온다.
 * @author 정우
 */
class NoticeAllDataSource(
    private val restApi: RestApi,
    private val q: String,
    private val target: String=PreferenceHelper.get("Urls", "")!!)
    : PageKeyedDataSource<Int, Notice>() {

    @SuppressLint("CheckResult")
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Notice>) {
        val curr = 1
        restApi.getNoticeAll(q=q, target=target!!, page=curr)
            .subscribe ({ it ->
                if (it.next == null)
                    it.results?.let { noticeList ->
                        callback.onResult(injectDataToNotices(noticeList), null, null)
                    }
                else
                    it.results?.let { noticeList ->
                        callback.onResult(injectDataToNotices(noticeList), null, curr + 1)
                    }
            }, {
                GlobalApplication.isServerConnect = false
            })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Notice>) {
        // no-op
    }

    @SuppressLint("CheckResult")
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Notice>) {
        restApi.getNoticeAll(q=q, target=target!!, page=params.key)
            .subscribe ({ it ->
                if (it.next == null)
                    it.results?.let { noticeList ->
                        callback.onResult(injectDataToNotices(noticeList),null) }
                else
                    it.results?.let { noticeList ->
                        callback.onResult(injectDataToNotices(noticeList),params.key+1) }
            },{
                GlobalApplication.isServerConnect = false
            })
    }
}