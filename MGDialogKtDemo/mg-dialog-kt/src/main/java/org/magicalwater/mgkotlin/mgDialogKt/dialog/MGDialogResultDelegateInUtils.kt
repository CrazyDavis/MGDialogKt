package org.magicalwater.mgkotlin.mgDialogKt.dialog

import android.view.View

interface MGDialogResultDelegateInUtils {
    //回傳是否關閉Dialog
    fun dialogResult(view: View, btn: MGDialogButton, data: Any? = null): Boolean
    fun outsideCancel(dialog: MGBaseDialog, type: MGDialogType)
}
