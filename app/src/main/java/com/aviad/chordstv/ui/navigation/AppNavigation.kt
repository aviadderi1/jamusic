package com.aviad.chordstv.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aviad.chordstv.di.AppContainer
import com.aviad.chordstv.ui.home.HomeScreen
import com.aviad.chordstv.ui.song.SongScreen

object Routes {
    const val HOME = "home"
    const val SONG = "song/{songId}"
    fun song(id: String) = "song/$id"
}

@Composable
fun AppNavigation(container: AppContainer) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                container = container,
                onOpenSong = { song -> navController.navigate(Routes.song(song.id)) }
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
    }
}
