package com.farshadtahmasbi.motionrecyclerview

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

fun ImageView.load(@DrawableRes res: Int) {
    Glide.with(context).load(res).into(this)
}