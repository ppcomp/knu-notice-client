package com.ppcomp.knu.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.ppcomp.knu.R

class LoadingDialog
    constructor(context: Context) : Dialog(context){

    init {
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.dialog_loading)
    }
}