package com.aviad.chordstv.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aviad.chordstv.di.AppContainer
import com.aviad.chordstv.ui.home.HomeScreen
import com.aviad.chordstv.ui.song.SongScreen
import com.aviad.chordstv.ui.web.WebScreen

object Routes {
    const val HOME = "home"
    const val SONG = "song/{songId}"
    const val WEB = "web/{url}"
    fun song(id: String) = "song/$id"
    fun web(url: String) = "web/" + Uri.encode(url)
}

@Composable
fun AppNavigation(container: AppContainer) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                container = container,
                onOpenSong = { song -> navController.navigate(Routes.song(song.id)) },
                onOpenWeb = { url -> navController.navigate(Routes.web(url)) }
            )
        }
        composable(
            route = Routes.SONG,
            arguments = listOf(navArgument("songId") { type = NavType.StringType })
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId").orEmpty()
            SongScreen(
                container = container,
                songId = songId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.WEB,
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url").orEmpty()
            WebScreen(
                container = container,
                initialUrl = url,
                onExit = { navController.popBackStack() }
            )
        }
    }
}
