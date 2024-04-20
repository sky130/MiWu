package com.github.miwu.widget.app

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.wear.widget.WearableLinearLayoutManager
import java.util.ArrayList
import kotlin.math.abs
import androidx.wear.widget.WearableRecyclerView as RV

class SmoothRecyclerView(context: Context, attr: AttributeSet) : RV(context, attr),
    SeslwBezelEventTimerListener {

    private var mSeslwBezelEventTimer: SeslwBezelEventTimer? = null
    private var mIsFastMode = false
    private var mDirection = 0
    private val f = resources.displayMetrics.heightPixels.toFloat()
    private var mSmoothScrollAnimator: SeslwBezelSmoothScrollAnimator? = null
    private val MIN_ROTARY_MOVEMENT = (0.08F * f).toInt()
    private val MAX_ROTARY_MOVEMENT = (f * 0.16F).toInt()
    private var mIsGenericScrollEvent = false
    private var mIsSupportDenseBezel = false
    private var mScroll = 0


    override fun onGenericMotionEvent(var1: MotionEvent): Boolean {
        return if (getLayoutHook() == null) {
            false
        } else if (getLayoutSuppressedHook()) {
            false
        } else {
            if (var1.action == 8) {
                var var2: Int
                var var3: Byte
                var var4: Float
                var var5: Float
                var var6: Float
                run run@{
                    this.mIsGenericScrollEvent = true
                    var2 = var1.source
                    var3 = 2
                    if (var2 and 2 != 0) {
                        var4 = if (getLayoutHook()!!.canScrollVertically()) {
                            -var1.getAxisValue(9)
                        } else {
                            0.0f
                        }
                        if (getLayoutHook()!!.canScrollHorizontally()) {
                            var5 = var1.getAxisValue(10)
                            var6 = 0.0f
                            return@run
                        }
                    } else {
                        if (var1.source and 4194304 != 0) {
                            if (!this.mIsSupportDenseBezel && var1.getAxisValue(10) != 0.0f) {
                                this.mIsSupportDenseBezel = true
                            }
                            if (this.mIsSupportDenseBezel) {
                                if (mSmoothScrollAnimator == null) {
                                    mSmoothScrollAnimator = SeslwBezelSmoothScrollAnimator(
                                        this, getLayoutHook()!!.canScrollHorizontally(), null
                                    )
                                }
                                var4 = -var1.getAxisValue(10) / 3.0f
                            } else {
                                var4 = var1.getAxisValue(26)
                            }
                            var6 = var1.getAxisValue(26)
                            if (getLayoutHook()!!.canScrollVertically()) {
                                var4 = -var4
                                var6 = -var6
                                var5 = 0.0f
                            } else if (getLayoutHook()!!.canScrollHorizontally()) {
                                if (getLayoutHook()!!.layoutDirection != 1) {
                                    var4 = -var4
                                }
                                if (canScrollHorizontally(var4.toInt())) {
                                    val var7 = StringBuilder()
                                    var7.append("haptic played by bezel. this - ")
                                    var7.append(this)
                                    Log.i("SeslwRecyclerView", var7.toString())
                                    this.performHapticFeedback(102)
                                }
                                var5 = var4
                                var4 = 0.0f
                            } else {
                                var4 = 0.0f
                                var5 = var4
                            }
                            return@run
                        }
                        var4 = 0.0f
                    }
                    var6 = 0.0f
                    var5 = 0.0f
                }
                if (!this.mIsSupportDenseBezel) {
                    var6 = var4
                }
                var var10: Float
                var2 = if ((var4 - 0.0f).also {
                        var10 = it
                    } == 0.0f) 0 else if (var10 < 0.0f) -1 else 1
                if (var2 != 0 || var5 != 0.0f) {
                    if (var2 == 0) {
                        var3 = 1
                    }
                    this.startNestedScroll(var3.toInt(), ViewCompat.TYPE_NON_TOUCH)
                    if (!this.dispatchNestedPreScroll(
                            (getScaledHorizontalScrollFactorHook() * var5).toInt(),
                            (getScaledVerticalScrollFactorHook() * var4).toInt(),
                            null as IntArray?,
                            null as IntArray?,
                            ViewCompat.TYPE_NON_TOUCH
                        )
                    ) {
                        nestedScrollByInternal(
                            (var5 * getScaledHorizontalScrollFactorHook()).toInt(),
                            (var4 * getScaledVerticalScrollFactorHook()).toInt(),
                            var1,
                            1
                        )
                    }
                }
            }
            false
        }
    }

    private fun nestedScrollByInternal(var1: Int, var2: Int, var3: MotionEvent?, var4: Int) {
        var var1 = var1
        var var2 = var2
        val var5 = getLayoutHook()
        if (var5 == null) {
            Log.e(
                "SeslwRecyclerView",
                "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument."
            )

        } else if (!getFieldHook<Boolean>("mLayoutSuppressed")!!) {
            var var6 = getFieldHook<IntArray>("mReusableIntPair")!!
            val var7: Byte = 0
            var6[0] = 0
            var6[1] = 0
            val var8 = getLayoutHook()!!.canScrollHorizontally()
            val var9 = getLayoutHook()!!.canScrollVertically()
            var var10: Int = if (var9) {
                var8.toInt() or 2
            } else {
                var8.toInt()
            }
            val var11: Float = var3?.y ?: (this.height.toFloat() / 2.0f)
            val var12: Float = var3?.x ?: (this.width.toFloat() / 2.0f)
            var var13 = var1 - releaseHorizontalGlowHook(var1, var11)
            var var14 = var2 - releaseVerticalGlowHook(var2, var12)
            this.startNestedScroll(var10, var4)
            var10 = if (var8.toInt() != 0) {
                var13
            } else {
                0
            }
            val var15: Int = if (var9) {
                var14
            } else {
                0
            }
            var2 = var13
            var1 = var14
            if (this.dispatchNestedPreScroll(
                    var10,
                    var15,
                    getFieldHook<IntArray>("mReusableIntPair")!!,
                    getFieldHook<IntArray>("mScrollOffset")!!,
                    var4
                )
            ) {
                var6 = getFieldHook<IntArray>("mReusableIntPair")!!
                var2 = var13 - var6[0]
                var1 = var14 - var6[1]
            }
            if (mIsGenericScrollEvent && var3!!.action == 8) {
                if (!mIsSupportDenseBezel) {
                    if (scrollState == 2) {
                        if (var9) {
                            var13 = getViewFlingerOverScrollerHook().finalY
                            var14 = getViewFlingerOverScrollerHook().currY
                        } else {
                            var13 = getViewFlingerOverScrollerHook().finalX
                            var14 = getViewFlingerOverScrollerHook().currX
                        }
                        var13 -= var14
                    } else {
                        var13 = 0
                    }
                    if (var9) {
                        this.smoothScrollBy(0, var13 + var1)
                    } else {
                        this.smoothScrollBy(var13 + var2, 0)
                    }
                } else if (var9) {
                    smoothScrollByHardBezel((var1 / abs(var1.toDouble())).toInt())
                } else {
                    smoothScrollByHardBezel((var2 / abs(var2.toDouble())).toInt())
                }
            } else {
                var13 = if (var8.toInt() != 0) {
                    var2
                } else {
                    0
                }
                var14 = var7.toInt()
                if (var9) {
                    var14 = var1
                }
                scrollByInternal(var13, var14, var3, var4)
            }
            if (var2 != 0 || var1 != 0) {
                getGapWorkMethodHook(var2, var1)
            }
            this.stopNestedScroll(var4)
        }
    }

    private fun getGapWorkMethodHook(prefetchDx: Int, prefetchDy: Int) {
        RecyclerView::class.java.getDeclaredField("mGapWorker").apply {
            isAccessible = true
        }.get(this)?.let {
            it::class.java.getDeclaredMethod(
                "postFromTraversal",
                RecyclerView::class.java,
                Int::class.java,
                Int::class.java
            ).apply {
                isAccessible = true
            }.invoke(it, this, prefetchDx, prefetchDy)
        }
    }

    private fun getAdapterHook() = RecyclerView::class.java.getDeclaredField("mAdapter").apply {
        isAccessible = true
    }.get(this) as Adapter<*>?

    private fun consumePendingUpdateOperationsHook() {
        RecyclerView::class.java.getDeclaredMethod("consumePendingUpdateOperations").apply {
            isAccessible = true
        }.invoke(this)
    }

    fun scrollByInternal(var1: Int, var2: Int, var3: MotionEvent?, var4: Int): Boolean {
        var var1 = var1
        consumePendingUpdateOperationsHook()
        val var5: Adapter<*>? = getAdapterHook()
        val var6 = true
        var var7: Int
        var var8: Int
        val var9: Int
        val var10: Int
        var var11: Int
        var var16: IntArray
        if (var5 != null) {
            var16 = getFieldHook<IntArray>("mReusableIntPair")!!
            var16[0] = 0
            var16[1] = 0
            RecyclerView::class.java.getDeclaredMethod(
                "scrollStep",
                Int::class.java,
                Int::class.java,
                IntArray::class.java
            ).apply {
                isAccessible = true
            }.invoke(this, var1, var2, var16)
            var16 = getFieldHook<IntArray>("mReusableIntPair")!!
            var7 = var16[0]
            var8 = var16[1]
            var9 = var8
            var10 = var7
            var7 = var1 - var7
            var8 = var2 - var8
        } else {
            var11 = 0
            var10 = var11
            var8 = var11
            var7 = var11
            var9 = var11
        }
        if (getFieldHook<ArrayList<ItemDecoration>>("mItemDecorations")!!.isNotEmpty()) {
            this.invalidate()
        }
        var16 = getFieldHook<IntArray>("mReusableIntPair")!!
        var16[0] = 0
        var16[1] = 0
        this.dispatchNestedScroll(
            var10,
            var9,
            var7,
            var8,
            getFieldHook<IntArray>("mScrollOffset")!!,
            var4,
            var16
        )
        var16 = getFieldHook<IntArray>("mReusableIntPair")!!
        var11 = var7 - var16[0]
        val var12 = var8 - var16[1]
        val var15: Boolean = !(var16[0] == 0 && var16[1] == 0)
        val var18: Boolean = !(mIsGenericScrollEvent && var12 < 0)
        val var17: Boolean = var3 != null && MotionEventCompat.isFromSource(var3, 8194)
        if (var18 && this.dispatchNestedScroll(
                var10,
                var9,
                var11,
                var12,
                getFieldHook<IntArray>("mScrollOffset")!!,
                mIsGenericScrollEvent.toInt()
            )
        ) {
            var8 = getFieldHook<Int>("mLastTouchX")!!
            var16 = getFieldHook<IntArray>("mScrollOffset")!!
            setFieldHook("mLastTouchX", var8 - var16[0])
            setFieldHook("mLastTouchY", getFieldHook<Int>("mLastTouchY")!! - var16[1])
            if (var3 != null && var16[1] == 0 && !var17 && this.overScrollMode != 2) {
                RecyclerView::class.java.getDeclaredMethod(
                    "pullGlows",
                    Float::class.java,
                    Float::class.java,
                    Float::class.java,
                    Float::class.java
                ).apply {
                    isAccessible = true
                }.invoke(this, var3.x, var11.toFloat(), var3.y, var12.toFloat())
                RecyclerView::class.java.getDeclaredMethod(
                    "considerReleasingGlowsOnScroll",
                    Int::class.java,
                    Int::class.java
                ).apply {
                    isAccessible = true
                }.invoke(this, var1, var2)
            }
            var16 = getFieldHook<IntArray>("mNestedOffsets")!!
            var1 = var16[0]
            val var14 = getFieldHook<IntArray>("mScrollOffset")!!
            var16[0] = var1 + var14[0]
            var16[1] += var14[1]
        } else if (this.overScrollMode != 2) {
            if (var3 != null && !var17) {
                RecyclerView::class.java.getDeclaredMethod(
                    "pullGlows",
                    Float::class.java,
                    Float::class.java,
                    Float::class.java,
                    Float::class.java
                ).apply {
                    isAccessible = true
                }.invoke(this, var3.x, var11.toFloat(), var3.y, var12.toFloat())
            }
//            considerReleasingGlowsOnScroll(var1, var2)
            RecyclerView::class.java.getDeclaredMethod(
                "considerReleasingGlowsOnScroll",
                Int::class.java,
                Int::class.java
            ).apply {
                isAccessible = true
            }.invoke(this, var1, var2)
        }
        if (var10 != 0 || var9 != 0) {
//            dispatchOnScrolled(var10, var9)
            RecyclerView::class.java.getDeclaredMethod(
                "dispatchOnScrolled",
                Int::class.java,
                Int::class.java
            ).apply {
                isAccessible = true
            }.invoke(this, var10, var9)
        }
        if (!this.awakenScrollBars()) {
            this.invalidate()
        }
        if (getLayoutHook()!! is StaggeredGridLayoutManager && (!canScrollVertically(-1) || !canScrollVertically(
                1
            ))
        ) {
            getLayoutHook()!!.onScrollStateChanged(0)
        }
        var var13 = var6
        if (!var15) {
            var13 = var6
            if (var10 == 0) {
                var13 = if (var9 != 0) {
                    var6
                } else {
                    false
                }
            }
        }
        return var13
    }

    private fun getViewFlingerOverScrollerHook() =
        RecyclerView::class.java.getDeclaredField("mViewFlinger").apply {
            isAccessible = true
        }.get(this)!!.let {
            it::class.java.getDeclaredField("mOverScroller").apply {
                isAccessible = true
            }.get(it) as OverScroller
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getFieldHook(field: String) =
        RecyclerView::class.java.getDeclaredField(field).apply {
            isAccessible = true
        }.get(this) as T?

    private fun setFieldHook(field: String, value: Any?) =
        RecyclerView::class.java.getDeclaredField(field).apply {
            isAccessible = true
        }.set(this, value)


    private fun releaseHorizontalGlowHook(deltaX: Int, y: Float) =
        RecyclerView::class.java.getDeclaredMethod(
            "releaseHorizontalGlow",
            Int::class.java,
            Float::class.java,
        ).apply {
            isAccessible = true
        }.invoke(this, deltaX, y) as Int

    private fun releaseVerticalGlowHook(deltaY: Int, x: Float) =
        RecyclerView::class.java.getDeclaredMethod(
            "releaseVerticalGlow",
            Int::class.java,
            Float::class.java,
        ).apply {
            isAccessible = true
        }.invoke(this, deltaY, x) as Int

    private fun getLayoutSuppressedHook() =
        RecyclerView::class.java.getDeclaredField("mLayoutSuppressed").apply {
            isAccessible = true
        }.getBoolean(this)

    private fun getLayoutHook() = RecyclerView::class.java.getDeclaredField("mLayout").apply {
        isAccessible = true
    }.get(this) as LayoutManager?


    private fun getScaledVerticalScrollFactorHook() =
        RecyclerView::class.java.getDeclaredField("mScaledVerticalScrollFactor").apply {
            isAccessible = true
        }.getFloat(this)

    private fun getScaledHorizontalScrollFactorHook() =
        RecyclerView::class.java.getDeclaredField("mScaledHorizontalScrollFactor").apply {
            isAccessible = true
        }.getFloat(this)

    private fun setScrollStateHook(param: Int) {
        RecyclerView::class.java.getDeclaredMethod("setScrollState", Int::class.java).apply {
            isAccessible = true
        }.invoke(this, param)
    }

    private fun smoothScrollByHardBezel(paramInt: Int) {
        var seslwBezelEventTimer = this.mSeslwBezelEventTimer
        if (seslwBezelEventTimer == null) {
            this.mDirection = paramInt
            seslwBezelEventTimer = SeslwBezelEventTimer(this)
            this.mSeslwBezelEventTimer = seslwBezelEventTimer
            seslwBezelEventTimer.start()
        } else {
            val j: Int = if (this.mDirection != paramInt) {
                seslwBezelEventTimer.start()
            } else {
                seslwBezelEventTimer.add()
            }
            if (j == 1) {
                this.mIsFastMode = true
            } else if (j == 2) {
                this.mIsFastMode = false
            }
        }
        var i: Int = MIN_ROTARY_MOVEMENT
        if (this.mIsFastMode) i = MAX_ROTARY_MOVEMENT
        this.mScroll += i * paramInt
        i = this.mDirection
        if (i != 0 && i != paramInt) {
            if (scrollState == 2) setScrollStateHook(0)
            resetSmoothScrollAnimator()
            this.mScroll = 0
        }
        this.mDirection = paramInt
        this.mSmoothScrollAnimator!!.start(0.0f, this.mScroll)
    }

    private fun resetSmoothScrollAnimator() {
        val seslwBezelSmoothScrollAnimator = this.mSmoothScrollAnimator
        if (seslwBezelSmoothScrollAnimator != null) {
            seslwBezelSmoothScrollAnimator.cancel()
            this.mSmoothScrollAnimator!!.setStartScroll(0)
            this.mSmoothScrollAnimator!!.setTargetScroll(0)
        }
    }


    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(WearableLinearLayoutManager(context, null))
    }

    class SeslwBezelEventTimer(paramSeslwBezelEventTimerListener: SeslwBezelEventTimerListener) :
        Animator.AnimatorListener {
        private val mBezelEventTimerListener: SeslwBezelEventTimerListener
        private val mDuration = 600
        private var mPrevTime = 0L
        private var mTimeCount = 0
        private var mTimeIndex = 0
        private val mTimeList = intArrayOf(0, 0, 0, 0, 0)
        private var mTimer: ValueAnimator? = null

        init {
            mBezelEventTimerListener = paramSeslwBezelEventTimerListener
        }

        private fun resetTimer() {
            var valueAnimator = mTimer
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners()
                mTimer!!.cancel()
                mTimer = null
            }
            valueAnimator = ValueAnimator.ofFloat(*floatArrayOf(0.0f, 0.0f))
            mTimer = valueAnimator
            valueAnimator.setDuration(mDuration.toLong())
            mTimer?.addListener(this)
            mTimer?.start()
        }

        fun add(): Int {
            resetTimer()
            val l1 = System.nanoTime()
            val l2 = mPrevTime
            mPrevTime = l1
            val f = 1.0f / ((l1 - l2) / 1.0E9 * 1000.0).toInt()
            val arrayOfInt = mTimeList
            var i = mTimeIndex
            arrayOfInt[i] = (f * 1000.0f).toInt()
            var bool = true
            mTimeIndex = (i + 1) % arrayOfInt.size
            i = mTimeCount + 1
            mTimeCount = i
            if (i > arrayOfInt.size) mTimeCount = arrayOfInt.size
            if (mTimeCount == 5) {
                i = 0
                var bool1 = true
                while (i < mTimeCount) {
                    if (mTimeList[i] < 70) {
                        bool = false
                    } else {
                        bool1 = false
                    }
                    i++
                }
                return if (bool) FAST_MODE else if (bool1) SLOW_MODE else CURRENT_SAVE
            }
            return SLOW_MODE
        }

        fun start(): Int {
            resetTimer()
            mPrevTime = System.nanoTime()
            mTimeIndex = 0
            mTimeCount = 0
            return SLOW_MODE
        }

        companion object {
            private const val CURRENT_SAVE = 3
            private const val FAST_MODE = 1
            private const val SLOW_MODE = 2
        }

        override fun onAnimationStart(p0: Animator) = Unit

        override fun onAnimationEnd(p0: Animator) = Unit

        override fun onAnimationCancel(p0: Animator) = Unit

        override fun onAnimationRepeat(p0: Animator) = Unit
    }

    override fun onBezelEventTimerFinished() {
        this.mSeslwBezelEventTimer = null
        this.mIsFastMode = false
    }


    class SeslwBezelSmoothScrollAnimator(
        paramRecyclerView: RecyclerView?,
        private val mIsHorizontal: Boolean,
        private val mSmoothScrollListener: SeslwBezelSmoothScrollListener?
    ) {
        private val SPRING_STIFFNESS_LIGHT = 200.0f
        private var mSpringAnimation: SpringAnimation? = null
        private var mSpringForce: SpringForce? = null
        private var SCROLL_X: FloatPropertyCompat<View> =
            object : FloatPropertyCompat<View?>("scrollX") {
                override fun getValue(param1View: View?): Float {
                    return 0.0f
                }

                override fun setValue(param1View: View?, param1Float: Float) {
                    if (param1View is SmoothRecyclerView) {
                        val recyclerView = param1View
                        val i = param1Float.toInt() - mPrevScrollOffset.toInt()
                        mPrevScrollOffset = (param1Float)
                        if (recyclerView.scrollState != 2) recyclerView.setScrollStateHook(2)
                        if (i != 0) if (!recyclerView.canScrollHorizontally(i)) {
                            recyclerView.smoothScrollBy(
                                i, 0, linearInterpolator as Interpolator, 5
                            )
                        } else {
                            recyclerView.scrollBy(i, 0)
                        }
                    }
                }
            } as FloatPropertyCompat<View>
        private var SCROLL_Y: FloatPropertyCompat<View> =
            object : FloatPropertyCompat<View?>("scrollY") {
                override fun getValue(param1View: View?): Float {
                    return 0.0f
                }

                override fun setValue(param1View: View?, param1Float: Float) {
                    if (param1View is SmoothRecyclerView) {
                        val recyclerView = param1View
                        val i = param1Float.toInt() - mPrevScrollOffset.toInt()
                        mPrevScrollOffset = (param1Float)
                        if (recyclerView.scrollState != 2) recyclerView.setScrollStateHook(2)
                        if (i != 0) if (!recyclerView.canScrollVertically(i)) {
                            recyclerView.smoothScrollBy(
                                0, i, linearInterpolator as Interpolator, 5
                            )
                        } else {
                            recyclerView.scrollBy(0, i)
                        }
                    }
                }
            } as FloatPropertyCompat<View>

        init {
            val springForce = SpringForce()
            mSpringForce = springForce
            springForce.setStiffness(SPRING_STIFFNESS_LIGHT)
            mSpringForce!!.setDampingRatio(1.0f)
            mSpringAnimation = if (mIsHorizontal) {
                SpringAnimation(paramRecyclerView, SCROLL_X)
            } else {
                SpringAnimation(paramRecyclerView, SCROLL_Y)
            }
            mSpringAnimation!!.addEndListener(OnAnimationEndListener { param1DynamicAnimation, param1Boolean, param1Float1, param1Float2 -> mSmoothScrollListener?.onAnimationEnd() })
        }

        fun cancel() {
            mSpringAnimation!!.cancel()
        }

        fun setStartScroll(paramInt: Int) {
            mPrevScrollOffset = 0.0f
            mSpringAnimation!!.setStartValue(paramInt.toFloat())
        }

        fun setTargetScroll(paramInt: Int) {
            mSpringForce!!.setFinalPosition(paramInt.toFloat())
        }

        fun start(paramFloat: Float, paramInt: Int): Boolean {
            val f = paramInt.toFloat()
            if (f == mSpringForce!!.finalPosition) return false
            mSpringForce!!.setFinalPosition(f)
            mSpringAnimation!!.setStartVelocity(paramFloat)
            mSpringAnimation!!.setSpring(mSpringForce)
            if (!mSpringAnimation!!.isRunning) mSpringAnimation!!.start()
            return true
        }


        companion object {
            private val linearInterpolator = LinearInterpolator()
            private var mPrevScrollOffset = 0.0f
        }

    }

}

private fun Boolean.toInt() = if (this) 1
else 0

interface SeslwBezelEventTimerListener {
    fun onBezelEventTimerFinished()
}


interface SeslwBezelSmoothScrollListener {
    fun onAnimationEnd()
}


