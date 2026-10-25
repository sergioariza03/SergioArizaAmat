package es.ua.eps.filmoteca

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Film(
    var title: String = "",
    var director: String = "",
    var year: Int = 0,
    var genre: Genre = Genre.Action,
    var format: Format = Format.DVD,
    var imdbUrl: String = "",
    var comments: String = "",

    var imageResId: Int = 0,
    var imageUrl: String? = null,
    var image: Bitmap? = null,


) : Parcelable {

    val poster: Any?
        get() = image ?: imageUrl ?: imageResId.takeIf { it != 0 }

    enum class Genre { Action, Drama, Comedy, Horror, SciFi }

    enum class Format { DVD, BluRay, Digital }

    override fun toString() = title
}
