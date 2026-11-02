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
import androidx.activity.result.contract.ActivityResultContracts
import android.view.Menu
import android.view.MenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.runtime.remember


class FilmListActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityFilmListBinding

    private lateinit var listAdapter: FilmArrayAdapter
    private lateinit var recyclerAdapter: FilmRecyclerAdapter
    private var openedFilm = -1

    private var recyclerActionMode: android.view.ActionMode? = null
    private val selectedFilms = mutableSetOf<Int>()

    private val selectionKey = "selected_films"

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.film_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_film -> {
                addFilm()
                true
            }

            R.id.action_about -> {
                openAbout()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    private val addFilmLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult

                val film = androidx.core.content.IntentCompat.getParcelableExtra(
                    data,
                    FilmEditActivity.EXTRA_FILM,
                    Film::class.java
                ) ?: return@registerForActivityResult

                val position = FilmDataSource.films.size
                FilmDataSource.films.add(film)
                films = FilmDataSource.films.toList()

                if (GlobalMode == Mode.Bindings) {
                    when (GlobalListMode) {
                        ListMode.ListView -> {
                            listAdapter.notifyDataSetChanged()
                            bindings.filmList.setSelection(position)
                        }

                        ListMode.RecyclerView -> {
                            recyclerAdapter.notifyItemInserted(position)
                            bindings.filmRecycler.scrollToPosition(position)
                        }
                    }
                }
            }
        }

    private var films by mutableStateOf(FilmDataSource.films.toList())
    private var selection by mutableStateOf(emptySet<Int>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeWithAppBar()
        initUI()

        val savedPositions = savedInstanceState
            ?.getIntegerArrayList(selectionKey)
            ?.toSet()
            ?: emptySet()

        when (GlobalMode) {
            Mode.Bindings -> {
                if (GlobalListMode == ListMode.RecyclerView &&
                    savedPositions.isNotEmpty()
                ) {
                    recyclerActionMode = startActionMode(recyclerActionModeCallback)

                    recyclerAdapter.restoreSelection(savedPositions)

                    savedPositions.forEach {
                        selectedFilms.add(it)
                    }

                    val count = recyclerAdapter.getSelectedPositions().size

                    recyclerActionMode?.title = resources.getQuantityString(
                        R.plurals.films_selected,
                        count,
                        count
                    )
                }
            }

            Mode.Compose -> {
                selection = savedPositions.filter {
                    it in films.indices
                }.toSet()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        when (GlobalMode) {
            Mode.Bindings -> {
                if (GlobalListMode == ListMode.RecyclerView &&
                    ::recyclerAdapter.isInitialized
                ) {
                    outState.putIntegerArrayList(
                        selectionKey,
                        ArrayList(recyclerAdapter.getSelectedPositions())
                    )
                }
            }

            Mode.Compose -> {
                outState.putIntegerArrayList(
                    selectionKey,
                    ArrayList(selection)
                )
            }
        }
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

    private fun addFilm() {
        val intent = Intent(this, FilmEditActivity::class.java)
        addFilmLauncher.launch(intent)
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

        bindings.filmList.choiceMode = android.widget.ListView.CHOICE_MODE_MULTIPLE_MODAL

        bindings.filmList.setMultiChoiceModeListener(
            object : android.widget.AbsListView.MultiChoiceModeListener {

                override fun onItemCheckedStateChanged(
                    mode: android.view.ActionMode,
                    position: Int,
                    id: Long,
                    checked: Boolean
                ) {
                    val count = bindings.filmList.checkedItemCount
                    mode.title = resources.getQuantityString(
                        R.plurals.films_selected,
                        count,
                        count
                    )
                }

                override fun onCreateActionMode(
                    mode: android.view.ActionMode,
                    menu: Menu
                ): Boolean {
                    menuInflater.inflate(R.menu.film_selection_menu, menu)

                    val count = bindings.filmList.checkedItemCount

                    mode.title = resources.getQuantityString(
                        R.plurals.films_selected,
                        count,
                        count
                    )

                    return true
                }

                override fun onPrepareActionMode(
                    mode: android.view.ActionMode,
                    menu: Menu
                ): Boolean = false

                override fun onActionItemClicked(
                    mode: android.view.ActionMode,
                    item: MenuItem
                ): Boolean {
                    return when (item.itemId) {
                        R.id.action_delete_selected -> {
                            val positions = bindings.filmList.checkedItemPositions
                            val selected = mutableListOf<Int>()

                            for (i in 0 until positions.size()) {
                                if (positions.valueAt(i)) {
                                    selected.add(positions.keyAt(i))
                                }
                            }

                            selected.sortedDescending().forEach { position ->
                                FilmDataSource.films.removeAt(position)
                            }

                            films = FilmDataSource.films.toList()
                            listAdapter.notifyDataSetChanged()
                            mode.finish()
                            true
                        }

                        else -> false
                    }
                }

                override fun onDestroyActionMode(mode: android.view.ActionMode) {
                    // La selección se limpia al cerrar el modo contextual.
                }
            }
        )

        bindings.filmList.setOnItemClickListener { _, _, position, _ ->
            viewFilm(position)
        }
    }


    private fun toggleRecyclerSelection(position: Int) {
        recyclerAdapter.toggleSelection(position)

        val count = recyclerAdapter.getSelectedPositions().size

        if (count == 0) {
            recyclerActionMode?.finish()
        } else {
            recyclerActionMode?.title = resources.getQuantityString(
                R.plurals.films_selected,
                count,
                count
            )
        }
    }

    private val recyclerActionModeCallback =
        object : android.view.ActionMode.Callback {

            override fun onCreateActionMode(
                mode: android.view.ActionMode,
                menu: Menu
            ): Boolean {
                mode.menuInflater.inflate(R.menu.film_selection_menu, menu)
                mode.title = resources.getQuantityString(
                    R.plurals.films_selected,
                    0,
                    0
                )
                return true
            }

            override fun onPrepareActionMode(
                mode: android.view.ActionMode,
                menu: Menu
            ): Boolean = false

            override fun onActionItemClicked(
                mode: android.view.ActionMode,
                item: MenuItem
            ): Boolean {
                return when (item.itemId) {
                    R.id.action_delete_selected -> {
                        val positions = recyclerAdapter
                            .getSelectedPositions()
                            .sortedDescending()

                        positions.forEach { position ->
                            if (position in FilmDataSource.films.indices) {
                                FilmDataSource.films.removeAt(position)
                            }
                        }

                        films = FilmDataSource.films.toList()
                        recyclerAdapter.notifyDataSetChanged()
                        mode.finish()
                        true
                    }

                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: android.view.ActionMode) {
                recyclerAdapter.clearSelection()
                recyclerActionMode = null
                selectedFilms.clear()
            }
        }


    private fun initRecyclerView() {
        bindings.filmList.visibility = View.GONE
        bindings.filmRecycler.visibility = View.VISIBLE

        recyclerAdapter = FilmRecyclerAdapter(
            FilmDataSource.films,
            onClick = { position ->
                if (recyclerActionMode != null) {
                    toggleRecyclerSelection(position)
                } else {
                    viewFilm(position)
                }
            },
            onLongClick = { position ->
                if (recyclerActionMode == null) {
                    recyclerActionMode = startActionMode(recyclerActionModeCallback)
                }

                toggleRecyclerSelection(position)
            }
        )

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
            BackHandler(enabled = selection.isNotEmpty()) {
                clearSelection()
            }

            MyWindow(
                title = if (selection.isNotEmpty()) {
                    pluralStringResource(
                        R.plurals.films_selected,
                        selection.size,
                        selection.size
                    )
                } else {
                    stringResource(R.string.app_name)
                },
                navigationIcon = {
                    if (selection.isNotEmpty()) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cancel)
                        )
                    } else {
                        AppBarIcon()
                    }
                },
                onNavigationClick = {
                    if (selection.isNotEmpty()) {
                        clearSelection()
                    }
                },
                actions = {
                    if (selection.isNotEmpty()) {
                        IconButton(onClick = { deleteSelectedFilms() }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    } else {
                        IconButton(onClick = { addFilm() }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_film)
                            )
                        }
                        OverflowMenu(onAbout = { openAbout() })
                    }
                }
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(films) { index, film ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (index in selection) {
                                        MaterialTheme.colorScheme.secondaryContainer
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .combinedClickable(
                                    onClick = {
                                        if (selection.isNotEmpty()) {
                                            toggleSelection(index)
                                        } else {
                                            viewFilm(index)
                                        }
                                    },
                                    onLongClick = {
                                        toggleSelection(index)
                                    }
                                )
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
                MyText(
                    "${stringResource(R.string.using_mode)} ${GlobalMode.name} (${GlobalListMode.name})"
                )
            }
        }
    }

    private fun toggleSelection(position: Int) {
        selection = if (position in selection) {
            selection - position
        } else {
            selection + position
        }
    }

    private fun clearSelection() {
        selection = emptySet()
    }

    private fun deleteSelectedFilms() {
        selection.sortedDescending().forEach { position ->
            if (position in FilmDataSource.films.indices) {
                FilmDataSource.films.removeAt(position)
            }
        }

        films = FilmDataSource.films.toList()
        clearSelection()
    }

    @Composable
    private fun OverflowMenu(onAbout: () -> Unit) {
        var expanded by remember { mutableStateOf(false) }

        androidx.compose.foundation.layout.Box {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more_options)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.about)) },
                    onClick = {
                        expanded = false
                        onAbout()
                    }
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



