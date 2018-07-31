package org.magicalwater.mgkotlin.mgDialogKt.dialog

/**
 * Created by magicalwater on 2018/1/26.
 */
typealias MGDialogType = Int

interface MGDialogParam {
    var delegate: MGDialogResultDelegateInUtils?

    //此值由 MGDialogUtils 進行操作, 上層 Dialog 操作無用
    var dialogType: MGDialogType
}