package ua.androbene.tripsandfuel.user_interface.trips

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import ua.androbene.tripsandfuel.R
import ua.androbene.tripsandfuel.databinding.FragmentTripsBinding
import ua.androbene.tripsandfuel.user_interface.base.BaseFragment
import ua.androbene.tripsandfuel.user_interface.base.invisible
import ua.androbene.tripsandfuel.user_interface.base.visible
import ua.androbene.tripsandfuel.user_interface.settings.SettingsFragment

/**
 * A fragment that shows all saved in database trips as items in a scrolling list.
 */
@Suppress("DEPRECATION")
class TripsFragment : BaseFragment<FragmentTripsBinding>() {
    override fun inflateFrag(inflater: LayoutInflater) =
        FragmentTripsBinding.inflate(layoutInflater)

    private var tripAdapter = TripAdapter()
    private val tripViewModel: TripsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecycler()
        setListeners()


        tripViewModel.trips.observe(viewLifecycleOwner) {
            tripAdapter.setData(it)
        }
        tripViewModel.isLoading.value = false
        tripViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.dataGroup.invisible()
                binding.pbLoading.visible()
            } else {
                binding.dataGroup.visible()
                binding.pbLoading.invisible()
            }
        }
    }

    private fun setRecycler() = with(binding) {
        recycler.adapter = tripAdapter
        tripAdapter.setData(tripViewModel.trips.value)
    }

    private fun setListeners() = with(binding) {
        addFAB.setOnClickListener {
            multiClickGuard {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, AddTripFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack("null").commit()
            }
        }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                tripAdapter.filter.filter(newText) // starts an asynchronous filtering operation
                return true
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (binding.search.query.isEmpty()) binding.search.visibility = View.GONE
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            TripAdapter.MENU_DEL_ID -> {
                tripViewModel.delTripByID(tripAdapter.contextSelectedItemID)
            }

            TripAdapter.MENU_EDIT_ID -> {
                tripViewModel.editTrip(tripAdapter.contextSelectedItemID)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, EditTripFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack("null")
                    .commit()
            }

            TripAdapter.MENU_CLEAR_ID -> tripViewModel.delAll()
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_trips, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_search -> {
                if (binding.search.visibility == View.GONE)
                    binding.search.visibility = View.VISIBLE
                else {
                    binding.search.visibility = View.GONE
                    binding.search.setQuery("", false)
                }
            }

            R.id.item_settings -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, SettingsFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack("null")
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}