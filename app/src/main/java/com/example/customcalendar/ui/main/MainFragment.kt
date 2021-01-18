package com.example.customcalendar.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.example.customcalendar.*
import com.example.customcalendar.R.color
import com.example.customcalendar.databinding.Example1CalendarDayBinding
import com.example.customcalendar.databinding.MainFragmentBinding
import com.example.customcalendar.getColorCompat
import com.example.customcalendar.setTextColorRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.LocalDate
import java.time.YearMonth

class MainFragment : Fragment()  {

    private lateinit var binding: MainFragmentBinding
    private val selectedDates = mutableSetOf<LocalDate>()
    private val today = LocalDate.now()
    private val currentMonth = YearMonth.now()
    private val startMonth = currentMonth.minusMonths(0)
    private val endMonth = currentMonth.plusMonths(2)
    private val endDate = today.plusMonths(2)
    private val nextMonth = currentMonth.plusMonths(1)

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainFragmentBinding.bind(view)
        val daysOfWeek = daysOfWeekFromLocale()
        binding.legendLayout.root.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                setTextColorRes(color.dark_gray)
            }
        }

        //Text of Button
        binding.monthButtonOne.text = currentMonth.month.toString()
        binding.monthButtonTwo.text = currentMonth.plusMonths(1).month.toString()
        binding.monthButtonThree.text = currentMonth.plusMonths(2).month.toString()


        //Setting up calendar and setting it's scroll to current month
        binding.exOneCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.exOneCalendar.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val textView = Example1CalendarDayBinding.bind(view).exOneDayText

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDates.contains(day.date)) {
                            selectedDates.remove(day.date)
                        } else {
                            selectedDates.add(day.date)
                        }
                        binding.exOneCalendar.notifyDayChanged(day)
                    }
                }
            }
        }

        binding.exOneCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                textView.text = day.date.dayOfMonth.toString()

                //To disable all the dates before the current date and end date
                if (day.date.isBefore(today) || day.date.isAfter(endDate)) {
                    textView.alpha = 0.6f
                    textView.background = null
                } else {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        when {
                            selectedDates.contains(day.date) -> {
                                textView.setTextColorRes(color.black)
                                textView.setBackgroundResource(R.drawable.example_1_selected_bg)
                            }
                            today == day.date -> {
                                textView.setTextColorRes(color.white)
                                textView.setBackgroundResource(R.drawable.example_1_today_bg)
                            }
                            else -> {
                                textView.setTextColorRes(color.black)
                                textView.background = null
                            }
                        }
                    } else {
                        textView.makeInVisible()
                    }
                }

            }
        }

        binding.exOneCalendar.monthScrollListener = { month ->
            val title = nextMonth.month.toString()
            binding.monthButtonTwo.text = title
            binding.monthButtonTwo.setBackgroundColor(R.color.green)
        }


    }

    override fun onStart() {
        super.onStart()
        requireActivity().window.statusBarColor =
            requireContext().getColorCompat(color.example_1_bg_light)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.statusBarColor =
            requireContext().getColorCompat(color.colorPrimaryDark)
    }

}