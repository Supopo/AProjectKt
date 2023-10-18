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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zoo.mvvmkt.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 城市选择器
 *
 *
 * mCityPicker = new CustomCityPicker(this, new CustomCityPicker.ResultHandler() {
 * public void handle(String result) {
 * //result为选择的地址
 * }
 * });
 * mCityPicker.initJson();
 * ...
 * mCityPicker.show();
 */
class CustomCityPicker(private val context: Context, private val handler: ResultHandler) {
    /**
     * 定义结果回调接口
     */
    interface ResultHandler {
        fun handle(result: String?)
    }

    private var canAccess = false
    private var cityPickerDialog: Dialog? = null
    private var province_pv: ScrollPickerView? = null
    private var city_pv: ScrollPickerView? = null
    private var area_pv: ScrollPickerView? = null
    private var tv_cancle: TextView? = null
    private var tv_select: TextView? = null
    private var mProvince: String? = null
    private var mCity: String? = null
    private var mArea: String? = null
    private val provinceList: MutableList<Province> = ArrayList()
    private val provinces: MutableList<String> = ArrayList()
    private val citys: MutableList<List<String>> = ArrayList()
    private val countys: MutableList<List<List<String>>> = ArrayList()
    private fun initDialog() {
        if (cityPickerDialog == null) {
            cityPickerDialog = Dialog(context, R.style.date_picker)
            cityPickerDialog!!.setCancelable(false)
            cityPickerDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            cityPickerDialog!!.setContentView(R.layout.picker_custom_city)
            val window = cityPickerDialog!!.window
            window!!.setGravity(Gravity.BOTTOM)
            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            manager.defaultDisplay.getMetrics(dm)
            val lp = window.attributes
            lp.width = dm.widthPixels
            window.attributes = lp
        }
    }

    private fun initView() {
        province_pv = cityPickerDialog!!.findViewById<View>(R.id.province_pv) as ScrollPickerView
        city_pv = cityPickerDialog!!.findViewById<View>(R.id.city_pv) as ScrollPickerView
        area_pv = cityPickerDialog!!.findViewById<View>(R.id.area_pv) as ScrollPickerView
        tv_cancle = cityPickerDialog!!.findViewById<View>(R.id.tv_cancle) as TextView
        tv_select = cityPickerDialog!!.findViewById<View>(R.id.tv_select) as TextView
        tv_cancle!!.setOnClickListener { cityPickerDialog!!.dismiss() }
        tv_select!!.setOnClickListener {
            if (mProvince == mCity) {
                handler.handle("$mCity $mArea")
            } else {
                handler.handle("$mProvince $mCity $mArea")
            }
            cityPickerDialog!!.dismiss()
        }
    }

    fun show(area: String?) {
        if (canAccess) {
            canAccess = true
            loadComponent()
            addListener()
            setSelectedArea(area)
            cityPickerDialog!!.show()
        } else {
            canAccess = false
        }
    }

    fun show() {
        if (canAccess) {
            canAccess = true
            loadComponent()
            addListener()
            cityPickerDialog!!.show()
        } else {
            canAccess = false
        }
    }

    //int type :  0 正常模式 ;1 第3级多一个全市区 ;2 只显示陕西省市区
    //提前加载数据，这样不需要花费过长时间
    @JvmOverloads
    fun initJson(type: Int = 0) {
        initArrayList()
        val json = getJson(context, "city.json")
        val gson = Gson()
        val temp =
            gson.fromJson<List<Province>>(json, object : TypeToken<List<Province?>?>() {}.type)
        provinceList.addAll(temp)
        val provinceSize = provinceList.size
        //添加省
        for (x in 0 until provinceSize) {
            var pro: Province
            pro = provinceList[x]
            if (type == 0) {
                provinces.add(pro.areaName!!)
                val cities = pro.cities
                val xCities: MutableList<String> = ArrayList()
                val xCounties: MutableList<List<String>> = ArrayList()
                val citySize = cities.size
                //添加地市
                for (y in 0 until citySize) {
                    val cit = cities[y]
                    xCities.add(cit.areaName!!)
                    val counties = cit.counties
                    val yCounties: MutableList<String> = ArrayList()
                    val countySize = counties.size
                    //添加区县
                    if (countySize == 0) {
                        yCounties.add(cit.areaName!!)
                    } else {
                        if (type == 2) yCounties.add("全市区")
                        for (z in 0 until countySize) {
                            yCounties.add(counties[z].areaName!!)
                        }
                    }
                    xCounties.add(yCounties)
                }
                citys.add(xCities)
                countys.add(xCounties)
            }
            if (type == 1) {
                provinces.add(pro.areaName!!)
                val cities = pro.cities
                val xCities: MutableList<String> = ArrayList()
                val xCounties: MutableList<List<String>> = ArrayList()
                val citySize = cities.size
                //添加地市
                for (y in 0 until citySize) {
                    val cit = cities[y]
                    xCities.add(cit.areaName!!)
                    val counties = cit.counties
                    val yCounties: MutableList<String> = ArrayList()
                    val countySize = counties.size
                    //添加区县
                    if (countySize == 0) {
                        yCounties.add(cit.areaName!!)
                    } else {
                        yCounties.add("全市区")
                        for (z in 0 until countySize) {
                            yCounties.add(counties[z].areaName!!)
                        }
                    }
                    xCounties.add(yCounties)
                }
                citys.add(xCities)
                countys.add(xCounties)
            }
            if (type == 2) {
                if (pro.areaName == "陕西省") {
                    provinces.add(pro.areaName!!)
                    val cities = pro.cities
                    val xCities: MutableList<String> = ArrayList()
                    val xCounties: MutableList<List<String>> = ArrayList()
                    val citySize = cities.size
                    //添加地市
                    for (y in 0 until citySize) {
                        val cit = cities[y]
                        xCities.add(cit.areaName!!)
                        val counties = cit.counties
                        val yCounties: MutableList<String> = ArrayList()
                        val countySize = counties.size
                        //添加区县
                        if (countySize == 0) {
                            yCounties.add(cit.areaName!!)
                        } else {
                            for (z in 0 until countySize) {
                                yCounties.add(counties[z].areaName!!)
                            }
                        }
                        xCounties.add(yCounties)
                    }
                    citys.add(xCities)
                    countys.add(xCounties)
                }
            }
        }
    }

    /**
     * The type Area.
     */
    abstract class Area {
        /**
         * Gets area id.
         *
         * @return the area id
         */
        /**
         * Sets area id.
         *
         * @param areaId the area id
         */
        /**
         * The Area id.
         */
        var areaId: String? = null
        /**
         * Gets area name.
         *
         * @return the area name
         */
        /**
         * Sets area name.
         *
         * @param areaName the area name
         */
        /**
         * The Area name.
         */
        var areaName: String? = null
        override fun toString(): String {
            return "areaId=$areaId,areaName=$areaName"
        }
    }

    /**
     * The type Province.
     */
    class Province : Area() {
        /**
         * Gets cities.
         *
         * @return the cities
         */
        /**
         * Sets cities.
         *
         * @param cities the cities
         */
        /**
         * The Cities.
         */
        var cities: List<City> = ArrayList()
    }

    /**
     * The type City.
     */
    class City : Area() {
        /**
         * Gets counties.
         *
         * @return the counties
         */
        var counties: List<County> = ArrayList()
            private set

        /**
         * Sets counties.
         *
         * @param counties the counties
         */
        fun setCounties(counties: ArrayList<County>) {
            this.counties = counties
        }
    }

    /**
     * The type County.
     */
    class County : Area()

    private fun initArrayList() {
        provinceList.clear()
        provinces.clear()
        citys.clear()
        countys.clear()
    }

    private fun loadComponent() {
        province_pv!!.setData(provinces)
        city_pv!!.setData(citys[0] as MutableList<String>)
        area_pv!!.setData(countys[0][0] as MutableList<String>)
        mProvince = provinces[0]
        mCity = citys[0][0]
        mArea = countys[0][0][0]
        province_pv!!.setSelected(0)
        city_pv!!.setSelected(0)
        area_pv!!.setSelected(0)
        executeScroll()
    }

    private var provinceSelect = 0
    private var citySelect = 0

    init {
        canAccess = true
        initDialog()
        initView()
    }

    private fun addListener() {
        province_pv!!.setOnSelectListener(object : ScrollPickerView.onSelectListener {
            override fun onSelect(text: String?) {
                for (i in provinces.indices) {
                    if (provinces[i] == text) {
                        provinceSelect = i
                        break
                    }
                }
                mProvince = text.toString()
                changeCity(provinceSelect)
            }

        })

        city_pv!!.setOnSelectListener(object : ScrollPickerView.onSelectListener {
            override fun onSelect(text: String?) {
                for (i in citys[provinceSelect].indices) {
                    if (citys[provinceSelect][i] == text) {
                        citySelect = i
                        break
                    }
                }
                mCity = text.toString()
                changeArea(provinceSelect, citySelect)
            }

        })


        area_pv!!.setOnSelectListener(object : ScrollPickerView.onSelectListener {
            override fun onSelect(text: String?) {
                mArea = text.toString()
            }
        })
    }

    private fun changeCity(temp: Int) {
        city_pv!!.setData(citys[temp] as MutableList<String>)
        city_pv!!.setSelected(0)
        mCity = citys[temp][0]
        executeAnimator(city_pv)
        city_pv!!.postDelayed({ changeArea(temp, 0) }, 100)
    }

    private fun changeArea(temp: Int, temp2: Int) {
        area_pv!!.setData(countys[temp][temp2] as MutableList<String>)
        area_pv!!.setSelected(0)
        mArea = countys[temp][temp2][0]
        executeAnimator(area_pv)
    }

    private fun executeAnimator(view: View?) {
        val pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f)
        val pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f)
        val pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f)
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(200).start()
    }

    private fun executeScroll() {
        province_pv!!.setCanScroll(provinces.size > 1)
        city_pv!!.setCanScroll(citys.size > 1)
        area_pv!!.setCanScroll(countys.size > 1)
    }

    /**
     * 设置默认选中的地址
     */
    fun setSelectedArea(area: String?) {}

    companion object {
        //获取assets文件夹里面的城市JSON
        fun getJson(mContext: Context, fileName: String?): String {
            val sb = StringBuilder()
            val am = mContext.assets
            try {
                val br = BufferedReader(
                    InputStreamReader(
                        am.open(fileName!!)
                    )
                )
                var next: String? = ""
                while (null != br.readLine().also { next = it }) {
                    sb.append(next)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                sb.delete(0, sb.length)
            }
            return sb.toString().trim { it <= ' ' }
        }
    }
}