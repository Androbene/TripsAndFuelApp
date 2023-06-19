package ua.androbene.tripsandfuel.user_interface.base

import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import ua.androbene.tripsandfuel.R

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun TextView.paintTextGradient() {
    val myShader: Shader = LinearGradient(
        0f, 0f, 0f, 60f,
        resources.getColor(R.color.gradient_color_2),
        resources.getColor(R.color.gradient_color_1),
        Shader.TileMode.CLAMP
    )
    this.paint.shader = myShader
}
fun ImageView.setSrc(@DrawableRes drawableRes: Int) {
    setImageDrawable(ContextCompat.getDrawable(context, drawableRes))
}

fun View.setBg(@DrawableRes drawableRes: Int) {
    background = ContextCompat.getDrawable(context, drawableRes)
}
