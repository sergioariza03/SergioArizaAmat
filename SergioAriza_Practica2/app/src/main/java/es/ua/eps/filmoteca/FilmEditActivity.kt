package es.ua.eps.filmoteca

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.databinding.ActivityAboutBinding

class FilmEditActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityAboutBinding

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

    private fun initUIBindings(){
        bindings= ActivityAboutBinding.inflate(layoutInflater)

        with(bindings){
            setContentView(root)
            root.applySystemBarsPadding(appBar.root, content)
            setSupportActionBar(appBar.toolbar)

            val str = "${getString(R.string.using_mode)} ${GlobalMode.name}"
            mode.text = str

            btnWebsite.setOnClickListener {

            }

            btnSupport.setOnClickListener {
            }

            btnBack.setOnClickListener {

            }
        }
    }

    private fun initUICompose(){

    }


}

