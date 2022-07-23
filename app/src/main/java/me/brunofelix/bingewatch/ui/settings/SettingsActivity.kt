package me.brunofelix.bingewatch.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import me.brunofelix.bingewatch.R
import me.brunofelix.bingewatch.databinding.ActivitySettingsBinding
import me.brunofelix.bingewatch.extension.changeAppTheme

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeAppTheme()
        initUI()
    }

    private fun initUI() {
        setTheme(R.style.ThemeBingeWatch)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        navToSettingsFragment()
    }

    private fun navToSettingsFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, SettingsFragment())
            .commit()
    }
}