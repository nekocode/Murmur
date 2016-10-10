package cn.nekocode.murmur.ui.main

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.text.TextUtils
import android.util.Patterns
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.*
import cn.nekocode.kotgo.component.rx.RxBus
import cn.nekocode.kotgo.component.ui.BaseFragment
import cn.nekocode.kotgo.component.ui.FragmentActivity
import cn.nekocode.murmur.R
import cn.nekocode.murmur.data.DO.douban.SongS
import cn.nekocode.murmur.data.DO.Murmur
import cn.nekocode.murmur.util.CircleTransform
import cn.nekocode.murmur.util.ImageUtil
import cn.nekocode.murmur.widget.ShaderRenderer
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.*
import qiu.niorgai.StatusBarCompat
import kotlin.properties.Delegates

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class MainFragment : BaseFragment(), Contract.View, View.OnTouchListener {
    companion object {
        const val TAG = "MainFragment"

        fun push(fragmentActivity: FragmentActivity) {
            fragmentActivity.push(TAG, MainFragment::class.java)
        }
    }

    lateinit var presenter: MainPresenter

    val ANIMATION_DURATION = 800L
    var renderer: ShaderRenderer by Delegates.notNull()
    var oldBackgroundColor = 0
    var oldTextColor = 0
    var backgroundColorAnimator: ValueAnimator? = null
    var textColorAnimator: ValueAnimator? = null

    // 初始化登陆进度框
    val loginProgressDialog by lazy {
        ProgressDialog(activity).apply {
            setMessage("Loging...")
            setCancelable(false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_main, container, false)
    }

    override fun onCreatePresenter(presenterFactory: PresenterFactory) {
        presenter = presenterFactory.createOrGet(MainPresenter::class.java)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化渲染器
        surfaceView.apply {
            setEGLContextClientVersion(2)
            setOnTouchListener(this@MainFragment)

            val shader = resources.openRawResource(R.raw.shader).reader().readText()
            renderer = ShaderRenderer(activity, shader).apply {
                setBackColor(ContextCompat.getColor(ctx, R.color.color_primary_dark))
                setSpeed(0.6f)

                surfaceView.setRenderer(this)
            }
        }

        // 初始化封面图控件
        coverImageView.apply {
            setFactory {
                ImageView(activity).apply {
                    scaleType = ImageView.ScaleType.FIT_XY
                    layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT)
                }
            }

            inAnimation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in)
            inAnimation.duration = ANIMATION_DURATION

            outAnimation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out)
            outAnimation.duration = ANIMATION_DURATION

            setImageResource(R.drawable.transparent)
        }

        // 设计背景和文字颜色渐变动画的开始颜色
        oldBackgroundColor = ContextCompat.getColor(ctx, R.color.color_primary)
        oldTextColor = Color.WHITE

        // 订阅事件总线
        RxBus.subscribe(String::class.java) {
            if (it.equals("Prepared")) {
                isPaletteChanging = false
                progressWheel.visibility = View.INVISIBLE
            }
        }
    }

    override fun onBackPressed(): Boolean {
        presenter.stopAll()
        return super.onBackPressed()
    }

    override fun showLoginDialog() {
        loginDialog.show()
    }

    override fun onLoginSuccess() {
        loginProgressDialog.dismiss()
    }

    override fun onLoginFailed() {
        showLoginDialog()
        loginProgressDialog.dismiss()
    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun onMurmursChanged(all: List<Murmur>, playing: List<Murmur>) {
        murmursTextView.text = when (playing.size) {
            0 -> "❤"
            1 -> "❤(${playing[0].name})"
            2 -> "❤(${playing[0].name}, ${playing[1].name})"
            else -> "❤(${playing[0].name}, ${playing[1].name}, ...)"
        }

        val booleans = BooleanArray(all.size)
        val items = arrayOfNulls<String>(all.size)
        all.forEachIndexed { i, murmur ->
            items[i] = murmur.name

            playing.forEach {
                if (it.id.equals(murmur.id)) {
                    booleans[i] = true
                }
            }
        }

        murmursTextView.onClick {
            AlertDialog.Builder(activity).apply {
                setMultiChoiceItems(
                        items,
                        booleans
                ) { dialogInterface: DialogInterface, which: Int, isChecked: Boolean ->
                    presenter.changeMurmur(all[which], isChecked)
                }
            }.show()
        }
    }

    val target = object : Target {
        override fun onPrepareLoad(drawable: Drawable?) {
        }

        override fun onBitmapFailed(drawable: Drawable?) {
            coverImageView.setImageResource(R.drawable.transparent)
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, p1: Picasso.LoadedFrom?) {
            bitmap ?: return

            switchPalette(bitmap)
            coverImageView.setImageDrawable(ImageUtil.bitmap2Drawable(bitmap))
        }
    }

    override fun onSongChanged(song: SongS.Song) {
        isPaletteChanging = true

        song.apply {
            titleTextView.text = title
            performerTextView.text = artist

            Picasso.with(activity).apply {
                cancelRequest(target)
                load(picture).transform(CircleTransform()).into(target)
            }
        }

        renderer.setSpeed(1.0f)
    }

    override fun onTimeChanged(timedText: String) {
        timeTextView.text = timedText
    }

    fun switchPalette(bitmap: Bitmap) {
        Palette.from(bitmap).generate {
            val swatch = it.darkVibrantSwatch ?: it.vibrantSwatch ?: it.darkMutedSwatch ?: it.lightMutedSwatch
            swatch!!

            fun createColorAnimator(sourceColor: Int,
                                    targetColor: Int,
                                    updateListener: (it: ValueAnimator) -> Unit): ValueAnimator
                    = ValueAnimator.ofObject(ArgbEvaluator(), sourceColor, targetColor).apply {
                duration = ANIMATION_DURATION + 100
                interpolator = LinearInterpolator()
                addUpdateListener(updateListener)
            }

            backgroundColorAnimator?.cancel()
            backgroundColorAnimator = createColorAnimator(oldBackgroundColor, swatch.rgb) {
                val color = it.animatedValue as Int

                backgroundView?.backgroundColor = color
                renderer.setBackColor(color)
                if(activity?.window != null) {
                    StatusBarCompat.setStatusBarColor(activity, color)
                }

                oldBackgroundColor = color
            }
            backgroundColorAnimator?.start()

            textColorAnimator?.cancel()
            textColorAnimator = createColorAnimator(oldTextColor, swatch.titleTextColor) {
                val color = it.animatedValue as Int

                titleTextView?.textColor = color
                performerTextView?.textColor = color
                murmursTextView?.textColor = color
                timeTextView?.textColor = color
                progressWheel?.barColor = color

                oldTextColor = color
            }
            textColorAnimator?.start()

            listenAnimation(textColorAnimator!!)
        }
    }

    var isPaletteChanging = false
    fun listenAnimation(animator: Animator) {
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isPaletteChanging = false
                progressWheel?.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
    }

    override fun onTouch(view: View?, event: MotionEvent?) = gestureDetector.onTouchEvent(event)

    val gestureDetector by lazy {
        GestureDetector(activity, object : GestureDetector.OnGestureListener {
            val FLING_MIN_DISTANCE = dip(100)
            val FLING_MIN_DISTANCE_Y = dip(50)
            val FLING_MIN_VELOCITY = 1

            var lastestTapTime = 0L
            override fun onSingleTapUp(p0: MotionEvent?): Boolean {
                val nowTapTime = System.currentTimeMillis()
                if (nowTapTime - lastestTapTime < 800) {
                    Toast.makeText(activity, "Double tap", Toast.LENGTH_SHORT).show()

                    lastestTapTime = 0
                    return false
                }

                lastestTapTime = nowTapTime
                return true
            }

            override fun onDown(p0: MotionEvent?): Boolean {
                return true
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (Math.abs(e1.y - e2.y) > FLING_MIN_DISTANCE_Y || isPaletteChanging)
                    return false

                if (e1.x - e2.x > FLING_MIN_DISTANCE
                        && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                    //向右滑动
                    progressWheel.visibility = View.VISIBLE
                    presenter.nextSong()

                } else if (e2.x - e1.x > FLING_MIN_DISTANCE
                        && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                    //向左滑动
                    progressWheel.visibility = View.VISIBLE
                    presenter.nextSong()
                }

                return false
            }

            override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
                return true
            }

            override fun onShowPress(p0: MotionEvent?) {
            }

            override fun onLongPress(p0: MotionEvent?) {
            }

        })
    }

    val loginDialog: AlertDialog by lazy {
        var emailEdit: EditText? = null
        var pwdEdit: EditText? = null

        val dialog = AlertDialog.Builder(ctx).apply {
            setCancelable(false)

            setView(ctx.UI({
                verticalLayout() {
                    padding = dip(30)

                    emailEdit = editText {
                        hint = "Email"
                        textSize = 14f
                    }

                    pwdEdit = editText {
                        hint = "Password"
                        textSize = 14f
                    }
                }
            }).view)

            setPositiveButton("Login") { dialog, which -> }

            setOnKeyListener() { dialog, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                    alert("Are you want to exit?") {
                        negativeButton("No") {
                        }

                        positiveButton("Yes") {
                            activity.finish()
                        }
                    }.show()
                }
                false
            }
        }.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val email = emailEdit?.text.toString().trim()
                val pwd = pwdEdit?.text.toString()

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    toast("Email address is invaild.")

                } else if (TextUtils.isEmpty(pwd)) {
                    toast("Password is invaild.")

                } else {
                    presenter.login(email, pwd)
                    loginProgressDialog.show()

                    dialog.dismiss()
                }
            }
        }

        dialog
    }
}
