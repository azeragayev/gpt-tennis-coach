package com.aaa.gptenniscoach.nav

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aaa.gptenniscoach.data.repo.PlayerMeta
import com.aaa.gptenniscoach.feature.analyze.AnalyzeScreen
import com.aaa.gptenniscoach.feature.capture.CaptureScreen
import com.aaa.gptenniscoach.feature.history.HistoryScreen
import com.aaa.gptenniscoach.feature.preview.RawPreviewScreen
import com.aaa.gptenniscoach.feature.results.ResultsScreen
import com.aaa.gptenniscoach.feature.sessiondetail.SessionDetailScreen

/**
 * Sets up the navigation graph for the entire application with all screen destinations.
 */
@UnstableApi
@Composable
fun AppNavGraph() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Destinations.CAPTURE
    ) {

        /* ---------------- Capture ---------------- */

        composable(Destinations.CAPTURE) {
            val vm = hiltViewModel<com.aaa.gptenniscoach.feature.capture.CaptureViewModel>()

            CaptureScreen(
                vm = vm,
                onStartAnalyze = { videoUri, meta ->
                    nav.navigate(
                        "${Destinations.ANALYZE}?uri=${Uri.encode(videoUri.toString())}" +
                                "&level=${Uri.encode(meta.level)}" +
                                "&hand=${Uri.encode(meta.handedness)}" +
                                "&goal=${Uri.encode(meta.goal)}" +
                                "&lang=${Uri.encode(meta.language)}" +
                                "&detail=${Uri.encode(meta.detailMode)}"
                    )
                },
                onOpenHistory = {
                    nav.navigate(Destinations.HISTORY)
                },
                onPreviewVideo = { uri ->
                    nav.navigate(
                        Destinations.previewRaw(
                            uri = uri,
                            meta = vm.toMeta()
                        )
                    )
                }
            )
        }

        /* ---------------- Analyze ---------------- */

        composable(
            route = "${Destinations.ANALYZE}" +
                    "?uri={uri}&level={level}&hand={hand}&goal={goal}&lang={lang}&detail={detail}",
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
                navArgument("level") { type = NavType.StringType },
                navArgument("hand") { type = NavType.StringType },
                navArgument("goal") { type = NavType.StringType },
                navArgument("lang") { type = NavType.StringType },
                navArgument("detail") { type = NavType.StringType }
            )
        ) {
            val vm = hiltViewModel<com.aaa.gptenniscoach.feature.analyze.AnalyzeViewModel>()

            AnalyzeScreen(
                vm = vm,
                onDone = { sessionId ->
                    nav.navigate(Destinations.results(sessionId)) {
                        popUpTo(Destinations.CAPTURE) { inclusive = false }
                    }
                },
                onCancel = {
                    nav.popBackStack()
                }
            )
        }

        /* ---------------- Raw Preview ---------------- */

        composable(
            route = "${Destinations.PREVIEW_RAW}/{uri}" +
                    "?level={level}&hand={hand}&goal={goal}&lang={lang}&detail={detail}",
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
                navArgument("level") { type = NavType.StringType },
                navArgument("hand") { type = NavType.StringType },
                navArgument("goal") { type = NavType.StringType },
                navArgument("lang") { type = NavType.StringType },
                navArgument("detail") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val uri = backStackEntry.arguments!!
                .getString("uri")!!
                .toUri()

            val meta = PlayerMeta(
                level = backStackEntry.arguments!!.getString("level")!!,
                handedness = backStackEntry.arguments!!.getString("hand")!!,
                goal = backStackEntry.arguments!!.getString("goal")!!,
                language = backStackEntry.arguments!!.getString("lang")!!,
                detailMode = backStackEntry.arguments!!.getString("detail")!!
            )

            RawPreviewScreen(
                videoUri = uri,
                meta = meta,
                onAnalyze = { v, m ->
                    nav.navigate(
                        "${Destinations.ANALYZE}?uri=${Uri.encode(v.toString())}" +
                                "&level=${Uri.encode(m.level)}" +
                                "&hand=${Uri.encode(m.handedness)}" +
                                "&goal=${Uri.encode(m.goal)}" +
                                "&lang=${Uri.encode(m.language)}" +
                                "&detail=${Uri.encode(m.detailMode)}"
                    )
                },
                onBack = {
                    nav.popBackStack()
                }
            )
        }

        /* ---------------- Results ---------------- */

        composable(
            route = "${Destinations.RESULTS}/{sessionId}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) {
            ResultsScreen(
                vm = hiltViewModel(),
                onOpenHistory = {
                    nav.navigate(Destinations.HISTORY)
                },
                onOpenDetail = { sid ->
                    nav.navigate(Destinations.detail(sid))
                },
                onOpenPreview = { sid ->
                    nav.navigate(Destinations.preview(sid))
                },
                onBack = {
                    nav.popBackStack()
                }
            )
        }

        /* ---------------- History ---------------- */

        composable(Destinations.HISTORY) {
            HistoryScreen(
                vm = hiltViewModel(),
                onBack = {
                    nav.popBackStack()
                },
                onOpenSession = { sid ->
                    nav.navigate(Destinations.detail(sid))
                }
            )
        }

        /* ---------------- Session Detail ---------------- */

        composable(
            route = "${Destinations.DETAIL}/{sessionId}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) {
            SessionDetailScreen(
                vm = hiltViewModel(),
                onBack = {
                    nav.popBackStack()
                },
                onOpenResults = { sid ->
                    nav.navigate(Destinations.results(sid))
                }
            )
        }

        /* ---------------- Stored Preview ---------------- */

        composable(
            route = "${Destinations.PREVIEW}/{sessionId}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) {
            com.aaa.gptenniscoach.feature.preview.PreviewScreen(
                vm = hiltViewModel(),
                onBack = {
                    nav.popBackStack()
                }
            )
        }
    }
}
