package ua.androbene.tripsandfuel.user_interface.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ua.androbene.tripsandfuel.R
import ua.androbene.tripsandfuel.databinding.FragmentMainBinding
import ua.androbene.tripsandfuel.user_interface.base.BaseFragment
import ua.androbene.tripsandfuel.user_interface.report.ReportFragment
import ua.androbene.tripsandfuel.user_interface.trips.TripsFragment

class MainFragment : BaseFragment<FragmentMainBinding>() {
    override fun inflateFrag(inflater: LayoutInflater) = FragmentMainBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            pager.adapter = PagerAdapter(activity as FragmentActivity)
            pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            val tab = view.findViewById<TabLayout>(R.id.tab_layout)
            tab.tabMode = TabLayout.MODE_SCROLLABLE
            TabLayoutMediator(tab, pager) { _tab, pos ->
                when (pos) {
                    0 -> _tab.text = getString(R.string.all_trips)
                    1 -> _tab.text = getString(R.string.report)
                }
            }.attach()
        }
    }
}

class PagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TripsFragment()
            else -> ReportFragment()
        }
    }
}