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



class FilmEditActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityFilmEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeWithAppBar()
        initUI()
    }

    private fun initUI(){
        when (GlobalMode) {
            Mode.Bindings -> initUIBindings()
            Mode.Compose -> initUICompose()
        }
    }

    private fun saveFilm() {
        setResult(Activity.RESULT_OK)
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

