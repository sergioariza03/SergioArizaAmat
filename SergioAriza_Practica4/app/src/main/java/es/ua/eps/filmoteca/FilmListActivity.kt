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
import es.ua.eps.filmoteca.ui.theme.FilmDataSource

class FilmListActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityFilmListBinding

    private lateinit var listAdapter: FilmArrayAdapter
    private lateinit var recyclerAdapter: FilmRecyclerAdapter
    private var openedFilm = -1

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

    override fun onResume() {
        super.onResume()
        if (::listAdapter.isInitialized) {
            listAdapter.notifyDataSetChanged()
        }
    }

    private fun viewFilm(position: Int) {
        val intent = Intent(
            this,
            FilmDataActivity::class.java
        )
        intent.putExtra(
            FilmDataActivity.EXTRA_FILM_INDEX,
            position
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

            listAdapter= FilmArrayAdapter(
                this@FilmListActivity,
                FilmDataSource.films
            )

            filmList.adapter = listAdapter
            filmList.setOnItemClickListener { _, _, position, _ ->
                viewFilm(position)
            }

            btnAbout.setOnClickListener {
                openAbout()
            }
        }
    }
    private fun initUICompose() {
        setContent {

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



