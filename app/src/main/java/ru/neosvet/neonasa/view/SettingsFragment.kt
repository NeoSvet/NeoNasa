package ru.neosvet.neonasa.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.utils.SettingsUtils
import ru.neosvet.neonasa.utils.Theme


class SettingsFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    private lateinit var cStarSky: Chip
    private lateinit var cSnowflakes: Chip
    private val settings: SettingsUtils by lazy {
        SettingsUtils(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTheme(view)
    }

    private fun setTheme(root: View) {
        cStarSky = root.findViewById(R.id.cStarSky)
        cSnowflakes = root.findViewById(R.id.cSnowflakes)

        when (settings.getTheme()) {
            Theme.STAR_SKY -> cStarSky.isChecked = true
            Theme.SNOWFLAKES -> cSnowflakes.isChecked = true
        }

        cStarSky.setOnCheckedChangeListener(this)
        cSnowflakes.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(sender: CompoundButton?, value: Boolean) {
        if (sender == null) return

        if (!value) {
            sender.isChecked = true
            return
        }

        if (sender.equals(cStarSky) && value) {
            settings.setTheme(Theme.STAR_SKY)
        } else if (sender.equals(cSnowflakes) && value) {
            settings.setTheme(Theme.SNOWFLAKES)
        }
        reload()
    }

    private fun reload() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(MainActivity.OPEN_SETTINGS, true)
        startActivity(intent)
        requireActivity().finish()
    }
}