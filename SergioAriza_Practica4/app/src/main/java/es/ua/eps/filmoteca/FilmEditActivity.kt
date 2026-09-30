package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.core.content.IntentCompat
import es.ua.eps.filmoteca.databinding.ActivityFilmEditBinding
import androidx.compose.material3.MenuAnchorType
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Row
import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.os.BundleCompat



class FilmEditActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityFilmEditBinding
    private lateinit var film: Film
    private var savedGenre = -1
    private var savedFormat = -1

    private var currentImage: Bitmap? by mutableStateOf(null)

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            showImage(bitmap)
        }
    }

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            loadScaledBitmap(uri)?.let { showImage(it) }
        }
    }
    companion object {
        const val EXTRA_FILM = "es.ua.eps.filmoteca.extra.FILM"
        private const val STATE_GENRE = "state_genre"
        private const val STATE_FORMAT = "state_format"
        private const val STATE_IMAGE = "state_image"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeWithAppBar()

        film = IntentCompat.getParcelableExtra(
            intent,
            EXTRA_FILM,
            Film::class.java
        ) ?: sampleFilms().first()

        currentImage = savedInstanceState?.let {
            BundleCompat.getParcelable(it, STATE_IMAGE, Bitmap::class.java)
        } ?: film.image

        savedGenre = savedInstanceState?.getInt(STATE_GENRE, -1) ?: -1
        savedFormat = savedInstanceState?.getInt(STATE_FORMAT, -1) ?: -1

        initUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (GlobalMode == Mode.Bindings) {
            val genreIndex = resources.getStringArray(R.array.genres)
                .indexOf(bindings.genre.text.toString())

            val formatIndex = resources.getStringArray(R.array.formats)
                .indexOf(bindings.format.text.toString())

            outState.putInt(STATE_GENRE, genreIndex)
            outState.putInt(STATE_FORMAT, formatIndex)
        }

        outState.putParcelable(STATE_IMAGE, currentImage)

        super.onSaveInstanceState(outState)
    }

    private fun initUI(){
        when (GlobalMode) {
            Mode.Bindings -> initUIBindings()
            Mode.Compose -> initUICompose()
        }
    }

    private fun saveFilm() {
        val genreIndex = resources.getStringArray(R.array.genres)
            .indexOf(bindings.genre.text.toString())
            .takeIf { it >= 0 } ?: film.genre.ordinal

        val formatIndex = resources.getStringArray(R.array.formats)
            .indexOf(bindings.format.text.toString())
            .takeIf { it >= 0 } ?: film.format.ordinal

        val edited = film.copy(
            title = bindings.title.text.toString(),
            director = bindings.director.text.toString(),
            year = bindings.year.text.toString().toIntOrNull() ?: 0,
            genre = Film.Genre.entries[genreIndex],
            format = Film.Format.entries[formatIndex],
            imdbUrl = bindings.imdbUrl.text.toString(),
            comments = bindings.comments.text.toString(),
            image = currentImage ?: film.image
        )

        val result = Intent()
        result.putExtra(EXTRA_FILM, edited)

        setResult(Activity.RESULT_OK, result)
        finish()
    }

    private fun cancelEdit() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun showImage(bitmap: Bitmap) {
        currentImage = bitmap
        if (GlobalMode == Mode.Bindings) {
            bindings.poster.setImageBitmap(bitmap)
        }
    }

    // Loads a downscaled copy of the image at uri, so it fits in an Intent
    private fun loadScaledBitmap(uri: Uri, maxSize: Int = 400): Bitmap? {
        return try {
            val boundsInput = contentResolver.openInputStream(uri) ?: return null
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(boundsInput, null, bounds)
            boundsInput.close()

            var sampleSize = 1
            while (bounds.outWidth / sampleSize > maxSize * 2 ||
                bounds.outHeight / sampleSize > maxSize * 2
            ) {
                sampleSize *= 2
            }

            val decodeInput = contentResolver.openInputStream(uri) ?: return null
            val options = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            val decoded = BitmapFactory.decodeStream(decodeInput, null, options)
            decodeInput.close()

            decoded?.let { bitmap ->
                val scale = maxSize.toFloat() / maxOf(bitmap.width, bitmap.height)
                if (scale < 1f) {
                    Bitmap.createScaledBitmap(
                        bitmap,
                        (bitmap.width * scale).toInt(),
                        (bitmap.height * scale).toInt(),
                        true
                    )
                } else {
                    bitmap
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun initUIBindings(){
        bindings= ActivityFilmEditBinding.inflate(layoutInflater)

        with(bindings){
            setContentView(root)
            root.applySystemBarsPadding(appBar.root, content)
            setSupportActionBar(appBar.toolbar)

            val str = "${getString(R.string.using_mode)} ${GlobalMode.name}"
            mode.text = str

            val initialBitmap = currentImage
            if (initialBitmap != null) {
                poster.setImageBitmap(initialBitmap)
            } else {
                poster.setImageResource(film.imageResId)
            }

            title.setText(film.title)
            director.setText(film.director)
            year.setText(film.year.toString())

            imdbUrl.setText(film.imdbUrl)
            comments.setText(film.comments)

            val genres = resources.getStringArray(R.array.genres)
            val formats = resources.getStringArray(R.array.formats)

            val genreAdapter = ArrayAdapter(
                this@FilmEditActivity,
                android.R.layout.simple_dropdown_item_1line,
                genres
            )
            genre.setAdapter(genreAdapter)

            val formatAdapter = ArrayAdapter(
                this@FilmEditActivity,
                android.R.layout.simple_dropdown_item_1line,
                formats
            )
            format.setAdapter(formatAdapter)

            val genreIndex = if (savedGenre >= 0) savedGenre else film.genre.ordinal
            val formatIndex = if (savedFormat >= 0) savedFormat else film.format.ordinal

            genre.setText(genres[genreIndex], false)
            genre.dismissDropDown()

            format.setText(formats[formatIndex], false)
            format.dismissDropDown()

            btnTakePhoto.setOnClickListener {
                try {
                    takePhotoLauncher.launch(null)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        this@FilmEditActivity,
                        R.string.no_camera_app,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            btnSelectImage.setOnClickListener {
                selectImageLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }

            btnSave.setOnClickListener {
                saveFilm()
            }

            btnCancel.setOnClickListener {
                cancelEdit()
            }
        }
    }

    private fun initUICompose() {
        setContent {
            FilmEditScreen(
                film = film,
                onSave = { editedFilm ->
                    val result = Intent()
                    result.putExtra(EXTRA_FILM, editedFilm)
                    setResult(Activity.RESULT_OK, result)
                    finish()
                },
                onCancel = { cancelEdit() }
            )
        }
    }

    @Composable
    fun FilmEditScreen(
        film: Film,
        onSave: (Film) -> Unit,
        onCancel: () -> Unit
    ) {
        var title by rememberSaveable { mutableStateOf(film.title) }
        var director by rememberSaveable { mutableStateOf(film.director) }
        var year by rememberSaveable { mutableStateOf(film.year.toString()) }
        var imdbUrl by rememberSaveable { mutableStateOf(film.imdbUrl) }
        var comments by rememberSaveable { mutableStateOf(film.comments) }

        var genre by rememberSaveable {
            mutableIntStateOf(film.genre.ordinal)
        }

        var format by rememberSaveable {
            mutableIntStateOf(film.format.ordinal)
        }

        val genres = resources.getStringArray(R.array.genres)
        val formats = resources.getStringArray(R.array.formats)

        MyWindow {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                MyText(stringResource(R.string.edit))

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val bitmap = currentImage
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.film_poster),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(120.dp)
                                .height(165.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = film.imageResId),
                            contentDescription = stringResource(R.string.film_poster),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(120.dp)
                                .height(165.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = {
                                try {
                                    takePhotoLauncher.launch(null)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(
                                        this@FilmEditActivity,
                                        R.string.no_camera_app,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.take_photo))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                selectImageLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.select_image))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = {
                        Text(stringResource(R.string.field_title))
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = director,
                    onValueChange = { director = it },
                    label = {
                        Text(stringResource(R.string.field_director))
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = {
                        Text(stringResource(R.string.field_year))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                FilmDropdown(
                    label = stringResource(R.string.field_genre),
                    options = genres,
                    selectedIndex = genre,
                    onSelected = { genre = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                FilmDropdown(
                    label = stringResource(R.string.field_format),
                    options = formats,
                    selectedIndex = format,
                    onSelected = { format = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = imdbUrl,
                    onValueChange = { imdbUrl = it },
                    label = {
                        Text(stringResource(R.string.imdb_url))
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = {
                        Text(stringResource(R.string.comments))
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onSave(
                                film.copy(
                                    title = title,
                                    director = director,
                                    year = year.toIntOrNull() ?: 0,
                                    genre = Film.Genre.entries[genre],
                                    format = Film.Format.entries[format],
                                    imdbUrl = imdbUrl,
                                    comments = comments,
                                    image = currentImage ?: film.image
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.save))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }

            MyText(
                "${stringResource(R.string.using_mode)} ${GlobalMode.name}"
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FilmDropdown(
        label: String,
        options: Array<String>,
        selectedIndex: Int,
        onSelected: (Int) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = options[selectedIndex],
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(label)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = {
                            Text(option)
                        },
                        onClick = {
                            onSelected(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun FilmEditScreenPreview() {
        FilmEditScreen(
            film = sampleFilms().first(),
            onSave = {},
            onCancel = {}
        )
    }


}

