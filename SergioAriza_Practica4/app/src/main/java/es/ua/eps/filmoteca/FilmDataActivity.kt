package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration

import androidx.compose.ui.unit.dp

class FilmDataActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILM_TITLE = "es.ua.eps.filmoteca.extra.FILM_TITLE"
        private const val STATE_FILM = "state_film"
    }
    private lateinit var bindings: ActivityFilmDataBinding
    private var film: Film by mutableStateOf(Film())


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

        film = savedInstanceState?.let {
            BundleCompat.getParcelable(
                it,
                STATE_FILM,
                Film::class.java
            )
        } ?: filmWithTitle(intent.getStringExtra(EXTRA_FILM_TITLE))

        initUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(STATE_FILM, film)
        super.onSaveInstanceState(outState)
    }

    private fun initUI(){
        when (GlobalMode) {
            Mode.Bindings -> initUIBindings()
            Mode.Compose -> initUICompose()
        }
    }

    /* private fun openRelatedFilm() {
        val intent = Intent(
            this,
            FilmDataActivity::class.java
        )

       intent.putExtra(
           EXTRA_FILM_TITLE,
           getString(R.string.related_film_title)
       )
        startActivity(intent)
    }*/

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

    private fun showFilm(newFilm: Film) {
        film = newFilm

        if (GlobalMode == Mode.Bindings) {
            with(bindings) {
                val bmp = film.image
                if (bmp != null) {
                    poster.setImageBitmap(bmp)
                } else {
                    poster.setImageResource(film.imageResId)
                }
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
                film = film,
                onImdb = {
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(film.imdbUrl))
                    startActivity(intent)
                },
                onEdit = { openEditFilm() },
                onBackToMain = { backToMain() }
            )
        }
    }


    @Composable
    fun FilmDataScreen(
        film: Film,
        onImdb: () -> Unit,
        onEdit: () -> Unit,
        onBackToMain: () -> Unit
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape =
            configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val genres = resources.getStringArray(R.array.genres)
        val formats = resources.getStringArray(R.array.formats)

        MyWindow {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                if (isLandscape) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {

                        // FOTO
                        val landscapeBitmap = film.image
                        if (landscapeBitmap != null) {
                            Image(
                                bitmap = landscapeBitmap.asImageBitmap(),
                                contentDescription = stringResource(R.string.film_poster),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(220.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = film.imageResId),
                                contentDescription = stringResource(R.string.film_poster),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(220.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))

                        // DATOS
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            MyText(film.title)

                            Spacer(modifier = Modifier.height(8.dp))

                            MyText(
                                stringResource(
                                    R.string.director,
                                    film.director
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            MyText(
                                stringResource(
                                    R.string.year,
                                    film.year
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            MyText(
                                stringResource(
                                    R.string.gen_and_format,
                                    genres[film.genre.ordinal],
                                    formats[film.format.ordinal]
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            MyText(film.comments)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // BOTONES
                        Column(
                            modifier = Modifier.width(150.dp)
                        ) {
                            Button(
                                onClick = onImdb,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.view_on_imdb))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = onEdit,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.edit_film))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = onBackToMain,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.back_to_main))
                            }
                        }
                    }

                } else {

                    // FOTO
                    val portraitBitmap = film.image
                    if (portraitBitmap != null) {
                        Image(
                            bitmap = portraitBitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.film_poster),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(160.dp)
                                .height(220.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = film.imageResId),
                            contentDescription = stringResource(R.string.film_poster),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(160.dp)
                                .height(220.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // DATOS
                    MyText(film.title)

                    Spacer(modifier = Modifier.height(8.dp))

                    MyText(
                        stringResource(
                            R.string.director,
                            film.director
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    MyText(
                        stringResource(
                            R.string.year,
                            film.year
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    MyText(
                        stringResource(
                            R.string.gen_and_format,
                            genres[film.genre.ordinal],
                            formats[film.format.ordinal]
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MyText(film.comments)

                    Spacer(modifier = Modifier.height(16.dp))

                    // BOTONES
                    Button(
                        onClick = onImdb,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.view_on_imdb))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.edit_film))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onBackToMain,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.back_to_main))
                        }
                    }
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
            film = sampleFilms().first(),
            onImdb = {},
            onEdit = {},
            onBackToMain = {}
        )
    }
}
