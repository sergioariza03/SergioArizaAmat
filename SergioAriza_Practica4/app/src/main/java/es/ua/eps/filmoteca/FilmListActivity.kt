package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding
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

class FilmListActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityFilmListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeWithAppBar()
        initUI()
    }

    private fun initUI() {
        when (GlobalMode) {
            Mode.Bindings -> initUIBindings()
            Mode.Compose -> initUICompose()
        }
    }

    private fun openFilmA() {
        val intent = Intent(
            this,
            FilmDataActivity::class.java
        )
        intent.putExtra(
            FilmDataActivity.EXTRA_FILM_TITLE,
            getString(R.string.film_a_title)
        )
        startActivity(intent)
    }

    private fun openFilmB() {
        val intent = Intent(
            this,
            FilmDataActivity::class.java
        )
        intent.putExtra(
            FilmDataActivity.EXTRA_FILM_TITLE,
            getString(R.string.film_b_title)
        )
        startActivity(intent)
    }

    private fun openAbout() {
        val intent = Intent(
            this,
            AboutActivity::class.java
        )
        startActivity(intent)
    }

    private fun initUIBindings() {
        bindings = ActivityFilmListBinding.inflate(layoutInflater)

        with(bindings) {
            setContentView(root)
            root.applySystemBarsPadding(appBar.root, content)
            setSupportActionBar(appBar.toolbar)

            val str = "${getString(R.string.using_mode)} ${GlobalMode.name}"
            mode.text = str

            btnFilmA.setOnClickListener {
                openFilmA()
            }

            btnFilmB.setOnClickListener {
                openFilmB()
            }

            btnAbout.setOnClickListener {
                openAbout()
            }
        }
    }

    private fun initUICompose() {
        setContent {
            FilmListScreen(
                onFilmA = { openFilmA() },
                onFilmB = { openFilmB() },
                onAbout = { openAbout() }
            )
        }
    }

    @Composable
    fun FilmListScreen(
        onFilmA: () -> Unit,
        onFilmB: () -> Unit,
        onAbout: () -> Unit
    ) {
        MyWindow {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = onFilmA) {
                    Text(stringResource(R.string.film_a))
                }

                Button(onClick = onFilmB) {
                    Text(stringResource(R.string.film_b))
                }

                Button(onClick = onAbout) {
                    Text(stringResource(R.string.about))
                }
            }

            MyText(
                "${stringResource(R.string.using_mode)} ${GlobalMode.name}"
            )
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun FilmListScreenPreview() {
        FilmListScreen(
            onFilmA = {},
            onFilmB = {},
            onAbout = {}
        )
    }
}



