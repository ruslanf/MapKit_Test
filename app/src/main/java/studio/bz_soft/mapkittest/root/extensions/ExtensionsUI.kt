package studio.bz_soft.mapkittest.root.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.network.HttpException
import com.google.android.material.snackbar.Snackbar
import studio.bz_soft.mapkittest.BuildConfig

fun ProgressBar.showProgressBar(progress: Int) {
    this.visibility = if (progress > 0) View.VISIBLE else View.GONE
}

fun View.showToast(text: String) {
    Toast.makeText(this.context, biggerText(text), Toast.LENGTH_LONG).show()
}

fun Context.showToast(text: String) {
    Toast.makeText(this, biggerText(text), Toast.LENGTH_LONG).show()
}

private fun biggerText(text: String): String {
    val biggerText = SpannableStringBuilder(text)
    biggerText.setSpan(RelativeSizeSpan(1.35f), 0, text.length, 0)
    return biggerText.toString()
}

fun View.showSnackBar(@StringRes messageId: Int): Snackbar =
    Snackbar.make(this, messageId, Snackbar.LENGTH_LONG)

//fun Context.showError(ex: HttpException, @StringRes messageId: Int, logTag: String) {
//    val error = parseHttpError(ex)
//    showToast("${this.getString(messageId)}. $error")
//    if (BuildConfig.DEBUG) Log.d(logTag, "Error is => $error")
//}
//
//fun Context.showError(ex: Exception, @StringRes messageId: Int, logTag: String) {
//    val error = parseError(ex)
//    showToast("${this.getString(messageId)}. $error")
//    if (BuildConfig.DEBUG) Log.d(logTag, "Error is => $error")
//}

@SuppressLint("UseCompatLoadingForDrawables")
fun View.drawable(@DrawableRes res: Int): Drawable = this.resources.getDrawable(res, null)
@SuppressLint("UseCompatLoadingForDrawables")
fun Context.drawable(@DrawableRes res: Int): Drawable = this.resources.getDrawable(res, null)

fun Context.color(@ColorRes color: Int): Int =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) this.resources.getColor(color, null)
    else this.resources.getColor(color)

@SuppressLint("NewApi")
fun View.color(@ColorRes color: Int): Int = this.resources.getColor(color, null)


fun Context.setAnimationTextLeft(): Animation =
    AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)

private fun scrollToPosition(recyclerView: RecyclerView, position: Int) =
    Handler().postDelayed({ recyclerView.scrollToPosition(position) }, 200)

//fun <T> RecyclerView.setRecyclerView(v: View, delegateAdapter: DelegateAdapter<T>) {
//    val recyclerViewState: Parcelable? = null
//    var position = 0
//    this.apply {
//        adapter = delegateAdapter
//        startAnimation(context.setAnimationTextLeft())
//        layoutManager = LinearLayoutManager(v.context, RecyclerView.VERTICAL, false)
//        recyclerViewState?.apply {
//            layoutManager?.onRestoreInstanceState(recyclerViewState)
//            scrollToPosition(this@setRecyclerView, position)
//        }
//    }
//}