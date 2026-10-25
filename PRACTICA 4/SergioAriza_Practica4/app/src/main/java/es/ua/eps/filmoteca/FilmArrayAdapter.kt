package es.ua.eps.filmoteca

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import es.ua.eps.filmoteca.databinding.ItemFilmBinding

fun ItemFilmBinding.bind(film: Film) {
    poster.showPoster(film.poster)
    title.text = film.title
    director.text = film.director
}

class FilmArrayAdapter(
    context: Context,
    films: List<Film>
) : ArrayAdapter<Film>(context, 0, films) {

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val binding =
            if (convertView == null) {
                ItemFilmBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            } else {
                ItemFilmBinding.bind(convertView)
            }

        getItem(position)?.let {
            binding.bind(it)
        }

        return binding.root
    }
}
