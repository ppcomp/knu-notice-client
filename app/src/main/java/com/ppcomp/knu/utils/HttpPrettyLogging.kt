package com.ppcomp.knu.utils

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.logging.HttpLoggingInterceptor

class HttpPrettyLogging : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        val logName = "OkHttp"
        if (!message.startsWith("{")) {
            Log.d(logName, message)
            return
        }

        try {
            val prettyPrintJson = GsonBuilder().setPrettyPrinting().create().toJson(
                JsonParser().parse(message)
            )
            largeLog(logName, prettyPrintJson)
        } catch (e: JsonSyntaxException) {
            Log.d(logName, message)
        }
    }

    companion object {
        private val MAX_LEN = 3000;

        private fun largeLog(tag: String, content: String) {
            if (content.length > 3000) {
                Log.d(tag, content.substring(0, MAX_LEN))
                largeLog(tag, content.substring(MAX_LEN))
            }
            else
                Log.d(tag, content)
        }
    }
}