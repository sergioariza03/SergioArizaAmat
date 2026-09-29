package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.IntentCompat

class FilmDataActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILM_TITLE = "es.ua.eps.filmoteca.extra.FILM_TITLE"
    }
    private lateinit var bindings: ActivityFilmDataBinding
    private lateinit var film: Film
    private var label by mutableStateOf("")


    private val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data

        if (result.resultCode == Activity.RESULT_OK && data != null) {
            IntentCompat.getParcelableExtra(
                data,
                FilmEditActivity.EXTRA_FILM,
                Film::class.java
            )?.let { showFilm(it) }
        }
    }

    private fun filmWithTitle(title: String?) =
        sampleFilms().firstOrNull { it.title == title } ?: sampleFilms().first()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        film = filmWithTitle(intent.getStringExtra(EXTRA_FILM_TITLE))

        initUI()
    }

    private fun initUI(){
        when (GlobalMode) {
            Mode.Bindings -> initUIBindings()
            Mode.Compose -> initUICompose()
        }
    }

    private fun openRelatedFilm() {
        val intent = Intent(
            this,
            FilmDataActivity::class.java
        )

       intent.putExtra(
           EXTRA_FILM_TITLE,
           getString(R.string.related_film_title)
       )
        startActivity(intent)
    }

    private fun openEditFilm() {
        val intent = Intent(
            this,
            FilmEditActivity::class.java
        )

        intent.putExtra(
            FilmEditActivity.EXTRA_FILM,
            film
        )

        editLauncher.launch(intent)
    }

    private fun backToMain() {
        val intent = Intent(
            this,
            FilmListActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)
    }

    private fun showFilm(film: Film) {
        this.film = film

        when (GlobalMode) {
            Mode.Bindings -> {
                with(bindings) {
                    poster.setImageResource(film.imageResId)
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

            Mode.Compose -> {
                label = film.title
            }
        }
    }

    private fun initUIBindings() {
        bindings = ActivityFilmDataBinding.inflate(layoutInflater)

        with(bindings) {
            setContentView(root)
            root.applySystemBarsPadding(appBar.root, content)
            setSupportActionBar(appBar.toolbar)

            val str = "${getString(R.string.using_mode)} ${GlobalMode.name}"
            mode.text = str

            showFilm(film)

            btnImdb.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    android.net.Uri.parse(film.imdbUrl)
                )
                startActivity(intent)
            }

            btnEditfilm.setOnClickListener {
                openEditFilm()
            }

            btnBacktomain.setOnClickListener {
                backToMain()
            }
        }
    }
    private fun initUICompose(){
        setContent {
            FilmDataScreen(
                label = label,
                onRelatedFilm = { openRelatedFilm() },
                onEdit = { openEditFilm() },
                onBackToMain = { backToMain() }
            )
        }
    }

    @Composable
    fun FilmDataScreen(
        label: String,
        onRelatedFilm: () -> Unit,
        onEdit: () -> Unit,
        onBackToMain: () -> Unit
    ) {
        MyWindow {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MyText(label)

                Button(onClick = onRelatedFilm) {
                    Text(stringResource(R.string.related_film))
                }

                Button(onClick = onEdit) {
                    Text(stringResource(R.string.edit_film))
                }

                Button(onClick = onBackToMain) {
                    Text(stringResource(R.string.back_to_main))
                }
            }

            MyText(
                "${stringResource(R.string.using_mode)} ${GlobalMode.name}"
            )
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun FilmDataScreenPreview() {
        FilmDataScreen(
            label = "Film A",
            onRelatedFilm = {},
            onEdit = {},
            onBackToMain = {}
        )
    }
}
