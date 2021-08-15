package ua.androbene.tripsandfuel.trips

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ua.androbene.tripsandfuel.DataLoadingFragment
import ua.androbene.tripsandfuel.R
import ua.androbene.tripsandfuel.SettingsFragment
import ua.androbene.tripsandfuel.databinding.TripsFragmentBinding

/**
 * A fragment that shows all saved in database trips as items in a scrolling list.
 */
class TripsFragment : Fragment() {

    private var _binding: TripsFragmentBinding? = null
    private val bind get() = _binding!!  // This property is only valid between onCreateView and onDestroyView

    private var adapter: TripAdapter = TripAdapter()
    private val tripViewModel: TripsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = TripsFragmentBinding.inflate(layoutInflater, container, false)
        val view = bind.root
        bind.recycler.adapter = adapter
        adapter.setData(tripViewModel.trips.value)

        bind.addFAB.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, AddTripFragment())
                .setReorderingAllowed(true)
                .addToBackStack("null").commit()
        }

        bind.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText) // starts an asynchronous filtering operation
                return true
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tripViewModel.trips.observe(viewLifecycleOwner, {
            adapter.setData(it)
        })
        tripViewModel.isLoading.value = false
        tripViewModel.isLoading.observe(viewLifecycleOwner, {
            if (it) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.trips_container, DataLoadingFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack("null")
                    .commit()
            } else {
                requireActivity().supportFragmentManager.popBackStack()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (bind.search.query.isEmpty()) bind.search.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            TripAdapter.MENU_DEL_ID -> {
                tripViewModel.delTripByID(adapter.contextSelectedItemID)
            }
            TripAdapter.MENU_EDIT_ID -> {
                tripViewModel.editTrip(adapter.contextSelectedItemID)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_trips, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_search -> {
                if (bind.search.visibility == View.GONE)
                    bind.search.visibility = View.VISIBLE
                else {
                    bind.search.visibility = View.GONE
                    bind.search.setQuery("", false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}