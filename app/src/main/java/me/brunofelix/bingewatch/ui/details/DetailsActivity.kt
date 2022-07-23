package me.brunofelix.bingewatch.ui.details

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import me.brunofelix.bingewatch.R
import me.brunofelix.bingewatch.data.AppDatabase
import me.brunofelix.bingewatch.data.Series
import me.brunofelix.bingewatch.data.SeriesRepositoryImpl
import me.brunofelix.bingewatch.databinding.ActivityDetailsBinding
import me.brunofelix.bingewatch.extension.changeAppTheme
import me.brunofelix.bingewatch.extension.toast
import me.brunofelix.bingewatch.ui.add.AddActivity
import me.brunofelix.bingewatch.util.ResourcesProvider
import me.brunofelix.bingewatch.util.convertFromTimestamp

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: DetailsViewModel
    private lateinit var currentSeries: Series
    private var seriesId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeAppTheme()
        initUI()
        initObjects()
    }

    override fun onResume() {
        super.onResume()
        viewModel.findSeriesById(seriesId)
        observeData()
    }

    private fun initUI() {
        setTheme(R.style.ThemeBingeWatch)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.inflateMenu(R.menu.menu_details)
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    val intent = Intent(this, AddActivity::class.java).apply {
                        putExtra(getString(R.string.key_series_obj), currentSeries)
                    }
                    startActivity(intent)
                    true
                }
                R.id.action_delete -> {
                    deleteDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun initObjects() {
        val db = AppDatabase.getInstance(this)
        val dao = db.seriesDao()
        val repository = SeriesRepositoryImpl(dao)
        val resources = ResourcesProvider(this)
        val factory = DetailsViewModelFactory(repository, Dispatchers.IO, resources)

        seriesId = intent.getLongExtra(getString(R.string.key_series_id), 0)

        viewModel = ViewModelProvider(this, factory).get(DetailsViewModel::class.java)
    }

    private fun deleteDialog() {
        val title = getString(R.string.title_dialog_delete)
        val message = String.format(getString(R.string.msg_dialog_delete), currentSeries.name)

        val builder = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.btn_dialog_yes) { dialog, id ->
                viewModel.deleteSeries(currentSeries)
            }
            setNegativeButton(R.string.btn_dialog_no) { dialog, id ->
                dialog.dismiss()
            }
        }
        builder.create().apply { show() }
    }

    private fun observeData() {
        lifecycleScope.launchWhenResumed {
            viewModel.liveData.observe(this@DetailsActivity, { uiState ->
                when (uiState) {
                    is DetailsViewModel.UiState.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is DetailsViewModel.UiState.Success -> {
                        binding.progressBar.isVisible = false
                        if (uiState.series != null) {
                            currentSeries = uiState.series

                            binding.tvName.text = currentSeries.name
                            binding.tvStartDate.text = convertFromTimestamp(currentSeries.startDate)
                            binding.tvCreatedAt.text = convertFromTimestamp(currentSeries.createdAt)
                        } else {
                            if (uiState.isDeleted) {
                                finish()
                            }
                        }
                    }
                    is DetailsViewModel.UiState.Error -> {
                        binding.progressBar.isVisible = false
                        toast(uiState.errorMsg)
                    }
                }
            })
        }
    }
}