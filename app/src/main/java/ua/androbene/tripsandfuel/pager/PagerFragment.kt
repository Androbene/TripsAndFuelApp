package ua.androbene.tripsandfuel.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ua.androbene.tripsandfuel.R
import ua.androbene.tripsandfuel.report.ReportFragment
import ua.androbene.tripsandfuel.trips.TripsFragment

/**
 * A top level host fragment.
 */
class PagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.pager_fragment, container, false)
        val pager = view.findViewById<ViewPager2>(R.id.pager)
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
        return view
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