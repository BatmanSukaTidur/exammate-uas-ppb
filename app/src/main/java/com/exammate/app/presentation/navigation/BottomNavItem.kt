package com.exammate.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Beranda", Icons.Default.Home, Screen.Dashboard.route),
    BottomNavItem("Ujian", Icons.Default.Assignment, Screen.DaftarUjian.route),
    BottomNavItem("Pengumuman", Icons.Default.Campaign, Screen.Pengumuman.route),
    BottomNavItem("Profil", Icons.Default.Person, Screen.Profil.route)
)
