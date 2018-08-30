package org.magicalwater.mgkotlin.mgDialogKt.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*


/**
 * Created by magicalwater on 2018/1/22.
 */
open class MGBaseDialog : Dialog {
    private var height: Int = 0
    private var width: Int = 0
    private var cancelTouchout: Boolean = false
    private var cancelDelegateMG: MGDialogResultDelegateInUtils? = null
    var dialogType: MGDialogType? = null

    var view: View? = null


    private constructor(builder: Builder) : super(builder.context) {
        syncBuilder(builder)
    }

    private constructor(builder: Builder, resStyle: Int) : super(builder.context, resStyle) {
        syncBuilder(builder)
    }

    private fun syncBuilder(builder: Builder) {
        height = builder.height
        width = builder.width
        cancelTouchout = builder.cancelTouchout
        cancelDelegateMG = builder.delegateMG
        dialogType = builder.type
        view = builder.view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(view)
        setCanceledOnTouchOutside(cancelTouchout)

        val win = window
        val lp = win.attributes
        //        lp.windowAnimations = R.style.dialog_anim;
        lp.gravity = Gravity.CENTER
        if (height > 0) lp.height = height
        if (width > 0) lp.width = width

        win.attributes = lp

        setOnKeyListener { _, i, _ ->
            if (i == android.view.KeyEvent.KEYCODE_BACK && cancelTouchout && isShowing && dialogType != null) {
                //如果點擊外部可取消, 回傳點選的callback
                cancelDelegateMG?.outsideCancel(this, dialogType!!)
            }
            return@setOnKeyListener true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (cancelTouchout && isShowing && dialogType != null) {
            //如果點擊外部可取消, 回傳點選的callback
            cancelDelegateMG?.outsideCancel(this, dialogType!!)
        }
        return super.onTouchEvent(event)
    }


    class Builder(context: Context) {
        var context: Context = context
        var height: Int = 0
        var width: Int = 0
        var cancelTouchout: Boolean = false
        var view: View? = null
        var resStyle = -1
        var delegateMG: MGDialogResultDelegateInUtils? = null
        var type: MGDialogType? = null

        fun view(resView: Int): Builder {
            view = LayoutInflater.from(context).inflate(resView, null, false)
            return this
        }

        fun view(view: View): Builder {
            this.view = view
            return this
        }

        fun height(value: Int): Builder {
            height = value
            return this
        }

        fun width(value: Int): Builder {
            width = value
            return this
        }

        fun style(resStyle: Int): Builder {
            this.resStyle = resStyle
            return this
        }

        fun cancelTouchout(enable: Boolean): Builder {
            cancelTouchout = enable
            return this
        }

        fun cancelCallback(delegateMG: MGDialogResultDelegateInUtils?, dialogType: MGDialogType?): Builder {
            this.delegateMG = delegateMG
            this.type = dialogType
            return this
        }

        fun build(): MGBaseDialog {
            return if (resStyle != -1) {
                MGBaseDialog(this, resStyle)
            } else {
                MGBaseDialog(this)
            }
        }

    }
}