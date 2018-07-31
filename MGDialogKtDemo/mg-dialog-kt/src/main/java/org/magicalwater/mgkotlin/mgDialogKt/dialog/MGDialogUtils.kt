package org.magicalwater.mgkotlin.mgDialogKt.dialog

import android.content.Context
import android.view.View

typealias MGDialogResultHandler = (type: MGDialogButton, data: Any?) -> Boolean

class MGDialogUtils {

    companion object {

        //開啟中的dialog都會存在這裡
        private var dialogMap: MutableMap<MGDialogType, Pair<MGBaseDialog, MGDialogResultHandler?>> = mutableMapOf()

        //dialog返回資料回調此處
        private var resultCallback = object : MGDialogResultDelegateInUtils {
            override fun dialogResult(view: View, btn: MGDialogButton, data: Any?): Boolean {
                //將結果送回
                val isDialogNeedDismiss = sendResultBack(view, btn, data)
                if (isDialogNeedDismiss) {
                    dismiss(view)
                }
                //此回傳上層Dialog是否有關閉
                return isDialogNeedDismiss
            }

            override fun outsideCancel(dialog: MGBaseDialog, type: MGDialogType) {
                removeDialogInMap(type)
            }
        }

        //最終顯示dialog的地方
        fun showDialog(context: Context, attr: DialogAttr) {

            val builder = MGBaseDialog.Builder(context)
                    .view(attr.view)
                    .cancelTouchout(attr.cancelOut)
                    .cancelCallback(resultCallback, attr.dialogType)

            if (attr.width != null) builder.width(attr.width!!)
            if (attr.height != null) builder.height(attr.height!!)
            if (attr.view is MGDialogParam) {
                (attr.view as MGDialogParam).delegate = resultCallback
                (attr.view as MGDialogParam).dialogType = attr.dialogType
            }

            val dialog = builder.build()
            dialog.show()

            dialogMap[attr.dialogType] = Pair(dialog, attr.resultHandler)
        }

        //將資料送回, 回傳是否將Dialog關閉
        private fun sendResultBack(view: View, btn: MGDialogButton, data: Any?): Boolean {
            //首先藉由type得到 dialog, resultHandler, 但前提是view繼承自 DialogParam
            if (view !is MGDialogParam) return true
            val dialogType = view.dialogType

            //沒有找到對應, 或者沒有handler時, 直接回傳true代表需要關閉, 同時將此type對應的資料刪除
            val pair = dialogMap[dialogType]
            return pair?.second?.invoke(btn, data) ?: true
        }

        //檢查dialog是否已存在
        fun checkDialogIsExist(type: MGDialogType): Boolean {
            return dialogMap.containsKey(type)
        }

        //回傳是否關閉成功
        private fun dismiss(view: View): Boolean {
            if (view !is MGDialogParam) return false
            val dialogType = view.dialogType
            return dismiss(dialogType)
        }

        //回傳是否關閉成功
        fun dismiss(type: MGDialogType): Boolean {
            //如果找不到也返回
            val dialogPair = removeDialogInMap(type)
            return if (dialogPair != null) {
                dialogPair.first.dismiss()
                true
            } else {
                false
            }
            return true
        }

        private fun removeDialogInMap(type: MGDialogType): Pair<MGBaseDialog, MGDialogResultHandler?>? {
            if (!dialogMap.containsKey(type)) return null
            return dialogMap.remove(type)
        }

    }

    data class DialogAttr(var view: View,
                          var width: Int? = null,
                          var height: Int? = null,
                          var dialogType: MGDialogType,
                          var cancelOut: Boolean = false,
                          var resultHandler: MGDialogResultHandler? = null)
}