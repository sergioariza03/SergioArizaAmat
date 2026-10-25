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
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class FilmListActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityFilmListBinding

    private lateinit var listAdapter: FilmArrayAdapter
    private lateinit var recyclerAdapter: FilmRecyclerAdapter
    private var openedFilm = -1

    private var films by mutableStateOf(FilmDataSource.films.toList())

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

        films = FilmDataSource.films.toList()

        if (GlobalMode == Mode.Bindings) {
            when (GlobalListMode) {
                ListMode.ListView -> {
                    if (::listAdapter.isInitialized) {
                        listAdapter.notifyDataSetChanged()
                    }
                }

                ListMode.RecyclerView -> {
                    if (::recyclerAdapter.isInitialized && openedFilm >= 0) {
                        recyclerAdapter.notifyItemChanged(openedFilm)
                    }
                }
            }
        }
    }

    private fun viewFilm(position: Int) {
        if (position !in FilmDataSource.films.indices) return

        openedFilm = position

        val intent = Intent(this, FilmDataActivity::class.java).apply {
            putExtra(FilmDataActivity.EXTRA_FILM_INDEX, position)
        }

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

            mode.text = "${getString(R.string.using_mode)} ${GlobalMode.name} (${GlobalListMode.name})"

            when (GlobalListMode) {
                ListMode.ListView -> initListView()
                ListMode.RecyclerView -> initRecyclerView()
            }

            btnAbout.setOnClickListener {
                openAbout()
            }
        }
    }

    private fun initListView() {
        bindings.filmList.visibility = View.VISIBLE
        bindings.filmRecycler.visibility = View.GONE

        listAdapter = FilmArrayAdapter(
            this,
            FilmDataSource.films
        )

        bindings.filmList.adapter = listAdapter

        bindings.filmList.setOnItemClickListener { _, _, position, _ ->
            viewFilm(position)
        }
    }

    private fun initRecyclerView() {
        bindings.filmList.visibility = View.GONE
        bindings.filmRecycler.visibility = View.VISIBLE

        recyclerAdapter = FilmRecyclerAdapter(FilmDataSource.films) { position ->
            viewFilm(position)
        }

        bindings.filmRecycler.layoutManager = LinearLayoutManager(this)
        bindings.filmRecycler.adapter = recyclerAdapter

        bindings.filmRecycler.addItemDecoration(
            MaterialDividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }
    private fun initUICompose() {
        setContent {
            MyWindow {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(films) { index, film ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewFilm(index)
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Poster(
                                poster = film.poster,
                                modifier = Modifier.size(
                                    width = 80.dp,
                                    height = 120.dp
                                )
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp)
                            ) {
                                Text(
                                    text = film.title,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = film.director)
                            }
                        }

                        if (index < films.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }

                Button(
                    onClick = { openAbout() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.about))
                }

                MyText(
                    "${stringResource(R.string.using_mode)} ${GlobalMode.name}"
                )
            }
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



