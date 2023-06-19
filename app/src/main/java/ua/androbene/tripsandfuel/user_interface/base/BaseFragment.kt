package ua.androbene.tripsandfuel.user_interface.base

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<out VB : ViewBinding> : Fragment() {
    private var _binding: VB? = null
    protected val binding: VB get() = checkNotNull(_binding) { "Non-initialized fragment binding" }

    abstract fun inflateFrag(inflater: LayoutInflater): VB

    override fun onCreateView(inflater: LayoutInflater, vg: ViewGroup?, sis: Bundle?): View {
        _binding = inflateFrag(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    protected fun addBackPressAction(onBackPressedCallback: OnBackPressedCallback.() -> Unit = {}) =
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            true,
            onBackPressedCallback
        )

    private var lastClickedTime = 0L
    protected fun multiClickGuard(preventTime: Long = 1000L, onFirstClickAction: () -> Unit) {
        if (SystemClock.elapsedRealtime() - lastClickedTime < preventTime) {
            return
        }
        lastClickedTime = SystemClock.elapsedRealtime()
        onFirstClickAction.invoke()
    }

    protected fun toast(@StringRes message: Int) = Toast.makeText(
        requireContext(),
        getString(message),
        Toast.LENGTH_SHORT
    ).show()

    protected fun toast( message: String) = Toast.makeText(
        requireContext(),
        message,
        Toast.LENGTH_SHORT
    ).show()

}