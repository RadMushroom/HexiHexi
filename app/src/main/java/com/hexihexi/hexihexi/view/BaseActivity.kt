package com.hexihexi.hexihexi.view

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    private var loadingDialog : Dialog? = null

    protected abstract fun getContentView(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
    }

    protected fun showLoading() {
        loadingDialog?.let {
            if (it.isShowing) {
                return
            }
        }
        loadingDialog = ProgressDialog.show(this, "Processing", "Please wait...")
    }

    protected fun hideLoading() {
        loadingDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}