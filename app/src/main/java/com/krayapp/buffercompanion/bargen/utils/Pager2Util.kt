package com.krayapp.buffercompanion.bargen.utils

import android.content.res.Resources
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.krayapp.buffercompanion.bargen.R

abstract class TabbedFragment : Fragment() {

    abstract fun getTabName(resources: Resources): String
}

abstract class TabbedFragmentStateAdapter(val data: List<TabbedFragment>, fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    fun getTitle(position: Int, resources: Resources): String = data[position].getTabName(resources)
}

fun getSimpleFragmentAdapter(
    parentFragment: Fragment,
    vararg frags: Fragment
): FragmentStateAdapter = object : FragmentStateAdapter(parentFragment) {
    override fun getItemCount() = frags.size

    override fun createFragment(position: Int): Fragment = frags[position]
}

fun getSimpleTabbedFragmentAdapter(
    parentFragment: Fragment,
    vararg frags: TabbedFragment
): TabbedFragmentStateAdapter =
    object : TabbedFragmentStateAdapter(frags.toList(), parentFragment) {
        override fun getItemCount() = frags.size

        override fun createFragment(position: Int): TabbedFragment = frags[position]
    }

fun ViewPager2.bindWithTabs(
    tabLayout: TabLayout?,
    @LayoutRes customView: Int,
    @IdRes textViewId: Int
) {
    if (this.adapter !is TabbedFragmentStateAdapter)
        throw RuntimeException("Для bindWithTabs нужно использовать TabbedFragmentStateAdapter и TabbedFragment")

    tabLayout?.isVisible = true

    val clearTabPaddings: () -> Unit = {
        try {
            val tabLayoutClass = TabLayout::class.java
            val fieldNames =
                listOf("tabPaddingStart", "tabPaddingTop", "tabPaddingEnd", "tabPaddingBottom")

            for (fieldName in fieldNames) {
                val field = tabLayoutClass.getDeclaredField(fieldName)
                field.isAccessible = true
                field.setInt(tabLayout, -1)
            }

            tabLayout?.requestLayout()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    clearTabPaddings()
    val tabbedAdapter = this.adapter as TabbedFragmentStateAdapter
    tabLayout?.setSelectedTabIndicatorHeight(0)
    tabLayout?.setSelectedTabIndicatorColor(context.getColor(R.color.colorTransparent))
    if (tabLayout != null) {
        TabLayoutMediator(tabLayout, this) { tab, position ->
            val title = tabbedAdapter.getTitle(position, tabLayout.context.resources)
            tab.setCustomView(customView)

            val tv = tab.customView?.findViewById<TextView>(textViewId)
            tv?.text = title

        }.attach()
    }
}