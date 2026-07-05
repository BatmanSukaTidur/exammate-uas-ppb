package com.exammate.app.presentation

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.exammate.app.presentation.login.LoginScreen
import com.exammate.app.presentation.login.RegisterScreen
import com.exammate.app.presentation.dashboard.DashboardScreen
import com.exammate.app.presentation.navigation.Screen
import com.exammate.app.presentation.navigation.bottomNavItems
import com.exammate.app.presentation.theme.Primary
import com.exammate.app.presentation.ujian.DaftarUjianScreen
import com.exammate.app.presentation.ujian.HalamanUjianScreen
import com.exammate.app.presentation.ujian.HasilUjianScreen
import com.exammate.app.presentation.ujian.PengumumanScreen
import com.exammate.app.presentation.ujian.ProfilScreen
import com.exammate.app.presentation.ujian.RingkasanUjianScreen
import com.exammate.app.presentation.ujian.ScoreResult

import com.exammate.app.presentation.teacher.TeacherDashboardScreen
import com.exammate.app.presentation.teacher.TeacherCreateExamScreen
import com.exammate.app.presentation.teacher.TeacherResultsScreen
import com.exammate.app.presentation.teacher.TeacherQuestionBankScreen
import com.exammate.app.presentation.teacher.TeacherAddQuestionScreen

private object ExamDataHolder {
    var selectedAnswers: Map<Int, Int> = emptyMap()
    var flaggedQuestions: Set<Int> = emptySet()
    var correctCount: Int = 0
    var totalCount: Int = 0
}

@Composable
fun ExammateApp() {
    val navController = rememberNavController()
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    isLoggedIn = true
                    val destination: String
                    when (role.uppercase()) {
                        "MURID" -> destination = Screen.Main.route
                        "GURU" -> destination = Screen.TeacherDashboard.route
                        else -> destination = Screen.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Student Main
        composable(Screen.Main.route) {
            MainScreen(
                onStartExam = { examId ->
                    navController.navigate(Screen.HalamanUjian.createRoute(examId))
                },
                onViewResult = { examId ->
                    navController.navigate(Screen.HasilUjian.createRoute(examId))
                },
                onLogout = {
                    isLoggedIn = false
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Teacher Dashboard
        composable(Screen.TeacherDashboard.route) {
            TeacherDashboardScreen(
                onCreateExam = {
                    navController.navigate(Screen.TeacherCreateExam.route)
                },
                onManageQuestions = {
                    navController.navigate(Screen.TeacherQuestionBank.route)
                },
                onViewResults = {
                    navController.navigate(Screen.TeacherResults.route)
                },
                onLogout = {
                    isLoggedIn = false
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.TeacherCreateExam.route) {
            TeacherCreateExamScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TeacherResults.route) {
            TeacherResultsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TeacherQuestionBank.route) {
            TeacherQuestionBankScreen(
                onBack = { navController.popBackStack() },
                onAddQuestion = { categoryId ->
                    navController.navigate(Screen.TeacherAddQuestion.createRoute(categoryId))
                }
            )
        }

        composable(
            route = Screen.TeacherAddQuestion.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            TeacherAddQuestionScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() }
            )
        }

        // Exam screens (shared between student & teacher for viewing)
        composable(
            route = Screen.HalamanUjian.route,
            arguments = listOf(
                navArgument("examId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getInt("examId") ?: 0

            HalamanUjianScreen(
                examId = examId,
                onFinish = { result ->
                    ExamDataHolder.selectedAnswers = result.selectedAnswers
                    ExamDataHolder.flaggedQuestions = result.flaggedQuestions
                    ExamDataHolder.correctCount = result.correctAnswers
                    ExamDataHolder.totalCount = result.totalQuestions
                    val score = if (result.totalQuestions > 0) (result.correctAnswers * 100 / result.totalQuestions) else 0
                    navController.navigate(
                        Screen.HasilUjian.createRoute(examId, score, result.totalQuestions, result.correctAnswers)
                    ) {
                        popUpTo(Screen.Main.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.RingkasanUjian.route,
            arguments = listOf(
                navArgument("examId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getInt("examId") ?: 0

            RingkasanUjianScreen(
                examId = examId,
                selectedAnswers = ExamDataHolder.selectedAnswers,
                flaggedQuestions = ExamDataHolder.flaggedQuestions,
                onSubmit = {
                    val correct = ExamDataHolder.correctCount
                    val total = ExamDataHolder.totalCount
                    val score = if (total > 0) (correct * 100 / total) else 0
                    navController.navigate(
                        Screen.HasilUjian.createRoute(examId, score, total, correct)
                    ) {
                        popUpTo(Screen.Main.route)
                    }
                },
                onBack = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.HasilUjian.route,
            arguments = listOf(
                navArgument("examId") { type = NavType.IntType },
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("correct") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getInt("examId") ?: 0
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0
            val correct = backStackEntry.arguments?.getInt("correct") ?: 0

            HasilUjianScreen(
                examId = examId,
                score = score,
                total = total,
                correct = correct,
                selectedAnswers = ExamDataHolder.selectedAnswers,
                onBackToHome = {
                    ExamDataHolder.selectedAnswers = emptyMap()
                    ExamDataHolder.flaggedQuestions = emptySet()
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreen(
    onStartExam: (Int) -> Unit,
    onViewResult: (Int) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Keluar Aplikasi") },
            text = { Text("Apakah kamu ingin keluar dari aplikasi?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    (context as? ComponentActivity)?.finishAndRemoveTask()
                }) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Tidak")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onStartExam = onStartExam
                )
            }

            composable(Screen.DaftarUjian.route) {
                DaftarUjianScreen(
                    onStartExam = onStartExam,
                    onViewResult = onViewResult
                )
            }

            composable(Screen.Pengumuman.route) {
                PengumumanScreen()
            }

            composable(Screen.Profil.route) {
                ProfilScreen(onLogout = onLogout)
            }
        }
    }
}
