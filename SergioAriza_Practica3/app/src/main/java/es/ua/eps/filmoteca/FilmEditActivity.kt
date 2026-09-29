package es.ua.eps.filmoteca

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.databinding.ActivityFilmEditBinding
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import android.content.Intent
import android.widget.Toast
import androidx.core.content.IntentCompat



class FilmEditActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityFilmEditBinding
    private lateinit var film: Film

    companion object {
        const val EXTRA_FILM = "es.ua.eps.filmoteca.extra.FILM"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeWithAppBar()
        film = IntentCompat.getParcelableExtra(
            intent,
            EXTRA_FILM,
            Film::class.java
        ) ?: sampleFilms().first()
        initUI()
    }

    private fun initUI(){
        when (GlobalMode) {
            Mode.Bindings -> initUIBindings()
            Mode.Compose -> initUICompose()
        }
    }

    private fun saveFilm() {
        val genreIndex =
            bindings.genre.listSelection.takeIf { it >= 0 } ?: film.genre.ordinal

        val formatIndex =
            bindings.format.listSelection.takeIf { it >= 0 } ?: film.format.ordinal

        val edited = film.copy(
            title = bindings.title.text.toString(),
            director = bindings.director.text.toString(),
            year = bindings.year.text.toString().toIntOrNull() ?: 0,
            genre = Film.Genre.entries[genreIndex],
            format = Film.Format.entries[formatIndex],
            imdbUrl = bindings.imdbUrl.text.toString(),
            comments = bindings.comments.text.toString()
        )

        val result = Intent()
        result.putExtra(EXTRA_FILM, edited)

        setResult(Activity.RESULT_OK, result)
        finish()
    }

    private fun cancelEdit() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun initUIBindings(){
        bindings= ActivityFilmEditBinding.inflate(layoutInflater)

        with(bindings){
            setContentView(root)
            root.applySystemBarsPadding(appBar.root, content)
            setSupportActionBar(appBar.toolbar)

            val str = "${getString(R.string.using_mode)} ${GlobalMode.name}"
            mode.text = str

            poster.setImageResource(film.imageResId)

            title.setText(film.title)
            director.setText(film.director)
            year.setText(film.year.toString())

            imdbUrl.setText(film.imdbUrl)
            comments.setText(film.comments)

            val genres = resources.getStringArray(R.array.genres)
            val formats = resources.getStringArray(R.array.formats)

            genre.setText(genres[film.genre.ordinal], false)
            genre.setSelection(film.genre.ordinal)

            format.setText(formats[film.format.ordinal], false)
            format.setSelection(film.format.ordinal)

            btnTakePhoto.setOnClickListener {
                Toast.makeText(
                    this@FilmEditActivity,
                    "Not implemented",
                    Toast.LENGTH_SHORT
                ).show()
            }

            btnSelectImage.setOnClickListener {
                Toast.makeText(
                    this@FilmEditActivity,
                    "Not implemented",
                    Toast.LENGTH_SHORT
                ).show()
            }

            btnSave.setOnClickListener {
                saveFilm()
            }

            btnCancel.setOnClickListener {
                cancelEdit()
            }
        }
    }

    private fun initUICompose(){
        setContent {
            FilmEditScreen(
                onSave = { saveFilm() },
                onCancel = { cancelEdit() }
            )
        }
    }

    @Composable
    fun FilmEditScreen(
        onSave: () -> Unit,
        onCancel: () -> Unit
    ) {
        MyWindow {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MyText(stringResource(R.string.edit))

                Button(onClick = onSave) {
                    Text(stringResource(R.string.save))
                }

                Button(onClick = onCancel) {
                    Text(stringResource(R.string.cancel))
                }
            }

            MyText(
                "${stringResource(R.string.using_mode)} ${GlobalMode.name}"
            )
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun FilmEditScreenPreview() {
        FilmEditScreen(
            onSave = {},
            onCancel = {}
        )
    }


}

