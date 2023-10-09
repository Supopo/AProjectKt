package com.zoo.mvvmkt.util

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.customview.widget.ViewDragHelper
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintStream
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.net.ServerSocket
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.WeakHashMap

/**
 * 使用：在Application文件中初始化init，把日志输出换为Logger.就行
 * 唤起log筛选器，在顶部200像素内，快速单击 SHORT_CLICK + 1 下；
 */
class Logger private constructor(private val mContext: Context) : FrameLayout(
    mContext
), Thread.UncaughtExceptionHandler, ActivityLifecycleCallbacks {
    private var timestamp: Long = 0
    private var mSrcView: View? = null
    private var mShortClick = 0
    private var mFilterClick = 0
    private var mCurrentActivity: Context? = null
    private var mFilterDialog: AlertDialog? = null
    private var mFilterText: String? = null
    private var mFilterLevel = 0
    private var mLogContainer: LinearLayout? = null
    private val mLogList: MutableList<String> = ArrayList()
    private val mFilterList: MutableList<String> = ArrayList()
    private val mLogAdapter: ArrayAdapter<String>
    private val mTvTitle: TextView
    private val mLvLog: ListView
    private val mAutoScroll = true

    @Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
    @Retention(RetentionPolicy.RUNTIME)
    annotation class IgnoreLoggerView { // 有些自定义view在解绑时会跟本工具冲突(onPause后view空白)
        // 可以在activity上打上此注解忽略本工具View
        // 当然忽略后不能在界面上唤起悬浮窗
    }

    //activity内存泄漏检测
    private val mLeakCheck: LeakCheck = LeakCheck()
    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            handler.removeCallbacks(this)
            handler.postDelayed(this, 10000) //10秒检测一次
            var s: String? = null
            try {
                s = mLeakCheck.checkLeak()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun print(type: Int, tag: String, msg: String) {
        if (!showLogcat || me == null || type < mFilterLevel + 2) return
        val str = "[" + time + "]" + getLevel(type) + "/" + tag + ":" + msg
        if (!TextUtils.isEmpty(mFilterText) && !str.contains(mFilterText!!)) return
        handler.obtainMessage(type, str).sendToTarget()
        var start = 0
        var end = 0
        while (end < msg.length) {
            end = if (start + 3000 > msg.length) msg.length else start + 3000
            val subMsg = msg.substring(start, end)
            start = end
            when (type) {
                Log.VERBOSE -> Log.v(tag, subMsg)
                Log.DEBUG -> Log.d(tag, subMsg)
                Log.INFO -> Log.i(tag, subMsg)
                Log.WARN -> Log.w(tag, subMsg)
                Log.ERROR -> Log.e(tag, subMsg)
                LOG_SOUT -> println("$tag:$subMsg")
            }
        }
    }

    private fun getLevel(type: Int): String {
        val level = arrayOf("S", "", "V", "D", "I", "W", "E")
        return level[type]
    }

    private val time: String
        private get() = SimpleDateFormat(
            "MM-dd HH:mm:ss.SSS",
            Locale.getDefault()
        ).format(Date())

    private fun addText(type: Int, text: String) {
        //设置日志颜色
        val level = arrayOf("#FFFFFF", "", "#FFFFFF", "#131417", "#00ff00", "#EFC429", "#FF0000")
        val str = String.format("<font color=\"" + level[type] + "\">%s</font>", text)
        mLogList.add(str)
        while (mLogList.size > 100) mLogList.removeAt(0)
        refreshList()
    }

    /*刷新日志列表*/
    private fun refreshList() {
        mFilterList.clear() //清空过滤列表
        for (i in mLogList.indices) {
            val s = mLogList[i]
            var l = 2
            for (j in 2..6) {
                val level1 = getLevel(j)
                if (s.contains("]$level1/")) {
                    l = j
                    break
                }
            }
            if (l >= mFilterLevel + 2 && (mFilterText == null || s.contains(mFilterText!!))) {
                mFilterList.add(s)
            }
        }
        mLogAdapter.notifyDataSetChanged()

        //自动向下滑
        if (mLogList.size > 0 && mAutoScroll) {
            mLvLog.setSelection(mLogList.size - 1)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mLeakCheck.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        mCurrentActivity = activity
        if (showLogcat) {
            if (checkIgnore(activity)) return
            val decorView = activity.window.decorView as ViewGroup
            mSrcView = decorView.getChildAt(0)
            decorView.removeView(mSrcView)
            me!!.addView(mSrcView, 0)
            me!!.addView(mLogContainer, 1)
            decorView.addView(me)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        mCurrentActivity = null
        if (checkIgnore(activity)) return
        val decorView = activity.window.decorView as ViewGroup
        me!!.removeView(mSrcView)
        me!!.removeView(mLogContainer)
        decorView.removeView(me)
        if (mSrcView != null) {
            decorView.addView(mSrcView, 0)
        }
    }

    private fun checkIgnore(activity: Activity): Boolean {
        val a: Class<out Activity> = activity.javaClass
        return a.isAnnotationPresent(IgnoreLoggerView::class.java)
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        mLeakCheck.remove(activity)
    }

    val dragHelper = ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child === mLogContainer
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            resetParams(left, top)
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return measuredWidth - child.measuredWidth
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return measuredHeight - child.measuredHeight
        }
    })

    private fun resetParams(x: Int, y: Int) {
        val margin = MarginLayoutParams(mLogContainer?.layoutParams)
        margin.setMargins(x, y, x + margin.width, y + margin.height)
        val layoutParams = LayoutParams(margin)
        mLogContainer?.layoutParams = layoutParams
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        if (action == MotionEvent.ACTION_DOWN) {
            val l = SystemClock.uptimeMillis()
            val dis = l - timestamp
            checkSwitchEx(dis)
            checkFilter(dis, ev.y)
            timestamp = SystemClock.uptimeMillis()
        }
        return super.dispatchTouchEvent(ev)
    }

    //快速点击四下
    private fun checkSwitchEx(dis: Long) {
        if (dis < 500) {
            mShortClick++
            if (mShortClick >= SHORT_CLICK) {
                loggerSwitch()
                mShortClick = 0
            }
        } else {
            mShortClick = 0
        }
    }

    //日志开关切换
    private fun loggerSwitch() {
        if (mLogContainer?.visibility == GONE) {
            mLogContainer?.visibility = VISIBLE
        } else {
            mLogContainer?.visibility = GONE
        }
        clearClick()
    }

    private fun checkFilter(dis: Long, y: Float) {
        if (mLogContainer?.visibility == GONE) return
        if (dis < 300 && y < 200) {
            mFilterClick++
            if (mFilterClick > 3) {
                showFilterDialog()
                mFilterClick = 0
            }
        } else {
            mFilterClick = 0
        }
    }

    private fun clearClick() {
        mShortClick = 0
    }

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val text = msg.obj as String
            addText(msg.what, text)
        }
    }

    init {
        Companion.tag = mContext.applicationInfo.packageName //可以自定义
        //日志容器
        mLogContainer = LinearLayout(mContext)
        mLogContainer?.orientation = LinearLayout.VERTICAL
        mLogContainer?.setBackgroundColor(Color.argb(0x33, 0X00, 0x00, 0x00))
        val widthPixels = mContext.resources.displayMetrics.widthPixels
        val heightPixels = mContext.resources.displayMetrics.heightPixels
        //设置日志窗口宽高
        val layoutParams = LayoutParams(4 * widthPixels / 5, heightPixels / 2, Gravity.CENTER)
        mLogContainer?.layoutParams = layoutParams
        mLogContainer?.visibility = GONE
        //小窗口标题
        mTvTitle = TextView(mContext)
        //标题字体大小
        mTvTitle.textSize = 14f
        mTvTitle.text = "此处可拖动 长按关闭"
        mTvTitle.setTextColor(Color.WHITE)
        mTvTitle.setBackgroundColor(Color.argb(0x55, 0X00, 0x00, 0x00))
        mTvTitle.setOnClickListener {
            showFilterDialog() //点击日志窗口标题栏打开过滤器
        }
        mTvTitle.setOnLongClickListener {
            loggerSwitch() //长按日志窗口标题栏关闭日志窗口
            true
        }
        mLogContainer?.addView(mTvTitle)
        //日志列表
        mLvLog = object : ListView(mContext) {
            override fun onTouchEvent(ev: MotionEvent): Boolean {
                parent.requestDisallowInterceptTouchEvent(true)
                return super.onTouchEvent(ev)
            }
        }
        mLvLog.setFastScrollEnabled(true)
        mLogContainer?.addView(mLvLog)
        mLogAdapter =
            object : ArrayAdapter<String>(mContext, R.layout.simple_list_item_1, mFilterList) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    var convertView = convertView
                    if (convertView == null) {
                        convertView = TextView(parent.context)
                    }
                    val textView = convertView as TextView
                    //内容字体大小
                    textView.textSize = 14f
                    textView.text = Html.fromHtml(mFilterList[position])
                    textView.setShadowLayer(1f, 1f, 1f, Color.BLACK)
                    return textView
                }
            }
        mLvLog.setAdapter(mLogAdapter)
        mLvLog.setOnItemClickListener { parent, view, position, id ->
            val builder = AlertDialog.Builder(mCurrentActivity)
            var message = mFilterList[position]
            message = message.replace("FFFFFF", "000000")
            builder.setMessage(Html.fromHtml(message))
            builder.setPositiveButton("确定", null)
            builder.setNegativeButton("清空日志") { dialog, which ->
                mLogList.clear()
                refreshList()
            }
            builder.show()
        }

        //检测内存泄漏相关
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(mRunnable, 10000)
    }

    private fun showFilterDialog() {
        if (mCurrentActivity == null) return
        val builder = AlertDialog.Builder(mCurrentActivity)
        builder.setTitle("日志过滤器")
        builder.setView(initDialogView())
        builder.setCancelable(false)
        if (mFilterDialog != null) {
            mFilterDialog!!.dismiss()
        }
        mFilterDialog = builder.show()
    }

    private fun initDialogView(): View {
        //容器
        val linearLayout = LinearLayout(mCurrentActivity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = Gravity.CENTER_HORIZONTAL
        //下拉框
        val spinner = Spinner(mCurrentActivity, Spinner.MODE_DROPDOWN)
        spinner.adapter = ArrayAdapter(
            mCurrentActivity!!,
            R.layout.simple_spinner_dropdown_item,
            arrayOf("Verbose", "Debug", "Info", "Warn", "Error")
        )
        spinner.setSelection(mFilterLevel)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                mFilterLevel = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        //文本编辑框
        val editText = EditText(mCurrentActivity)
        editText.hint = "筛选关键字"
        if (mFilterText != null) {
            editText.setText(mFilterText)
            editText.setSelection(mFilterText!!.length)
        }
        //按钮
        val button = Button(mCurrentActivity)
        button.text = "确定"
        button.setOnClickListener {
            mFilterText = editText.text.toString()
            mFilterDialog!!.dismiss()
            refreshList()
        }
        //添加到容器
        linearLayout.addView(spinner)
        linearLayout.addView(editText)
        linearLayout.addView(button)
        return linearLayout
    }

    /**
     * 捕获崩溃信息
     *
     * @param t
     * @param e
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
        // 打印异常信息
        e.printStackTrace()
        // 我们没有处理异常 并且默认异常处理不为空 则交给系统处理
        if (!handleException(t, e) && mDefaultHandler != null) {
            // 系统处理
            mDefaultHandler!!.uncaughtException(t, e)
        }
    }

    /*自己处理崩溃事件*/
    private fun handleException(t: Thread, e: Throwable?): Boolean {
        if (e == null) {
            return false
        }
        if (null == mCurrentActivity) {
            val content_url = Uri.parse("http://127.0.0.1:45678")
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_TASK_ON_HOME
            intent.action = "android.intent.action.VIEW"
            intent.data = content_url
            mContext.startActivity(intent)
        }
        object : Thread() {
            override fun run() {
                val baos = ByteArrayOutputStream()
                val printStream = PrintStream(baos)
                e.printStackTrace(printStream)
                val s = baos.toString()
                val split = s.split("\t".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val sb = StringBuilder()
                for (i in split.indices) {
                    var s1 = split[i]
                    if ((!s1.contains("android.") && !s1.contains("java.")
                                && s1.contains("at")) && i > 0
                    ) {
                        s1 = String.format("<br> <font color='#ff0000'>%s</font>", s1)
                    }
                    sb.append(s1).append("\t ")
                }
                if (null == mCurrentActivity) {
                    showInWeb(sb.toString(), t, e)
                    return
                }
                val spanned = Html.fromHtml(sb.toString())
                Looper.prepare()
                Toast.makeText(mCurrentActivity, "APP 崩溃", Toast.LENGTH_LONG)
                    .show()
                val builder = AlertDialog.Builder(mCurrentActivity)
                builder.setTitle("App Crash,Log:")
                builder.setMessage(spanned)
                builder.setPositiveButton("关闭app") { dialog, which ->
                    mDefaultHandler!!.uncaughtException(t, e)
                    //Process.killProcess(Process.myPid());
                }
                builder.setCancelable(false)
                builder.show()
                Looper.loop()
            }
        }.start()
        return true
    }

    private fun showInWeb(msg: CharSequence, t: Thread, ex: Throwable) {
        try {
            val socket = ServerSocket(45678)
            val sb = StringBuilder("HTTP/1.1 200 OK\n")
                .append("\n")
                .append("<head>")
                .append("<meta name='viewport' content='width=240, target-densityDpi=device-dpi'>")
                .append("</head>")
                .append("<html>")
                .append("<h1>APP Crash</h1>")
                .append(msg)
                .append("<br/>")
                .append("</html>")
            val bytes = sb.toString().toByteArray()
            while (true) {
                val accept = socket.accept()
                val os = accept.getOutputStream()
                os.write(bytes)
                os.flush()
                os.close()
                accept.close()
                mDefaultHandler!!.uncaughtException(t, ex)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private inner class LeakCheck {
        var mList = Collections.synchronizedList(ArrayList<Int>())
        var mMap = WeakHashMap<Activity, Int>()

        var mQueue: ReferenceQueue<Any> = ReferenceQueue()
        var mPhantomReference: WeakReference<Any> = WeakReference(Any(), mQueue)


        fun add(activity: Activity) {
            val code = activity.hashCode()
            mList.add(code)
            mMap[activity] = code
        }

        fun remove(activity: Activity) {
            mList.remove(Integer.valueOf(activity.hashCode()))
        }

        @Throws(InterruptedException::class)
        fun checkLeak(): String? {
            if (!mPhantomReference.isEnqueued) return null
            if (checkLeak) {
                e("检测到GC")
                e("理论存活activity数：" + mList.size)
            }
            val stringBuilder = StringBuilder()
            for (activity in mMap.keys) {
                val s = activity.hashCode()
                val name = activity.javaClass.name
                if (!mList.contains(s)) {
                    stringBuilder.append(name).append(";")
                    if (checkLeak) {
                        e("$name 可能发生内存泄漏,请检查")
                    }
                }
            }
            mQueue.remove()
            mPhantomReference = WeakReference(Any(), mQueue)
            return stringBuilder.toString()
        }
    }

    companion object {
        private const val showLogcat = true //正式环境(false)不打印日志，也不能唤起app的debug界面
        private const val checkLeak = false //是否展示内存泄露信息

        @SuppressLint("StaticFieldLeak")
        private var me: Logger? = null
        private var tag: String = "Logger"
        private const val LOG_SOUT = 8
        private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
        private const val SHORT_CLICK = 3
        fun setTag(tag: String) {
            Companion.tag = tag
        }

        /**
         * 在application 的 onCreate() 方法初始化
         *
         * @param application
         */
        fun init(application: Application) {
            if (showLogcat && me == null) {
                synchronized(Logger::class.java) {
                    if (me == null) {
                        me = Logger(application.applicationContext)
                        application.registerActivityLifecycleCallbacks(me)
                        //获取系统默认异常处理器
                        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
                        //线程空闲时设置异常处理，兼容其他框架异常处理能力
                        Looper.myQueue().addIdleHandler {
                            Thread.setDefaultUncaughtExceptionHandler(me) //线程异常处理设置为自己
                            false
                        }
                    }
                }
            }
        }

        fun v(msg: String) {
            v(tag, msg)
        }

        fun d(msg: String) {
            d(tag, msg)
        }

        fun i(msg: String) {
            i(tag, msg)
        }

        fun w(msg: String) {
            w(tag, msg)
        }

        fun e(msg: String) {
            e(tag, msg)
        }

        fun s(msg: String) {
            s(tag, msg)
        }

        fun v(tag: String, msg: String) {
            if (me != null) me!!.print(Log.VERBOSE, tag, msg)
        }

        fun d(tag: String, msg: String) {
            if (me != null) me!!.print(Log.DEBUG, tag, msg)
        }

        fun i(tag: String, msg: String) {
            if (me != null) me!!.print(Log.INFO, tag, msg)
        }

        fun w(tag: String, msg: String) {
            if (me != null) me!!.print(Log.WARN, tag, msg)
        }

        fun e(tag: String, msg: String) {
            if (me != null) me!!.print(Log.ERROR, tag, msg)
        }

        fun s(tag: String, msg: String) {
            if (me != null) me!!.print(LOG_SOUT, tag, msg)
        }
    }
}