package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Intent

fun Activity.navigateUpToList() {
    val intent = Intent(this, FilmListDetailActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_SINGLE_TOP
    navigateUpTo(intent)
}
