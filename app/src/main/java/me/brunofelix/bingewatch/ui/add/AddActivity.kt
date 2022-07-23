package me.brunofelix.bingewatch.ui.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.brunofelix.bingewatch.R
import me.brunofelix.bingewatch.data.AppDatabase
import me.brunofelix.bingewatch.data.Series
import me.brunofelix.bingewatch.data.SeriesRepositoryImpl
import me.brunofelix.bingewatch.databinding.ActivityAddBinding
import me.brunofelix.bingewatch.extension.toast
import me.brunofelix.bingewatch.util.*
import java.util.*

class AddActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: ActivityAddBinding
    private lateinit var viewModel: AddViewModel
    private var currentSeries: Series? = null

    private var startDate: Long = 0L
    private var startDateFormatted: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        initObjects()
        observeData()
    }

    private fun initUI() {
        setTheme(R.style.ThemeBingeWatch)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startDate = System.currentTimeMillis()
        startDateFormatted = convertFromTimestamp(System.currentTimeMillis())
        binding.tvStartDate.text = startDateFormatted

        binding.toolbar.inflateMenu(R.menu.menu_add)
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_left)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_start_date -> {
                    showDatePicker()
                    true
                }
                R.id.action_add -> {
                    submitForm()
                    true
                }
                else -> false
            }
        }

        binding.btnSave.setOnClickListener {
            submitForm()
        }
    }

    private fun initObjects() {
        val db = AppDatabase.getInstance(this)
        val dao = db.seriesDao()
        val repository = SeriesRepositoryImpl(dao)
        val resources = ResourcesProvider(this)
        val factory = AddViewModelFactory(repository, Dispatchers.IO, resources)

        currentSeries = intent.getParcelableExtra(getString(R.string.key_series_obj))

        viewModel = ViewModelProvider(this, factory).get(AddViewModel::class.java)

        validCurrentSeries()
    }

    private fun submitForm() {
        val name = binding.etName.text.toString()

        if (name.isEmpty() || startDate == 0L) {
            toast(Constants.FORM_SUBMIT_ERROR)
            return
        }
        val duration = getDuration(startDate)
        val series = Series(0, name, startDate, System.currentTimeMillis(), duration)

        if (currentSeries != null) {
            viewModel.editSeries(
                Series(currentSeries?.id!!, name, startDate, currentSeries?.createdAt!!, duration)
            )
        } else {
            viewModel.addNewSeries(series)
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.liveData.observe(this@AddActivity) { uiState ->
                when (uiState) {
                    is AddViewModel.UiState.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is AddViewModel.UiState.Success -> {
                        binding.progressBar.isVisible = false
                        toast(uiState.successMsg)
                        onBackPressed()
                    }
                    is AddViewModel.UiState.Error -> {
                        binding.progressBar.isVisible = false
                        toast(uiState.errorMsg)
                    }
                }
            }
        }
    }

    private fun validCurrentSeries() {
        if (currentSeries != null) {
            binding.toolbar.title = "Edit series"
            binding.etName.setText(currentSeries?.name)

            startDate = currentSeries?.startDate!!
            startDateFormatted = convertFromTimestamp(currentSeries?.startDate!!)
            binding.tvStartDate.text = startDateFormatted
        }
    }

    private fun showDatePicker() {
        val now = Calendar.getInstance()
        val day = now.get(Calendar.DAY_OF_MONTH)
        val month = now.get(Calendar.MONTH)
        val year = now.get(Calendar.YEAR)

        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun showTimePicker() {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR)
        val minute = now.get(Calendar.MINUTE)

        TimePickerDialog(this, this, hour, minute, false).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val pickedDay = if (dayOfMonth < 10) "0${dayOfMonth}" else dayOfMonth.toString()
        val pickedMonth = if (month < 10) "0${month.plus(1)}" else month.plus(1).toString()
        val pickedYear = year.toString()

        startDateFormatted = "${pickedMonth}-${pickedDay}-${pickedYear}"

        showTimePicker()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val time = getTime(hourOfDay, minute)

        startDateFormatted = "$startDateFormatted $time"
        startDate = convertToTimestamp(startDateFormatted)
        binding.tvStartDate.text = startDateFormatted
    }
}