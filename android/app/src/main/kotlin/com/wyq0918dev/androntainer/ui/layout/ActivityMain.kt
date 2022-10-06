package com.wyq0918dev.androntainer.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wyq0918dev.androntainer.R
import com.wyq0918dev.androntainer.app.screen.Screen
import com.wyq0918dev.androntainer.fragment.Settings
import com.wyq0918dev.androntainer.ui.theme.AndrontainerTheme

@Composable
fun ActivityMainLayout() {
    val scaffoldState = rememberScaffoldState()
    val expandedMenu = remember { mutableStateOf(false) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val items = listOf(
        Screen.Navigation,
        Screen.Launcher,
        Screen.Flutter,
        Screen.Dashboard,
        Screen.Settings
    )
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "sb")
                },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            expandedMenu.value = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = expandedMenu.value,
                        onDismissRequest = {
                            expandedMenu.value = false
                        },
                    ) {
                        DropdownMenuItem(
                            onClick = {

                                expandedMenu.value = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null
                            )
                            Spacer(
                                Modifier.size(
                                    ButtonDefaults.IconSpacing
                                )
                            )
                            Text(text = "设置")
                        }
                        Divider()
                        DropdownMenuItem(
                            onClick = {

                                expandedMenu.value = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = null
                            )
                            Spacer(
                                Modifier.size(
                                    ButtonDefaults.IconSpacing
                                )
                            )
                            Text(text = "抽屉")
                        }
                        Divider()
                        DropdownMenuItem(
                            onClick = {
                                expandedMenu.value = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = null
                            )
                            Spacer(
                                Modifier.size(
                                    ButtonDefaults.IconSpacing
                                )
                            )
                            Text("更多")
                        }
                    }
                },
                elevation = 10.dp
            )
        },
        bottomBar = {
            BottomNavigation(
                modifier = Modifier.fillMaxWidth(),
                elevation = 10.dp
            ) {
//                items.forEach { screen ->
//                    NavigationBarItem(
//                        icon = { Icon(screen.imageVector, contentDescription = null) },
//                        label = { Text(stringResource(id = screen.resourceId)) },
//                        selected = currentDestination?.hierarchy?.any {
//                            it.route == screen.route
//                        } == true,
//                        onClick = { navController.navigate(
//                                screen.route
//                            ) {
//                                popUpTo(
//                                    navController.graph.findStartDestination().id
//                                ) {
//                                    saveState = true
//                                }
//                                launchSingleTop = true
//                                restoreState = true
//                            }
//                        }
//                    )
//                }
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                screen.imageVector,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                stringResource(screen.resourceId)
                            )
                        },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true,
                        onClick = {
                            navController.navigate(
                                screen.route
                            ) {
                                popUpTo(
                                    navController.graph.findStartDestination().id
                                ) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Flutter.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Navigation.route) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                }
            }
            composable(Screen.Launcher.route) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                }
            }
            composable(Screen.Flutter.route) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Button(
                        onClick = {
//                            findViewById<FragmentContainerView>(flutterId).visibility = ViewGroup.VISIBLE
                        }
                    ) {
                        Text(text = "open")
                    }
//                        AndroidView(
//                            factory = { context ->
//                                FragmentContainerView(
//                                    context
//                                ).apply {
//                                    id = flutterId
//                                    val fragmentManager: FragmentManager = supportFragmentManager
//                                    flutterFragment =
//                                        fragmentManager.findFragmentByTag(tagFlutterFragment) as FlutterFragment?
//                                    if (flutterFragment == null) {
//                                        flutterFragment = FlutterFragment
//                                            .withNewEngine()
//                                            .shouldAttachEngineToActivity(true)
//                                            .build()
//                                        fragmentManager
//                                            .beginTransaction()
//                                            .add(id, flutterFragment!!, tagFlutterFragment)
//                                            .commit()
//                                    }
//                                }
//                            },
//                            modifier = Modifier.fillMaxSize()
//                        )
                }
            }
            composable(Screen.Dashboard.route) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                }
            }
            composable(Screen.Settings.route) {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
//
//
//                }

            }
        }


    }


}

//    @Composable
//    private fun WidgetFlutter(modifier: Modifier) {
//        AndroidView(
//            factory = { context ->
//                FragmentContainerView(
//                    context
//                ).apply {
//                    id = flutterId
//                    val fragmentManager: FragmentManager = supportFragmentManager
//                    flutterFragment =
//                        fragmentManager.findFragmentByTag(tagFlutterFragment) as FlutterFragment?
//                    if (flutterFragment == null) {
//                        flutterFragment = FlutterFragment
//                            .withNewEngine()
//                            .shouldAttachEngineToActivity(true)
//                            .build()
//                        fragmentManager
//                            .beginTransaction()
//                            .add(
//                                this.id,
//                                flutterFragment!!,
//                                tagFlutterFragment
//                            )
//                            .commit()
//                    }
//                }
//            },
//            modifier = modifier
//        )
//    }

//@Composable
//private fun WidgetSettings(modifier: Modifier) {
//    AndroidView(
//        factory = { context ->
//            FragmentContainerView(
//                context
//            ).apply {
//                id = settingsId
//            }
//        },
//        modifier = modifier
//    )
//}

@Preview
@Composable
private fun ActivityMainLayoutPreview() {
    AndrontainerTheme {
        ActivityMainLayout()
    }
}