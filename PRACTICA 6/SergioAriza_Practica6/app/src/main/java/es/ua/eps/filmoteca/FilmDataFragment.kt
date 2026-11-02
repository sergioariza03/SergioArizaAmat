package es.ua.eps.filmoteca

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import es.ua.eps.filmoteca.databinding.FragmentFilmDataBinding
import es.ua.eps.filmoteca.ui.theme.FilmDataSource

class FilmDataFragment : Fragment() {

    companion object {
        private const val ARG_FILM_INDEX = "filmIndex"
        private const val STATE_FILM_INDEX = "filmIndex"

        // Un fragmento necesita un constructor sin parámetros, así que la posición
        // se pasa en los argumentos (el Bundle se construye a mano, sin bundleOf)
        fun newInstance(position: Int): FilmDataFragment {
            val fragment = FilmDataFragment()
            fragment.arguments = Bundle().apply { putInt(ARG_FILM_INDEX, position) }
            return fragment
        }
    }

    private var _bindings: FragmentFilmDataBinding? = null
    private val bindings get() = _bindings!!

    // Posición de la película que se ve, -1 si no hay ninguna
    private var filmIndex: Int = -1

    private val editLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult

                val newFilm = IntentCompat.getParcelableExtra(
                    data,
                    FilmEditActivity.EXTRA_FILM,
                    Film::class.java
                ) ?: return@registerForActivityResult

                if (filmIndex in FilmDataSource.films.indices) {
                    FilmDataSource.films[filmIndex] = newFilm
                    bindFilm()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Primero el estado guardado (showFilm puede haber cambiado la película
        // después de crear el fragmento), y si no hay, los argumentos
        filmIndex = savedInstanceState?.getInt(STATE_FILM_INDEX)
            ?: arguments?.getInt(ARG_FILM_INDEX, -1)
            ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bindings = FragmentFilmDataBinding.inflate(
            inflater,
            container,
            false
        )
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindings.mode.text =
            "${getString(R.string.using_mode)} ${GlobalMode.name}"

        bindings.btnImdb.setOnClickListener {
            val film = FilmDataSource.films.getOrNull(filmIndex)
                ?: return@setOnClickListener

            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(film.imdbUrl)
            )
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // No hay ninguna aplicación disponible para abrir el enlace.
            }
        }

        bindings.btnEditfilm.setOnClickListener {
            openEditFilm()
        }

        bindFilm()
    }

    // Lo llama la actividad cuando hay dos paneles
    fun showFilm(position: Int) {
        filmIndex = position
        bindFilm()
        // La película nueva empieza arriba, no donde estaba la anterior
        _bindings?.filmContent?.scrollTo(0, 0)
    }

    // Borrar películas que están por encima de esta hace que suba de posición
    fun onFilmsDeleted(positions: List<Int>) {
        filmIndex =
            if (filmIndex in positions) {
                -1
            } else {
                filmIndex - positions.count { it < filmIndex }
            }
        bindFilm()
    }

    private fun bindFilm() {
        val currentBindings = _bindings ?: return
        val film = FilmDataSource.films.getOrNull(filmIndex)

        currentBindings.filmContent.isVisible = film != null
        currentBindings.placeholder.isVisible = film == null

        if (film == null) return

        with(currentBindings) {
            poster.showPoster(film.poster)
            title.text = film.title
            director.text = getString(R.string.director, film.director)
            year.text = getString(R.string.year, film.year)
            comments.text = film.comments

            val genres = resources.getStringArray(R.array.genres)
            val formats = resources.getStringArray(R.array.formats)

            genreAndFormat.text = getString(
                R.string.gen_and_format,
                genres[film.genre.ordinal],
                formats[film.format.ordinal]
            )
        }
    }

    private fun openEditFilm() {
        val film = FilmDataSource.films.getOrNull(filmIndex)
            ?: return

        val intent = Intent(
            requireContext(),
            FilmEditActivity::class.java
        )

        intent.putExtra(
            FilmEditActivity.EXTRA_FILM,
            film
        )

        editLauncher.launch(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_FILM_INDEX, filmIndex)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }
}
