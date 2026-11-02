package es.ua.eps.filmoteca.ui.theme

import es.ua.eps.filmoteca.Film
import es.ua.eps.filmoteca.R

object FilmDataSource {
    val films: MutableList<Film> = mutableListOf(
        Film(
            title = "Star Wars: Episodio IV - Una nueva esperanza",
            director = "George Lucas",
            year = 1977,
            genre = Film.Genre.SciFi,
            format = Film.Format.BluRay,
            imdbUrl = "https://www.imdb.com/title/tt0076759/",
            comments = "Edición coleccionista",
            imageUrl = "https://www.adictosalcine.com/ver-caratulas/de-pelicula/star-wars-episodio-4.-una-nueva-esperanza/893/0/"
        ),
        Film(
            title = "El Señor de los Anillos: La Comunidad del Anillo",
            director = "Peter Jackson",
            year = 2001,
            genre = Film.Genre.SciFi,
            format = Film.Format.BluRay,
            imdbUrl = "https://www.imdb.com/title/tt0120737/",
            comments = "Edición 25 aniversario",
            imageUrl = "https://www.adictosalcine.com/ver-caratulas/de-pelicula/el-senor-de-los-anillos.-la-comunidad-del-anillo/1077/0/"
        ),
        Film(
            title = "El quinto elemento",
            director = "Luc Besson",
            year = 1997,
            genre = Film.Genre.SciFi,
            format = Film.Format.DVD,
            imdbUrl = "https://www.imdb.com/title/tt0119116/",
            comments = "",
            imageUrl = "https://www.adictosalcine.com/ver-caratulas/de-pelicula/el-quinto-elemento/4735/0/"
        ),
        Film(
            title = "Interstellar",
            director = "Christopher Nolan",
            year = 2014,
            genre = Film.Genre.SciFi,
            format = Film.Format.BluRay,
            imdbUrl = "https://www.imdb.com/title/tt0816692/",
            comments = "",
            imageUrl = "https://www.adictosalcine.com/ver-caratulas/de-pelicula/interstellar/30012/0/"
        ),
        Film(
            title = "Pulp Fiction",
            director = "Quentin Tarantino",
            year = 1994,
            genre = Film.Genre.Action,
            format = Film.Format.DVD,
            imdbUrl = "https://www.imdb.com/title/tt0110912/",
            comments = "Edición 30 aniversario",
            imageUrl = "https://www.adictosalcine.com/ver-caratulas/de-pelicula/pulp-fiction/5542/0/"
        ),
        Film(
            title = "El resplandor",
            director = "Stanley Kubrick",
            year = 1980,
            genre = Film.Genre.Horror,
            format = Film.Format.DVD,
            imdbUrl = "https://www.imdb.com/title/tt0081505/",
            comments = "",
            imageUrl = "https://www.adictosalcine.com/ver-caratulas/de-pelicula/el-resplandor/8828/0/"
        ),
        Film(
            title = "Regreso al futuro",
            director = "Robert Zemeckis",
            year = 1985,
            genre = Film.Genre.Comedy,
            format = Film.Format.BluRay,
            imdbUrl = "https://www.imdb.com/title/tt0088763/",
            comments = "",
            imageUrl = "https://www.adictosalcine.com/ver-caratulas/de-pelicula/regreso-al-futuro/1202/0/"
        ),
        Film(
            title = "El caballero oscuro",
            director = "Christopher Nolan",
            year = 2008,
            genre = Film.Genre.Action,
            format = Film.Format.BluRay,
            imdbUrl = "https://www.imdb.com/title/tt0468569/",
            comments = "",
            imageUrl = "https://www.adictosalcine.com/ver-caratulas/de-pelicula/el-caballero-oscuro-batman-begins-2/30713/1/"
        )
    )
}
