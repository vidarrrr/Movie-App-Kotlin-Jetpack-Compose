package com.movie.app.jetpack

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.movie.app.jetpack.common.Constants
import com.movie.app.jetpack.common.Navigation
import com.movie.app.jetpack.data.repository.MovieRepository
import com.movie.app.jetpack.data.source.local.SharedPrefs
import com.movie.app.jetpack.model.MovieModel
import com.movie.app.jetpack.ui.screens.details.CardViewStar
import com.movie.app.jetpack.ui.screens.details.MovieDetailsScreen
import com.movie.app.jetpack.ui.screens.home.MainLayout
import com.movie.app.jetpack.ui.theme.MovieAppJetpackTheme
import com.movie.app.jetpack.ui.viewmodel.MovieViewModel
import com.movie.app.jetpack.ui.viewmodel.ViewModelFactory
import com.movie.app.jetpack.ui.viewmodel.model.MovieTypes
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelKey = "MovieViewModel"

        setContent {
            MovieAppJetpackTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val owner = LocalViewModelStoreOwner.current
                    owner?.let {
                        val movieViewModel: MovieViewModel = viewModel(
                            it,
                            viewModelKey,
                            ViewModelFactory(MovieRepository()) { error ->
                                printMessage(applicationContext, error)
                            }
                        )
                        movieViewModel.getMovies(MovieTypes.THEATER.type)
                        val mutableIsOpenedDrawer = remember {
                            mutableStateOf(true)
                        }
                        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                        val scope = rememberCoroutineScope()
                        val openDrawer = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                        val context = LocalContext.current
                        val tempList = remember {
                            mutableStateOf(getWatchList(context))
                        }
                        ModalDrawer(drawerState = drawerState,
                            gesturesEnabled = mutableIsOpenedDrawer.value,//drawerState.isOpen,
                            drawerContent = {
                                Drawer(tempList.value)
                            }, content = {
                                val navController = rememberNavController()
                                NavHost(
                                    navController = navController,
                                    startDestination = Navigation.HOME.navName
                                ) {
                                    //https://www.youtube.com/watch?v=V1UtZALhhg0 pass data
                                    composable(Navigation.HOME.navName) {
                                        mutableIsOpenedDrawer.value = true
                                        MainLayout(movieViewModel, { movieModel ->
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                Constants.MOVIE_MODEL_KEY,
                                                movieModel
                                            )
                                            navController.navigate(Navigation.MOVIE_DETAILS.navName)
                                        }, {
                                            tempList.value = getWatchList(context)
                                            openDrawer()
                                        })
                                    }
                                    composable(Navigation.MOVIE_DETAILS.navName) {
                                        mutableIsOpenedDrawer.value = false
                                        val result =
                                            navController.previousBackStackEntry?.savedStateHandle?.get<MovieModel>(
                                                Constants.MOVIE_MODEL_KEY
                                            )
                                        result?.let { movieModel ->
                                            MovieDetailsScreen(movieModel)
                                        }

                                    }

                                }
                            })


                    }
                    //https://developer.android.com/jetpack/compose/navigation

                }
            }
        }
    }

    private fun getWatchList(context: Context): List<String> {
        val sharedPrefs = SharedPrefs()
        val index = sharedPrefs.getParamMenuIndex(context)
        val list = mutableListOf<String>()
        for (value in 0 until index) {

            sharedPrefs.getParamString(
                context,
                "${Constants.MENU_ITEM_SUFFIX}${value}"
            )?.let {
                list.add(
                    it
                )
            }
        }
        return list
    }


    @Composable
    private fun Drawer(watchList: List<String>) {
        Column {
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .background(Color(0xffBB86FC)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "WATCH LIST",
                    modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 32.sp
                    )
                )
            }

            for (item in watchList) {
                Text(item, modifier = Modifier.padding(16.dp, 12.dp, 0.dp, 12.dp))
            }
        }


    }

    private fun printMessage(context: Context, message: String?) {
        message?.let {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MovieAppJetpackTheme {

            /*val textState = remember { mutableStateOf(TextFieldValue("")) }
        SearchViewCompose(textState)

        val mutableSelectedTab = remember {
            mutableStateOf(0)
        }
        val mutableSelectedChip = remember {
            mutableStateOf(0)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Tab("In theater", 0, mutableSelectedTab)
            Tab("Box Office", 1, mutableSelectedTab)
            Tab("Coming Soon", 2, mutableSelectedTab)
            Tab("Coming Soon", 2, mutableSelectedTab)
            Tab("Coming Soon", 2, mutableSelectedTab)
            Tab("Coming Soon", 2, mutableSelectedTab)
            Tab("Coming Soon", 2, mutableSelectedTab)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ChipCustom(text = "Action", index = 0, state = mutableSelectedChip)
            ChipCustom(text = "Crime", index = 1, state = mutableSelectedChip)
            ChipCustom(text = "Comedy", index = 2, state = mutableSelectedChip)
            ChipCustom(text = "Drama", index = 3, state = mutableSelectedChip)
            ChipCustom(text = "Action1", index = 4, state = mutableSelectedChip)
            ChipCustom(text = "Action2", index = 5, state = mutableSelectedChip)
        }*/
            /*CardViewStar(
                modifier = Modifier
                    .height(112.dp)
                    .padding(0.dp, 0.dp, 16.dp, 0.dp),
                "10",
                "213,123",
                64,
                "523,211 critic reviews"
            )*/

            Drawer(listOf("a"))


        }
    }
}