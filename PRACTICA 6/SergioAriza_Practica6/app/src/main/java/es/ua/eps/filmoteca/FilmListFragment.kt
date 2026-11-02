package es.ua.eps.filmoteca

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import es.ua.eps.filmoteca.databinding.FragmentFilmListBinding
import es.ua.eps.filmoteca.ui.theme.FilmDataSource


class FilmListFragment : Fragment() {

    // La lista no sabe cuántos paneles hay: avisa a la actividad y ella decide
    interface OnFilmSelectedListener {
        fun onFilmSelected(position: Int)
        fun onFilmsDeleted(positions: List<Int>)
    }

    companion object {
        private const val STATE_SELECTION = "selected_films"
    }

    private var listener: OnFilmSelectedListener? = null

    private var _bindings: FragmentFilmListBinding? = null
    private val bindings get() = _bindings!!

    private lateinit var listAdapter: FilmArrayAdapter
    private lateinit var recyclerAdapter: FilmRecyclerAdapter

    private var recyclerActionMode: ActionMode? = null

    // Selección guardada antes de girar, se aplica en onStart (con RecyclerView)
    private var pendingSelection: List<Int>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnFilmSelectedListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bindings = FragmentFilmListBinding.inflate(
            inflater,
            container,
            false
        )
        return bindings.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        pendingSelection = savedInstanceState?.getIntegerArrayList(STATE_SELECTION)

        initUI()

        requireActivity().addMenuProvider(
            object : MenuProvider {

                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {
                    menuInflater.inflate(R.menu.film_list_menu, menu)
                }

                override fun onMenuItemSelected(
                    menuItem: MenuItem
                ): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_add_film -> {
                            (requireActivity() as FilmListDetailActivity).addFilm()
                            true
                        }

                        R.id.action_about -> {
                            (requireActivity() as FilmListDetailActivity).openAbout()
                            true
                        }

                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    override fun onStart() {
        super.onStart()

        // Con RecyclerView el modo contextual lo hemos creado nosotros, así que
        // hay que volver a abrirlo y marcar lo que estaba seleccionado.
        // El ListView restaura él solo su selección
        val saved = pendingSelection ?: return
        pendingSelection = null

        if (GlobalListMode == ListMode.RecyclerView && saved.isNotEmpty()) {
            recyclerActionMode =
                (requireActivity() as AppCompatActivity)
                    .startSupportActionMode(recyclerActionModeCallback)

            recyclerAdapter.restoreSelection(saved.toSet())
            updateRecyclerSelection()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (GlobalListMode == ListMode.RecyclerView &&
            ::recyclerAdapter.isInitialized
        ) {
            outState.putIntegerArrayList(
                STATE_SELECTION,
                ArrayList(recyclerAdapter.getSelectedPositions())
            )
        }
    }

    private fun initUI() {
        bindings.mode.text =
            "${getString(R.string.using_mode)} ${GlobalMode.name} (${GlobalListMode.name})"

        when (GlobalListMode) {
            ListMode.ListView -> initListView()
            ListMode.RecyclerView -> initRecyclerView()
        }
    }

    fun refreshFilms() {
        when (GlobalListMode) {
            ListMode.ListView -> {
                if (::listAdapter.isInitialized) {
                    listAdapter.notifyDataSetChanged()
                }
            }

            ListMode.RecyclerView -> {
                if (::recyclerAdapter.isInitialized) {
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun initListView() {
        bindings.filmList.visibility = View.VISIBLE
        bindings.filmRecycler.visibility = View.GONE

        listAdapter = FilmArrayAdapter(
            requireContext(),
            FilmDataSource.films
        )

        bindings.filmList.adapter = listAdapter

        bindings.filmList.setOnItemClickListener { _, _, position, _ ->
            listener?.onFilmSelected(position)
        }

        bindings.filmList.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL

        bindings.filmList.setMultiChoiceModeListener(
            object : AbsListView.MultiChoiceModeListener {

                override fun onCreateActionMode(
                    mode: android.view.ActionMode,
                    menu: Menu
                ): Boolean {
                    requireActivity().menuInflater.inflate(
                        R.menu.film_selection_menu,
                        menu
                    )

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
                            val checkedPositions =
                                bindings.filmList.checkedItemPositions

                            val positions = mutableListOf<Int>()

                            for (i in 0 until checkedPositions.size()) {
                                if (checkedPositions.valueAt(i)) {
                                    positions.add(checkedPositions.keyAt(i))
                                }
                            }

                            positions.sortedDescending().forEach { position ->
                                FilmDataSource.films.removeAt(position)
                            }

                            listAdapter.notifyDataSetChanged()
                            listener?.onFilmsDeleted(positions)
                            mode.finish()
                            true
                        }

                        else -> false
                    }
                }

                override fun onDestroyActionMode(
                    mode: android.view.ActionMode
                ) {
                    // No es necesario realizar ninguna acción adicional.
                }

                override fun onItemCheckedStateChanged(
                    mode: android.view.ActionMode,
                    position: Int,
                    id: Long,
                    checked: Boolean
                ) {
                    mode.title = resources.getQuantityString(
                        R.plurals.films_selected,
                        bindings.filmList.checkedItemCount,
                        bindings.filmList.checkedItemCount
                    )
                }
            }
        )
    }

    private fun initRecyclerView() {
        bindings.filmList.visibility = View.GONE
        bindings.filmRecycler.visibility = View.VISIBLE

        recyclerAdapter = FilmRecyclerAdapter(
            FilmDataSource.films,
            onClick = { position ->
                if (recyclerActionMode != null) {
                    recyclerAdapter.toggleSelection(position)
                    updateRecyclerSelection()
                } else {
                    listener?.onFilmSelected(position)
                }
            },
            onLongClick = { position ->
                if (recyclerActionMode == null) {
                    recyclerActionMode =
                        (requireActivity() as AppCompatActivity)
                            .startSupportActionMode(recyclerActionModeCallback)
                }

                recyclerAdapter.toggleSelection(position)
                updateRecyclerSelection()
            }
        )

        bindings.filmRecycler.layoutManager = LinearLayoutManager(requireContext())
        bindings.filmRecycler.adapter = recyclerAdapter

        bindings.filmRecycler.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
    }

    // Pone el título del modo contextual, y lo cierra si no queda nada seleccionado
    private fun updateRecyclerSelection() {
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
        object : ActionMode.Callback {

            override fun onCreateActionMode(
                mode: ActionMode,
                menu: Menu
            ): Boolean {
                mode.menuInflater.inflate(
                    R.menu.film_selection_menu,
                    menu
                )
                return true
            }

            override fun onPrepareActionMode(
                mode: ActionMode,
                menu: Menu
            ): Boolean = false

            override fun onActionItemClicked(
                mode: ActionMode,
                item: MenuItem
            ): Boolean {
                return when (item.itemId) {
                    R.id.action_delete_selected -> {
                        val positions = recyclerAdapter
                            .getSelectedPositions()
                            .toList()

                        positions
                            .sortedDescending()
                            .forEach { position ->
                                FilmDataSource.films.removeAt(position)
                            }

                        recyclerAdapter.notifyDataSetChanged()
                        listener?.onFilmsDeleted(positions)
                        mode.finish()
                        true
                    }

                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                recyclerAdapter.clearSelection()
                recyclerActionMode = null
            }
        }


    override fun onResume() {
        super.onResume()

        // Por si se ha editado o añadido una película mientras no estábamos
        if (_bindings != null) {
            refreshFilms()
        }
    }

    override fun onDestroyView() {
        recyclerActionMode?.finish()
        _bindings = null
        super.onDestroyView()
    }
}
