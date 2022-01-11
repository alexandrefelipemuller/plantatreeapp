package org.arboristasurbanos.treeplant.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import org.arboristasurbanos.treeplant.databinding.FragmentSettingsBinding
import org.arboristasurbanos.treeplant.ui.home.HomeViewModel


class SettingsFragment: Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val prefs: SharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context)
        _binding!!.switchImage.isChecked = prefs.getBoolean("SatImageType", true)

        _binding!!.switchImage.setOnCheckedChangeListener { compoundButton, b ->
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            val edit = prefs.edit()
            edit.putBoolean("SatImageType", compoundButton.isChecked)
            edit.commit()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}