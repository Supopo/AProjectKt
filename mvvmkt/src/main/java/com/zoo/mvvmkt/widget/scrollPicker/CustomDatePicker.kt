package com.zoo.mvvmkt.widget.scrollPicker

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.zoo.mvvmkt.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 时间选择器
 */

//val datePicker =
//CustomDatePicker(this, object : CustomDatePicker.ResultHandler {
//    override fun handle(time: String?) {
//
//    }
//}, "2023-01-01 00:00", "2024-12-31 00:00")
//datePicker!!.showSpecificTime(false)//不显示时间
//
//datePicker!!.show

class CustomDatePicker(
    context: Context?,
    resultHandler: ResultHandler?,
    startDate: String,
    endDate: String
) {
    private var showHM //是否显示时分
            = false

    /**
     * 定义结果回调接口
     */
    interface ResultHandler {
        fun handle(time: String?)
    }

    enum class SCROLL_TYPE(var value: Int) {
        HOUR(1), MINUTE(2);
    }

    private var scrollUnits = SCROLL_TYPE.HOUR.value + SCROLL_TYPE.MINUTE.value
    private var handler: ResultHandler? = null
    private var context: Context? = null
    private var canAccess = false
    private var datePickerDialog: Dialog? = null
    private var year_pv: ScrollPickerView? = null
    private var month_pv: ScrollPickerView? = null
    private var day_pv: ScrollPickerView? = null
    private var hour_pv: ScrollPickerView? = null
    private var minute_pv: ScrollPickerView? = null
    private var year: ArrayList<String>? = null
    private var month: ArrayList<String>? = null
    private var day: ArrayList<String>? = null
    private var hour: ArrayList<String>? = null
    private var minute: ArrayList<String>? = null
    private var startYear = 0
    private var startMonth = 0
    private var startDay = 0
    private var startHour = 0
    private var startMinute = 0
    private var endYear = 0
    private var endMonth = 0
    private var endDay = 0
    private var endHour = 0
    private var endMinute = 0
    private var spanYear = false
    private var spanMon = false
    private var spanDay = false
    private var spanHour = false
    private var spanMin = false
    private var selectedCalender: Calendar? = null
    private var startCalendar: Calendar? = null
    private var endCalendar: Calendar? = null
    private var tv_cancle: TextView? = null
    private var tv_select: TextView? = null
    private var hour_text: TextView? = null
    private var minute_text: TextView? = null

    //    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    //    //获取当前时间
    //    String now = sdf.format(new Date());
    //    传入时间格式为 "yyyy-MM-dd HH:mm"
    //    mDatePicker.showSpecificTime(false);//不显示时分
    init {
        if (isValidDate(startDate, "yyyy-MM-dd HH:mm") && isValidDate(
                endDate,
                "yyyy-MM-dd HH:mm"
            )
        ) {
            canAccess = true
            this.context = context
            handler = resultHandler
            selectedCalender = Calendar.getInstance()
            startCalendar = Calendar.getInstance()
            endCalendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
            try {
                startCalendar!!.setTime(sdf.parse(startDate))
                endCalendar!!.setTime(sdf.parse(endDate))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            initDialog()
            initView()
        }
    }

    private fun initDialog() {
        if (datePickerDialog == null) {
            datePickerDialog = Dialog(context!!, R.style.date_picker)
            datePickerDialog!!.setCancelable(false)
            datePickerDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            datePickerDialog!!.setContentView(R.layout.picker_custom_date)
            val window = datePickerDialog!!.window
            window!!.setGravity(Gravity.BOTTOM)
            val manager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            manager.defaultDisplay.getMetrics(dm)
            val lp = window.attributes
            lp.width = dm.widthPixels
            window.attributes = lp
        }
    }

    private fun initView() {
        year_pv = datePickerDialog!!.findViewById<View>(R.id.year_pv) as ScrollPickerView
        month_pv = datePickerDialog!!.findViewById<View>(R.id.month_pv) as ScrollPickerView
        day_pv = datePickerDialog!!.findViewById<View>(R.id.day_pv) as ScrollPickerView
        hour_pv = datePickerDialog!!.findViewById<View>(R.id.hour_pv) as ScrollPickerView
        minute_pv = datePickerDialog!!.findViewById<View>(R.id.minute_pv) as ScrollPickerView
        tv_cancle = datePickerDialog!!.findViewById<View>(R.id.tv_cancle) as TextView
        tv_select = datePickerDialog!!.findViewById<View>(R.id.tv_select) as TextView
        hour_text = datePickerDialog!!.findViewById<View>(R.id.hour_text) as TextView
        minute_text = datePickerDialog!!.findViewById<View>(R.id.minute_text) as TextView
        tv_cancle!!.setOnClickListener { datePickerDialog!!.dismiss() }
        tv_select!!.setOnClickListener {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
            val time = sdf.format(selectedCalender!!.time)
            if (showHM) {
                handler!!.handle(time)
            } else {
                handler!!.handle(time.substring(0, 10))
            }
            datePickerDialog!!.dismiss()
        }
    }

    private fun initParameter() {
        startYear = startCalendar!![Calendar.YEAR]
        startMonth = startCalendar!![Calendar.MONTH] + 1
        startDay = startCalendar!![Calendar.DAY_OF_MONTH]
        startHour = startCalendar!![Calendar.HOUR_OF_DAY]
        startMinute = startCalendar!![Calendar.MINUTE]
        endYear = endCalendar!![Calendar.YEAR]
        //        endMonth = endCalendar.get(Calendar.MONTH) + 1; 如果是这行只显示本月之前月份
        endMonth = 12
        endDay = endCalendar!![Calendar.DAY_OF_MONTH]
        endHour = endCalendar!![Calendar.HOUR_OF_DAY]
        endMinute = endCalendar!![Calendar.MINUTE]
        spanYear = startYear != endYear
        spanMon = !spanYear && startMonth != endMonth
        spanDay = !spanMon && startDay != endDay
        spanHour = !spanDay && startHour != endHour
        spanMin = !spanHour && startMinute != endMinute
        selectedCalender!!.time = startCalendar!!.time
    }

    private fun initTimer() {
        initArrayList()
        if (spanYear) {
            for (i in startYear..endYear) {
                year!!.add(i.toString())
            }
            for (i in startMonth..MAX_MONTH) {
                month!!.add(formatTimeUnit(i))
            }
            for (i in startDay..startCalendar!!.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                day!!.add(formatTimeUnit(i))
            }
            if (scrollUnits and SCROLL_TYPE.HOUR.value != SCROLL_TYPE.HOUR.value) {
                hour!!.add(formatTimeUnit(startHour))
            } else {
                for (i in startHour..MAX_HOUR) {
                    hour!!.add(formatTimeUnit(i))
                }
            }
            if (scrollUnits and SCROLL_TYPE.MINUTE.value != SCROLL_TYPE.MINUTE.value) {
                minute!!.add(formatTimeUnit(startMinute))
            } else {
                for (i in startMinute..MAX_MINUTE) {
                    minute!!.add(formatTimeUnit(i))
                }
            }
        } else if (spanMon) {
            year!!.add(startYear.toString())
            for (i in startMonth..endMonth) {
                month!!.add(formatTimeUnit(i))
            }
            for (i in startDay..startCalendar!!.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                day!!.add(formatTimeUnit(i))
            }
            if (scrollUnits and SCROLL_TYPE.HOUR.value != SCROLL_TYPE.HOUR.value) {
                hour!!.add(formatTimeUnit(startHour))
            } else {
                for (i in startHour..MAX_HOUR) {
                    hour!!.add(formatTimeUnit(i))
                }
            }
            if (scrollUnits and SCROLL_TYPE.MINUTE.value != SCROLL_TYPE.MINUTE.value) {
                minute!!.add(formatTimeUnit(startMinute))
            } else {
                for (i in startMinute..MAX_MINUTE) {
                    minute!!.add(formatTimeUnit(i))
                }
            }
        } else if (spanDay) {
            year!!.add(startYear.toString())
            month!!.add(formatTimeUnit(startMonth))
            for (i in startDay..endDay) {
                day!!.add(formatTimeUnit(i))
            }
            if (scrollUnits and SCROLL_TYPE.HOUR.value != SCROLL_TYPE.HOUR.value) {
                hour!!.add(formatTimeUnit(startHour))
            } else {
                for (i in startHour..MAX_HOUR) {
                    hour!!.add(formatTimeUnit(i))
                }
            }
            if (scrollUnits and SCROLL_TYPE.MINUTE.value != SCROLL_TYPE.MINUTE.value) {
                minute!!.add(formatTimeUnit(startMinute))
            } else {
                for (i in startMinute..MAX_MINUTE) {
                    minute!!.add(formatTimeUnit(i))
                }
            }
        } else if (spanHour) {
            year!!.add(startYear.toString())
            month!!.add(formatTimeUnit(startMonth))
            day!!.add(formatTimeUnit(startDay))
            if (scrollUnits and SCROLL_TYPE.HOUR.value != SCROLL_TYPE.HOUR.value) {
                hour!!.add(formatTimeUnit(startHour))
            } else {
                for (i in startHour..endHour) {
                    hour!!.add(formatTimeUnit(i))
                }
            }
            if (scrollUnits and SCROLL_TYPE.MINUTE.value != SCROLL_TYPE.MINUTE.value) {
                minute!!.add(formatTimeUnit(startMinute))
            } else {
                for (i in startMinute..MAX_MINUTE) {
                    minute!!.add(formatTimeUnit(i))
                }
            }
        } else if (spanMin) {
            year!!.add(startYear.toString())
            month!!.add(formatTimeUnit(startMonth))
            day!!.add(formatTimeUnit(startDay))
            hour!!.add(formatTimeUnit(startHour))
            if (scrollUnits and SCROLL_TYPE.MINUTE.value != SCROLL_TYPE.MINUTE.value) {
                minute!!.add(formatTimeUnit(startMinute))
            } else {
                for (i in startMinute..endMinute) {
                    minute!!.add(formatTimeUnit(i))
                }
            }
        }
        loadComponent()
    }

    /**
     * 将“0-9”转换为“00-09”
     */
    private fun formatTimeUnit(unit: Int): String {
        return if (unit < 10) "0$unit" else unit.toString()
    }

    private fun initArrayList() {
        if (year == null) year = ArrayList()
        if (month == null) month = ArrayList()
        if (day == null) day = ArrayList()
        if (hour == null) hour = ArrayList()
        if (minute == null) minute = ArrayList()
        year!!.clear()
        month!!.clear()
        day!!.clear()
        hour!!.clear()
        minute!!.clear()
    }

    private fun loadComponent() {
        year_pv!!.setData(year!!)
        month_pv!!.setData(month!!)
        day_pv!!.setData(day!!)
        hour_pv!!.setData(hour!!)
        minute_pv!!.setData(minute!!)
        year_pv!!.setSelected(0)
        month_pv!!.setSelected(0)
        day_pv!!.setSelected(0)
        hour_pv!!.setSelected(0)
        minute_pv!!.setSelected(0)
        executeScroll()
    }

    private fun addListener() {
        year_pv!!.setOnSelectListener(object : ScrollPickerView.onSelectListener {
            override fun onSelect(text: String?) {
                selectedCalender!![Calendar.YEAR] = text!!.toInt()
                monthChange()
            }
        })
        month_pv!!.setOnSelectListener(object : ScrollPickerView.onSelectListener {
            override fun onSelect(text: String?) {
                selectedCalender!![Calendar.DAY_OF_MONTH] = 1
                selectedCalender!![Calendar.MONTH] = text!!.toInt() - 1
                dayChange()
            }
        })
        day_pv!!.setOnSelectListener(object : ScrollPickerView.onSelectListener {
            override fun onSelect(text: String?) {
                selectedCalender!![Calendar.DAY_OF_MONTH] = text!!.toInt()
                hourChange()
            }
        })
        hour_pv!!.setOnSelectListener(object : ScrollPickerView.onSelectListener {
            override fun onSelect(text: String?) {
                selectedCalender!![Calendar.HOUR_OF_DAY] = text!!.toInt()
                minuteChange()
            }
        })
        minute_pv!!.setOnSelectListener(object : ScrollPickerView.onSelectListener {
            override fun onSelect(text: String?) {
                selectedCalender!![Calendar.MINUTE] = text!!.toInt()
            }
        })
    }

    private fun monthChange() {
        month!!.clear()
        val selectedYear = selectedCalender!![Calendar.YEAR]
        if (selectedYear == startYear) {
            for (i in startMonth..MAX_MONTH) {
                month!!.add(formatTimeUnit(i))
            }
        } else if (selectedYear == endYear) {
            for (i in 1..endMonth) {
                month!!.add(formatTimeUnit(i))
            }
        } else {
            for (i in 1..MAX_MONTH) {
                month!!.add(formatTimeUnit(i))
            }
        }
        selectedCalender!![Calendar.MONTH] = month!![0].toInt() - 1
        month_pv!!.setData(month!!)
        month_pv!!.setSelected(0)
        executeAnimator(month_pv)
        month_pv!!.postDelayed({ dayChange() }, 100)
    }

    private fun dayChange() {
        day!!.clear()
        val selectedYear = selectedCalender!![Calendar.YEAR]
        val selectedMonth = selectedCalender!![Calendar.MONTH] + 1
        if (selectedYear == startYear && selectedMonth == startMonth) {
            for (i in startDay..selectedCalender!!.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                day!!.add(formatTimeUnit(i))
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            for (i in 1..endDay) {
                day!!.add(formatTimeUnit(i))
            }
        } else {
            for (i in 1..selectedCalender!!.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                day!!.add(formatTimeUnit(i))
            }
        }
        selectedCalender!![Calendar.DAY_OF_MONTH] = day!![0].toInt()
        day_pv!!.setData(day!!)
        day_pv!!.setSelected(0)
        executeAnimator(day_pv)
        day_pv!!.postDelayed({ hourChange() }, 100)
    }

    private fun hourChange() {
        if (scrollUnits and SCROLL_TYPE.HOUR.value == SCROLL_TYPE.HOUR.value) {
            hour!!.clear()
            val selectedYear = selectedCalender!![Calendar.YEAR]
            val selectedMonth = selectedCalender!![Calendar.MONTH] + 1
            val selectedDay = selectedCalender!![Calendar.DAY_OF_MONTH]
            if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay) {
                for (i in startHour..MAX_HOUR) {
                    hour!!.add(formatTimeUnit(i))
                }
            } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay) {
                for (i in MIN_HOUR..endHour) {
                    hour!!.add(formatTimeUnit(i))
                }
            } else {
                for (i in MIN_HOUR..MAX_HOUR) {
                    hour!!.add(formatTimeUnit(i))
                }
            }
            selectedCalender!![Calendar.HOUR_OF_DAY] = hour!![0].toInt()
            hour_pv!!.setData(hour!!)
            hour_pv!!.setSelected(0)
            executeAnimator(hour_pv)
        }
        hour_pv!!.postDelayed({ minuteChange() }, 100)
    }

    private fun minuteChange() {
        if (scrollUnits and SCROLL_TYPE.MINUTE.value == SCROLL_TYPE.MINUTE.value) {
            minute!!.clear()
            val selectedYear = selectedCalender!![Calendar.YEAR]
            val selectedMonth = selectedCalender!![Calendar.MONTH] + 1
            val selectedDay = selectedCalender!![Calendar.DAY_OF_MONTH]
            val selectedHour = selectedCalender!![Calendar.HOUR_OF_DAY]
            if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour) {
                for (i in startMinute..MAX_MINUTE) {
                    minute!!.add(formatTimeUnit(i))
                }
            } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour) {
                for (i in MIN_MINUTE..endMinute) {
                    minute!!.add(formatTimeUnit(i))
                }
            } else {
                for (i in MIN_MINUTE..MAX_MINUTE) {
                    minute!!.add(formatTimeUnit(i))
                }
            }
            selectedCalender!![Calendar.MINUTE] = minute!![0].toInt()
            minute_pv!!.setData(minute!!)
            minute_pv!!.setSelected(0)
            executeAnimator(minute_pv)
        }
        executeScroll()
    }

    private fun executeAnimator(view: View?) {
        val pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f)
        val pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f)
        val pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f)
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(200).start()
    }

    private fun executeScroll() {
        year_pv!!.setCanScroll(year!!.size > 1)
        month_pv!!.setCanScroll(month!!.size > 1)
        day_pv!!.setCanScroll(day!!.size > 1)
        hour_pv!!.setCanScroll(hour!!.size > 1 && scrollUnits and SCROLL_TYPE.HOUR.value == SCROLL_TYPE.HOUR.value)
        minute_pv!!.setCanScroll(minute!!.size > 1 && scrollUnits and SCROLL_TYPE.MINUTE.value == SCROLL_TYPE.MINUTE.value)
    }

    private fun disScrollUnit(vararg scroll_types: SCROLL_TYPE): Int {
        if (scroll_types == null || scroll_types.size == 0) {
            scrollUnits = SCROLL_TYPE.HOUR.value + SCROLL_TYPE.MINUTE.value
        } else {
            for (scroll_type in scroll_types) {
                scrollUnits = scrollUnits xor scroll_type.value
            }
        }
        return scrollUnits
    }

    fun show(time: String) {
        if (canAccess) {
            if (isValidDate(time, "yyyy-MM-dd")) {
                if (startCalendar!!.time.time < endCalendar!!.time.time) {
                    canAccess = true
                    initParameter()
                    initTimer()
                    addListener()
                    setSelectedTime(time)
                    datePickerDialog!!.show()
                }
            } else {
                canAccess = false
            }
        }
    }

    /**
     * 设置日期控件是否显示时和分
     */
    fun showSpecificTime(show: Boolean) {
        showHM = show
        if (canAccess) {
            if (show) {
                disScrollUnit()
                hour_pv!!.visibility = View.VISIBLE
                hour_text!!.visibility = View.VISIBLE
                minute_pv!!.visibility = View.VISIBLE
                minute_text!!.visibility = View.VISIBLE
            } else {
                disScrollUnit(SCROLL_TYPE.HOUR, SCROLL_TYPE.MINUTE)
                hour_pv!!.visibility = View.GONE
                hour_text!!.visibility = View.GONE
                minute_pv!!.visibility = View.GONE
                minute_text!!.visibility = View.GONE
            }
        }
    }

    /**
     * 设置日期控件是否可以循环滚动
     */
    fun setIsLoop(isLoop: Boolean) {
        if (canAccess) {
            year_pv!!.setIsLoop(isLoop)
            month_pv!!.setIsLoop(isLoop)
            day_pv!!.setIsLoop(isLoop)
            hour_pv!!.setIsLoop(isLoop)
            minute_pv!!.setIsLoop(isLoop)
        }
    }

    /**
     * 设置日期控件默认选中的时间
     */
    fun setSelectedTime(time: String) {
        if (canAccess) {
            val str = time.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val dateStr = str[0].split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            year_pv!!.setSelected(dateStr[0])
            selectedCalender!![Calendar.YEAR] = dateStr[0].toInt()
            month!!.clear()
            val selectedYear = selectedCalender!![Calendar.YEAR]
            if (selectedYear == startYear) {
                for (i in startMonth..MAX_MONTH) {
                    month!!.add(formatTimeUnit(i))
                }
            } else if (selectedYear == endYear) {
                for (i in 1..endMonth) {
                    month!!.add(formatTimeUnit(i))
                }
            } else {
                for (i in 1..MAX_MONTH) {
                    month!!.add(formatTimeUnit(i))
                }
            }
            month_pv!!.setData(month!!)
            month_pv!!.setSelected(dateStr[1])
            selectedCalender!![Calendar.MONTH] = dateStr[1].toInt() - 1
            executeAnimator(month_pv)
            day!!.clear()
            val selectedMonth = selectedCalender!![Calendar.MONTH] + 1
            if (selectedYear == startYear && selectedMonth == startMonth) {
                for (i in startDay..selectedCalender!!.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    day!!.add(formatTimeUnit(i))
                }
            } else if (selectedYear == endYear && selectedMonth == endMonth) {
                for (i in 1..endDay) {
                    day!!.add(formatTimeUnit(i))
                }
            } else {
                for (i in 1..selectedCalender!!.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    day!!.add(formatTimeUnit(i))
                }
            }
            day_pv!!.setData(day!!)
            day_pv!!.setSelected(dateStr[2])
            selectedCalender!![Calendar.DAY_OF_MONTH] = dateStr[2].toInt()
            executeAnimator(day_pv)
            if (str.size == 2) {
                val timeStr =
                    str[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (scrollUnits and SCROLL_TYPE.HOUR.value == SCROLL_TYPE.HOUR.value) {
                    hour!!.clear()
                    val selectedDay = selectedCalender!![Calendar.DAY_OF_MONTH]
                    if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay) {
                        for (i in startHour..MAX_HOUR) {
                            hour!!.add(formatTimeUnit(i))
                        }
                    } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay) {
                        for (i in MIN_HOUR..endHour) {
                            hour!!.add(formatTimeUnit(i))
                        }
                    } else {
                        for (i in MIN_HOUR..MAX_HOUR) {
                            hour!!.add(formatTimeUnit(i))
                        }
                    }
                    hour_pv!!.setData(hour!!)
                    hour_pv!!.setSelected(timeStr[0])
                    selectedCalender!![Calendar.HOUR_OF_DAY] = timeStr[0].toInt()
                    executeAnimator(hour_pv)
                }
                if (scrollUnits and SCROLL_TYPE.MINUTE.value == SCROLL_TYPE.MINUTE.value) {
                    minute!!.clear()
                    val selectedDay = selectedCalender!![Calendar.DAY_OF_MONTH]
                    val selectedHour = selectedCalender!![Calendar.HOUR_OF_DAY]
                    if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour) {
                        for (i in startMinute..MAX_MINUTE) {
                            minute!!.add(formatTimeUnit(i))
                        }
                    } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour) {
                        for (i in MIN_MINUTE..endMinute) {
                            minute!!.add(formatTimeUnit(i))
                        }
                    } else {
                        for (i in MIN_MINUTE..MAX_MINUTE) {
                            minute!!.add(formatTimeUnit(i))
                        }
                    }
                    minute_pv!!.setData(minute!!)
                    minute_pv!!.setSelected(timeStr[1])
                    selectedCalender!![Calendar.MINUTE] = timeStr[1].toInt()
                    executeAnimator(minute_pv)
                }
            }
            executeScroll()
        }
    }

    /**
     * 验证字符串是否是一个合法的日期格式
     */
    private fun isValidDate(date: String, template: String): Boolean {
        var convertSuccess = true
        // 指定日期格式
        val format = SimpleDateFormat(template, Locale.CHINA)
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2015/02/29会被接受，并转换成2015/03/01
            format.isLenient = false
            format.parse(date)
        } catch (e: Exception) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false
        }
        return convertSuccess
    }

    companion object {
        private const val MAX_MINUTE = 59
        private const val MAX_HOUR = 23
        private const val MIN_MINUTE = 0
        private const val MIN_HOUR = 0
        private const val MAX_MONTH = 12
    }
}