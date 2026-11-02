package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import es.ua.eps.filmoteca.databinding.ActivityFilmListDetailBinding
import es.ua.eps.filmoteca.ui.theme.FilmDataSource

class FilmListDetailActivity : AppCompatActivity(),
    FilmListFragment.OnFilmSelectedListener {

    private lateinit var bindings: ActivityFilmListDetailBinding

    private val addFilmLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult

                val film = IntentCompat.getParcelableExtra(
                    data,
                    FilmEditActivity.EXTRA_FILM,
                    Film::class.java
                ) ?: return@registerForActivityResult

                FilmDataSource.films.add(film)

                // Actualizamos el fragmento de lista si ya está creado.
                supportFragmentManager.fragments
                    .filterIsInstance<FilmListFragment>()
                    .forEach { fragment ->
                        fragment.refreshFilms()
                    }
            }
        }

    // Si existe el panel de datos, se ha cargado el layout de pantallas grandes
    private fun isTwoPane() = bindings.filmDataPane != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdgeWithAppBar()

        bindings = ActivityFilmListDetailBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        bindings.root.applySystemBarsPadding(
            bindings.appBar.root,
            bindings.content
        )

        setSupportActionBar(bindings.appBar.toolbar)

        // Con dos paneles los fragmentos ya vienen del layout. Con uno, ponemos la lista.
        // Al girar, el FragmentManager restaura solo lo que había: si añadiéramos
        // otra lista, habría dos
        if (!isTwoPane() && savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, FilmListFragment())
                .commit()
        }

        supportFragmentManager.addOnBackStackChangedListener { updateUpButton() }
        updateUpButton()
    }

    // Solo con un panel hay a dónde subir: a la lista, que está bajo los datos
    private fun updateUpButton() {
        val canGoUp = supportFragmentManager.backStackEntryCount > 0
        supportActionBar?.setDisplayHomeAsUpEnabled(canGoUp)

        if (canGoUp) {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        } else {
            // Quitar la flecha quita también el icono de la barra, que es el de la app
            bindings.appBar.toolbar.setNavigationIcon(R.drawable.app_bar_icon)
            bindings.appBar.toolbar.navigationContentDescription = null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onFilmSelected(position: Int) {
        val dataPane = supportFragmentManager
            .findFragmentById(R.id.filmDataPane) as FilmDataFragment?

        if (dataPane != null) {
            // Dos paneles: actualizamos el fragmento de datos que ya está en pantalla
            dataPane.showFilm(position)
        } else {
            // Un panel: cambiamos la lista por los datos, y Atrás vuelve a la lista
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FilmDataFragment.newInstance(position))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onFilmsDeleted(positions: List<Int>) {
        val dataPane = supportFragmentManager
            .findFragmentById(R.id.filmDataPane) as FilmDataFragment?

        dataPane?.onFilmsDeleted(positions)
    }

    fun addFilm() {
        val intent = Intent(
            this,
            FilmEditActivity::class.java
        )
        addFilmLauncher.launch(intent)
    }

    fun openAbout() {
        val intent = Intent(
            this,
            AboutActivity::class.java
        )
        startActivity(intent)
    }
}
