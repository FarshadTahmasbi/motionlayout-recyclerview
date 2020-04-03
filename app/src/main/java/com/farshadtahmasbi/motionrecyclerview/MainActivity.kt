package com.farshadtahmasbi.motionrecyclerview

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity(), CharacterAdapter.OnItemClickListener {

    private val itemTouchInterceptor = ItemTouchInterceptor()

    companion object {
        const val TAG = "MotionRecyclerView"
        const val SPAN_COUNT = 3
        const val NO_CLIP = 0;
        const val CLIP_TOP = 1;
        const val CLIP_BOTTOM = 2;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStatusBarTransparent()
        motion.setOnApplyWindowInsetsListener { v, insets ->
            toolbar.setPadding(0, insets.systemWindowInsetTop, 0, 0)
            insets
        }
        init()
    }

    private fun setStatusBarTransparent() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun init() {
        setupAdapter()
        rv_image.addOnItemTouchListener(itemTouchInterceptor)
    }

    private fun setupAdapter() {
        rv_image.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        rv_image.adapter = CharacterAdapter(DataSource().data, this)
    }

    override fun onItemClick(view: View, position: Int, data: CharacterAdapter.Data) {
        val viewGroup = view as? ViewGroup
        val img = viewGroup?.run { findViewById<AppCompatImageView>(R.id.img) } ?: return

        img_header.load(data.headerRes)
        text_title.text = getString(data.title)
        text_desc.text = getString(data.desc)

        val rect = Rect()
        img.getLocalVisibleRect(rect)
        val clipType = when {
            rect.height() == img.height -> NO_CLIP
            rect.top > 0 -> CLIP_TOP
            else -> CLIP_BOTTOM
        }

        Log.d(TAG, "clip type: $clipType")
        motion.offsetDescendantRectToMyCoords(img, rect)
        Log.d(TAG, "localVisibleRect in parent coord system: $rect")

        val set = motion.getConstraintSet(R.id.start)
        set.clear(R.id.img_motion)
        set.constrainWidth(R.id.img_motion, img.width)
        set.constrainHeight(R.id.img_motion, img.height)
        set.connect(
            R.id.img_motion,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            rect.left
        )
        when (clipType) {
            CLIP_TOP -> set.connect(
                R.id.img_motion,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM,
                motion.bottom - rect.bottom
            )
            else -> set.connect(
                R.id.img_motion,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                rect.top
            )
        }

        img.alpha = 0.0f
        img_motion.visibility = View.VISIBLE
        motion.img_motion.setImageDrawable(img.drawable)
        motion.apply {
            updateState(R.id.start, set)
            setTransition(R.id.start, R.id.end)
            setTransitionListener({ start, end ->
                itemTouchInterceptor.enable()
                if (start == startState) {
                    img.alpha = 0.0f
                    img_motion.alpha = 1.0f
                }
            },
                { state ->
                    if (state == startState) {
                        itemTouchInterceptor.disable()
                        img.alpha = 1.0f
                        img_motion.alpha = 0.0f
                    }
                })
            transitionToEnd()
        }
    }

    override fun onBackPressed() {
        if (motion.currentState != motion.startState) {
            motion.transitionToStart()
        } else super.onBackPressed()
    }
}