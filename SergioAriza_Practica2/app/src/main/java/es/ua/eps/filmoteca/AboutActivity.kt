package es.ua.eps.filmoteca

import android.content.Intent
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import es.ua.eps.filmoteca.databinding.ActivityAboutBinding
import android.content.ActivityNotFoundException
import android.net.Uri

class AboutActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityAboutBinding

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

    private fun OpenWebsite(){
        val viewIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.ua.es")
        )

        try{
            startActivity(viewIntent)
        } catch(e: ActivityNotFoundException){
            Toast.makeText(
                this,
                R.string.no_app_available,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getSupport(){
        val emailIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("mailto:saa64@alu.ua.es")
        )
        try{
            startActivity(emailIntent)
        } catch(e: ActivityNotFoundException){
            Toast.makeText(
                this,
                R.string.no_app_available,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun goBack(){
        finish()
    }
    private fun initUIBindings() {
        bindings = ActivityAboutBinding.inflate(layoutInflater)

        with(bindings) {
            setContentView(root)
            root.applySystemBarsPadding(appBar.root, content)
            setSupportActionBar(appBar.toolbar)

            val str = "${getString(R.string.using_mode)} ${GlobalMode.name}"
            mode.text = str

            btnWebsite.setOnClickListener {
                OpenWebsite()
            }

            btnSupport.setOnClickListener {
                getSupport()
            }

            btnBack.setOnClickListener {
                goBack()
            }
        }
    }

    private fun initUICompose() {
        setContent {
            AboutScreen()
        }
    }

    @Composable
    fun AboutScreen() {

        val context = LocalContext.current
        val resources = LocalResources.current

        MyWindow {

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.created_by)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(R.drawable.user),
                    contentDescription = stringResource(R.string.about_image_description),
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                resources.getString(R.string.not_implemented),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Text(stringResource(R.string.go_to_website))
                    }

                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                resources.getString(R.string.not_implemented),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Text(stringResource(R.string.get_support))
                    }

                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                resources.getString(R.string.not_implemented),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Text(stringResource(R.string.back))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            MyText(
                "${stringResource(R.string.using_mode)} ${GlobalMode.name}"
            )
        }
    }

    @Preview(showSystemUi = true, name = "Light Mode")
    @Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode")
    @Composable
    fun AboutScreenPreview() {
        AboutScreen()
    }
}
