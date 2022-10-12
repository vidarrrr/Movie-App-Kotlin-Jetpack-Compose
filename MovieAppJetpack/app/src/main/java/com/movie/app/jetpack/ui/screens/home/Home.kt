package com.movie.app.jetpack.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.movie.app.jetpack.R
import com.movie.app.jetpack.model.MovieModel
import com.movie.app.jetpack.ui.screens.ChipTypes
import com.movie.app.jetpack.ui.theme.Purple200
import com.movie.app.jetpack.ui.theme.Purple700
import com.movie.app.jetpack.ui.viewmodel.MovieViewModel
import com.movie.app.jetpack.ui.viewmodel.model.MovieTypes
import com.movie.app.jetpack.ui.viewmodel.model.Status
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage


@Composable
fun MainLayout(
    movieViewModel: MovieViewModel,
    navigateMovieDetails: (MovieModel) -> Unit,
    openDrawer: () -> Unit
) {
    val context = LocalContext.current
    val swipeRefreshState = rememberSwipeRefreshState(false)
    val mutableSelectedTab = remember {
        mutableStateOf(0)
    }
    val mutableSelectedChip = remember {
        mutableStateOf(-1)
    }



    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            when (mutableSelectedTab.value) {
                MovieTypes.THEATER.type -> {
                    movieViewModel.getMovies(MovieTypes.THEATER.type)
                }
                MovieTypes.BOX_OFFICE.type -> {
                    movieViewModel.getMovies(MovieTypes.BOX_OFFICE.type)
                }
                MovieTypes.COMING_SOON.type -> {
                    movieViewModel.getMovies(MovieTypes.COMING_SOON.type)
                }
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val textState = remember { mutableStateOf(TextFieldValue("")) }
            MenuAndSearch(textState) {
                openDrawer()
            }

            TabRow(movieViewModel, mutableSelectedTab)

            TabGenres(mutableSelectedChip)


            val movieModels by movieViewModel.movieListVal.observeAsState()
            val listState = rememberLazyListState()
            val firstItemIndex = remember {
                mutableStateOf(0)
            }
            val lastItem = remember {
                mutableStateOf(0)
            }

            //https://stackoverflow.com/questions/66712286/get-last-visible-item-index-in-jetpack-compose-lazycolumn
            //https://developer.android.com/jetpack/compose/lists#react-to-scroll-position
            //https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/LazyListState#firstVisibleItemIndex()
            LaunchedEffect(listState) {
                /*snapshotFlow { listState.firstVisibleItemIndex }//listState.layoutInfo.visibleItemsInfo }
                    .collect {
                        firstItemIndex.value = it
                    }*/

                snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                    .collect {
                        //val lastItem = movieModels?.data?.size?.minus(1) ?: 0

                        if (it.isEmpty()) {
                            firstItemIndex.value = 0
                        } else if (it[it.size - 1].index == lastItem.value && it.size >= 2) {
                            //problem only two items lastItem.value != 1
                            if (it[0].offset >= 0) {
                                firstItemIndex.value = it[0].index
                            } else {
                                firstItemIndex.value = it[1].index
                            }
                            //firstItemIndex.value = it[1].index
                        } else if (it.size == 3) {
                            firstItemIndex.value = it[1].index
                        } else {
                            firstItemIndex.value = it[0].index
                        }
                    }
            }

            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth(),
                //.horizontalScroll(currentMovieScrollState),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (movieModels?.status == Status.SUCCESS) {
                    movieModels?.data?.let { it ->

                        //var currentIndex = 0 it always increases so this is not appropriate

                        val chipFilterVal = mutableSelectedChip.value
                        val chipSelectedName = getSelectedChipName(chipFilterVal)
                        val filteredList = getFilteredItems(it, textState, chipSelectedName)
                        lastItem.value = filteredList.size - 1
                        //https://stackoverflow.com/questions/70755946/android-jetpack-compose-lazy-column-items-with-index
                        itemsIndexed(items = filteredList) { index, item ->
                            MovieItem(
                                item,
                                index,//it.indexOf(item),//currentIndex++,
                                firstItemIndex.value,
                                LocalConfiguration.current.screenWidthDp,
                                LocalConfiguration.current.screenHeightDp
                            ) {
                                navigateMovieDetails(it)
                            }


                        }
                    }
                } else if (movieModels?.status == Status.ERROR) {

                    movieModels?.message?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }
    }
}

fun getFilteredItems(
    it: List<MovieModel>,
    textState: MutableState<TextFieldValue>,
    chipSelectedName: String?
): List<MovieModel> {
    return it.filter { movieModel ->
        (chipSelectedName != null && movieModel.genres.contains(
            chipSelectedName,
            true
        )) && (textState.value.text.isNotEmpty() && movieModel.title.contains(
            textState.value.text,
            true
        )) || (chipSelectedName != null && textState.value.text.isEmpty() && movieModel.genres.contains(
            chipSelectedName,
            true
        )) || (textState.value.text.isNotEmpty() && chipSelectedName == null && movieModel.title.contains(
            textState.value.text,
            true
        ) || chipSelectedName == null && textState.value.text.isEmpty())
    }
}

@Composable
fun TabGenres(mutableSelectedChip: MutableState<Int>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        ChipCustom(text = "Action", index = 0, state = mutableSelectedChip, false)
        ChipCustom(text = "Crime", index = 1, state = mutableSelectedChip, false)
        ChipCustom(text = "Comedy", index = 2, state = mutableSelectedChip, false)
        ChipCustom(text = "Drama", index = 3, state = mutableSelectedChip, false)
        ChipCustom(text = "Action1", index = 4, state = mutableSelectedChip, false)
        ChipCustom(text = "Action2", index = 5, state = mutableSelectedChip, false)
    }
}

@Composable
fun TabRow(movieViewModel: MovieViewModel, mutableSelectedTab: MutableState<Int>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TabMovie("In Theater", 0, mutableSelectedTab) {
            movieViewModel.getMovies(MovieTypes.THEATER.type)
        }
        TabMovie("Box Office", 1, mutableSelectedTab) {
            movieViewModel.getMovies(MovieTypes.BOX_OFFICE.type)
        }
        TabMovie("Coming Soon", 2, mutableSelectedTab) {
            movieViewModel.getMovies(MovieTypes.COMING_SOON.type)
        }
        /*TabMovie("Coming Soon", 2, mutableSelectedTab) {}
        TabMovie("Coming Soon", 2, mutableSelectedTab) {}
        TabMovie("Coming Soon", 2, mutableSelectedTab) {}
        TabMovie("Coming Soon", 2, mutableSelectedTab) {}*/
    }
}

fun getSelectedChipName(chipFilterVal: Int): String? {
    return when (chipFilterVal) {
        ChipTypes.ACTION.type -> {
            ChipTypes.ACTION.typeName
        }
        ChipTypes.DRAMA.type -> {
            ChipTypes.DRAMA.typeName
        }
        ChipTypes.COMEDY.type -> {
            ChipTypes.COMEDY.typeName
        }
        ChipTypes.CRIME.type -> {
            ChipTypes.CRIME.typeName
        }
        ChipTypes.ACTION1.type -> {
            ChipTypes.ACTION1.typeName
        }
        ChipTypes.ACTION2.type -> {
            ChipTypes.ACTION2.typeName
        }
        else -> {
            null
        }


    }

}

@Composable
fun MenuAndSearch(textState: MutableState<TextFieldValue>, openDrawer: () -> Unit) {
    //https://stackoverflow.com/questions/64362801/how-to-handle-visibility-of-a-text-in-jetpack-compose
    var visibilityOfMenu by remember { mutableStateOf(false) }
    var visibilityOfSearch by remember { mutableStateOf(false) }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Image(
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    //https://stackoverflow.com/questions/70008853/jetpack-compose-adding-clickable-modifier-on-image-component-changes-the-layout
                    visibilityOfMenu = !visibilityOfMenu
                    openDrawer()
                },
            painter = painterResource(id = R.drawable.menu),
            contentDescription = "Menu"
        )
        //https://stackoverflow.com/questions/65780722/jetpack-compose-how-to-remove-edittext-textfield-underline-and-keep-cursor
        //https://stackoverflow.com/questions/72599643/how-to-implement-search-in-jetpack-compose-android
        if (visibilityOfSearch)
            SearchViewCompose(textState)
        Image(
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    visibilityOfSearch = !visibilityOfSearch
                },
            painter = painterResource(id = R.drawable.search),
            contentDescription = "Search"
        )
    }
}

@Composable
fun SearchViewCompose(state: MutableState<TextFieldValue>) {
    TextField(
        modifier = Modifier.fillMaxWidth(0.7f),
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(onClick = {
                    state.value = TextFieldValue("")
                }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White.copy(alpha = 0.24f),
            //focusedIndicatorColor = Color.Transparent,
            //unfocusedIndicatorColor = Color.Transparent,
            //disabledIndicatorColor = Color.Transparent
        ),
        shape = RectangleShape
    )
}

@Composable
fun TabMovie(text: String, index: Int, state: MutableState<Int>, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = 12.dp, end = 12.dp)
            //.padding(top = 16.dp, end = 16.dp, bottom = 16.dp)
            .clickable {
                state.value = index
                onClick()
            }
    ) {
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        if (state.value == index) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painterResource(id = R.drawable.rectangle_tab),
                contentDescription = "TabIndicator"
            )
        }
    }
}

@Composable
fun ChipCustom(text: String, index: Int, state: MutableState<Int>, isDisabled: Boolean) {
    Surface(
        modifier = Modifier
            .padding(0.dp, 4.dp, 12.dp, 4.dp)
            .clickable {
                if (isDisabled) return@clickable
                if (state.value == index) state.value = -1
                else state.value = index
            },
        elevation = 1.dp,
        color = when (state.value) {
            index -> Purple200
            else -> Color.White
        },
        shape = RoundedCornerShape(16.dp),
        contentColor = when (state.value) {
            index -> Purple700
            else -> Color.Black
        },
        border = BorderStroke(
            width = 0.5.dp, color = when (state.value) {
                index -> Color.Blue
                else -> Color.Gray
            }
        )
    ) {
        Text(text, modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 8.dp))
    }
}

@Composable
fun MovieItem(
    movieModel: MovieModel,
    index: Int,
    state: Int,
    screenWidth: Int,
    screenHeight: Int,
    navigateMovieDetails: (MovieModel) -> Unit,
) {
//fun MovieItem(movieModel: MovieModel,index: Int, state: MutableState<Int>, navigateMovieDetails: () -> Unit) {

    val rotation = if (index < state) -10f else if (index > state) 10f else 0f
    //val rotation = if (index < state.value) -10f else if (index > state.value) 10f else 0f
    Column(
        modifier = Modifier
            .width((screenWidth * 7 / 10).dp)
            .padding(top = 12.dp)
            .alpha(if (index == state) 1f else 0.25f)
            .rotate(rotation)
            .clickable {
                navigateMovieDetails(movieModel)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlideImage(
            imageModel = movieModel.poster,//"https://cdn.myanimelist.net/images/anime/10/75815.jpg",
            modifier = Modifier
                .width((screenWidth * 5 / 10).dp)
                .height((screenHeight * 4 / 10).dp),
            //.clip(RoundedCornerShape(corner = CornerSize(64f))),
            imageOptions = ImageOptions(
                contentDescription = "Movie Image",
                contentScale = ContentScale.FillBounds
            ),
            requestOptions = {
                //val widthPx = LocalDensity.current.run { 200.dp.roundToPx() }
                //val heightPx = LocalDensity.current.run { 250.dp.roundToPx() }
                RequestOptions().apply(
                    RequestOptions.bitmapTransform(RoundedCorners(64))
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(movieModel.title, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(painterResource(id = R.drawable.star), contentDescription = "Star")
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "8.2", fontSize = 20.sp)
        }
    }
}

