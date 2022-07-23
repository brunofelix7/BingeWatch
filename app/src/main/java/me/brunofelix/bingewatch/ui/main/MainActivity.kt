package me.brunofelix.bingewatch.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import kotlinx.coroutines.launch
import me.brunofelix.bingewatch.R
import me.brunofelix.bingewatch.data.AppDatabase
import me.brunofelix.bingewatch.data.Series
import me.brunofelix.bingewatch.data.SeriesRepositoryImpl
import me.brunofelix.bingewatch.data.SeriesSortByEnum
import me.brunofelix.bingewatch.databinding.ActivityMainBinding
import me.brunofelix.bingewatch.extension.changeAppTheme
import me.brunofelix.bingewatch.extension.getSortBy
import me.brunofelix.bingewatch.extension.putSortBy
import me.brunofelix.bingewatch.extension.toast
import me.brunofelix.bingewatch.ui.add.AddActivity
import me.brunofelix.bingewatch.ui.details.DetailsActivity
import me.brunofelix.bingewatch.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity(), MainClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeAppTheme()
        initUI()
        initObjects()
        initAdapter()
        observeData()
    }

    private fun initUI() {
        setTheme(R.style.ThemeBingeWatch)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        binding.toolbar.inflateMenu(R.menu.menu_main)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_search -> {
                    //  TODO: search...
                    true
                }
                R.id.action_sort -> {
                    sortByDialog()
                    true
                }
                R.id.action_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        when(getSortBy()) {
            SeriesSortByEnum.NAME.value -> binding.tvSortedBy.text = "(By name)"
            SeriesSortByEnum.START_DATE.value -> binding.tvSortedBy.text = "(By start date)"
        }
    }

    private fun initObjects() {
        val db = AppDatabase.getInstance(this)
        val dao = db.seriesDao()
        val repository = SeriesRepositoryImpl(dao)
        val factory = MainViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    private fun initAdapter() {
        adapter = MainAdapter()
        adapter.listener = this
        adapter.context = this

        binding.rvSeries.adapter = adapter.withLoadStateHeaderAndFooter(
            header = ItemLoadStateAdapter { adapter.retry() },
            footer = ItemLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            binding.tvListTitle.text = "${adapter.itemCount} items"
            binding.rvSeries.isVisible = loadState.source.refresh is LoadState.NotLoading
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            binding.btnRetry.isVisible = loadState.source.refresh is LoadState.Error

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                toast("\uD83D\uDE28 Oops! ${it.error}")
            }
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.findAll(getSortBy()).collect {
                adapter.submitData(it)
            }
        }
    }

    private fun sortByDialog() {
        val builder = AlertDialog.Builder(this, R.style.MaterialThemeDialog)
        val itemsArray = arrayOf(
            SeriesSortByEnum.NAME.value,
            SeriesSortByEnum.START_DATE.value
        )
        var checkedItem = -1

        when(getSortBy()) {
            SeriesSortByEnum.NAME.value -> checkedItem = 0
            SeriesSortByEnum.START_DATE.value -> checkedItem = 1
        }

        builder.setTitle(resources.getString(R.string.title_dialog_sort_by))
        builder.setSingleChoiceItems(itemsArray, checkedItem) { _, which ->
            val item = itemsArray[which]
            putSortBy(item)
        }
        builder.setPositiveButton(resources.getString(R.string.btn_dialog_ok)) {_, _ ->
            initAdapter()
            observeData()
        }
        builder.setNegativeButton(resources.getString(R.string.btn_dialog_cancel)) { dialog, _ ->
            dialog.cancel()
        }
        builder.create()
        builder.show()
    }

    override fun onItemClick(series: Series) {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra(getString(R.string.key_series_id), series.id)
        }
        startActivity(intent)
    }
}