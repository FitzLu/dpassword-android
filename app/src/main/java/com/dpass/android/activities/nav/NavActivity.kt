package com.dpass.android.activities.nav

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dpass.android.R
import com.dpass.android.activities.create.CreateActivity
import com.dpass.android.base.BaseAccountActivity
import com.dpass.android.utils.SupportActivityUtil
import com.dpass.android.widgets.CircleIndicator

class NavActivity: BaseAccountActivity() {

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_navi
    override var hasToolbar: Boolean = false

    private var mViewPager: ViewPager? = null
    private var mIndicator: CircleIndicator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.WHITE
    }

    override fun setUpViews(savedInstanceState: Bundle?) {
        findViewById<View>(R.id.tvStartUse)?.setOnClickListener {
            SupportActivityUtil.jumpActivity(this@NavActivity,
                    CreateActivity::class.java)
        }
        mViewPager = findViewById(R.id.viewPager)
        mIndicator = findViewById(R.id.indicator)
        mViewPager?.adapter = NavPagerAdapter(this)
        mIndicator?.transfer(0)
        mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mIndicator?.transfer(position)
                mIndicator?.postInvalidate()
            }

        })
    }

    override fun work(savedInstanceState: Bundle?) {

    }

    class NavPagerAdapter(context: Context): PagerAdapter(){

        private var mInnerViews: ArrayList<View> = arrayListOf()

        init {
            val view1 = LayoutInflater.from(context).inflate(R.layout.nav_pager_first, null, false)
            val view2 = LayoutInflater.from(context).inflate(R.layout.nav_pager_second, null, false)
            mInnerViews.add(view1)
            mInnerViews.add(view2)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int = mInnerViews.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(mInnerViews[position])
            return mInnerViews[position]
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(mInnerViews[position])
        }

    }
}