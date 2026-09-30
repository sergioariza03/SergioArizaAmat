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

class FilmDataActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILM_TITLE = "es.ua.eps.filmoteca.extra.FILM_TITLE"
    }
    private lateinit var bindings: ActivityFilmDataBinding
    private var label by mutableStateOf("")

    private fun showLabel(text: String) {
        when (GlobalMode) {
            Mode.Bindings -> bindings.FilmData.text = text
            Mode.Compose -> label = text
        }
    }
    private val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            showLabel(getString(R.string.film_edited, label))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        label = intent.getStringExtra(EXTRA_FILM_TITLE)
            ?: getString(R.string.film_data)

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

    private fun openEditFilm(){
        val intent = Intent(
            this,
            FilmEditActivity::class.java

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

    private fun initUIBindings(){
        bindings = ActivityFilmDataBinding.inflate(layoutInflater)

        with(bindings){
            setContentView(root)
            root.applySystemBarsPadding(appBar.root, content)
            setSupportActionBar(appBar.toolbar)

            val str = "${getString(R.string.using_mode)} ${GlobalMode.name}"
            mode.text = str
            FilmData.text = label

            btnRelatedfilm.setOnClickListener {
                openRelatedFilm()
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
