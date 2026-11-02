package es.ua.eps.filmoteca

import android.widget.ImageView
import coil3.load
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage


fun ImageView.showPoster(poster: Any?) {
    load(poster) {
        placeholder(R.drawable.poster_placeholder)
        error(R.drawable.poster_placeholder)
        fallback(R.drawable.poster_placeholder)
    }

}

@Composable
fun Poster(
    poster: Any?,
    modifier: Modifier = Modifier
) {
    val placeholder = painterResource(R.drawable.poster_placeholder)

    AsyncImage(
        model = poster,
        contentDescription = stringResource(R.string.poster_description),
        placeholder = placeholder,
        error = placeholder,
        fallback = placeholder,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
