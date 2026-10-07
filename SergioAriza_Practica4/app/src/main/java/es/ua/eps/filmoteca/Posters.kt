package es.ua.eps.filmoteca

import android.widget.ImageView
import coil3.load
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.error


fun ImageView.showPoster(poster: Any?) {
    load(poster) {
        placeholder(R.drawable.poster_placeholder)
        error(R.drawable.poster_placeholder)
        fallback(R.drawable.poster_placeholder)
    }
}
