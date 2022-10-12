package com.movie.app.jetpack.ui.screens.details

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.request.RequestOptions
import com.movie.app.jetpack.R
import com.movie.app.jetpack.common.Constants
import com.movie.app.jetpack.data.source.local.SharedPrefs
import com.movie.app.jetpack.model.ActorModel
import com.movie.app.jetpack.model.MovieModel
import com.movie.app.jetpack.ui.screens.home.ChipCustom
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun MovieDetailsScreen(movieModel: MovieModel) {
    val isWillWatch = remember {
        mutableStateOf(false)
    }

    isWillWatch.value = SharedPrefs().getParamBoolean(
        LocalContext.current,
        movieModel.title
    )


    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        //https://foso.github.io/Jetpack-Compose-Playground/layout/box/
        //all image height + cardviewstars height half
        TopHeader(movieModel.poster)


        RowWatchList(movieModel, isWillWatch)


        RowChipGenre(movieModel)


        TextBold("Plot Summary", 32, true)
        Text(movieModel.description, modifier = Modifier.padding(16.dp, 4.dp, 16.dp, 0.dp))

        TextBold("Cast & Crew", 32, true)
        ActorsRow(movieModel.actors)
    }
}

@Composable
fun RowChipGenre(movieModel: MovieModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp, 0.dp, 0.dp)
            .horizontalScroll(rememberScrollState()),

        ) {

        var index = 0
        val state = remember {
            mutableStateOf(-1)
        }
        for (item in movieModel.genres.split("|"))
            ChipCustom(text = item, index = index++, state = state, true)
        /*ChipCustom(text = "Crime", index = 1, state = mutableSelectedChip)
        ChipCustom(text = "Comedy", index = 2, state = mutableSelectedChip)
        ChipCustom(text = "Drama", index = 3, state = mutableSelectedChip)
        ChipCustom(text = "Action1", index = 4, state = mutableSelectedChip)
        ChipCustom(text = "Action2", index = 5, state = mutableSelectedChip)*/
    }
}

@Composable
fun RowWatchList(movieModel: MovieModel, isWillWatch: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp, 16.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .height(64.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextBold(movieModel.title, 32, false)
            Text("${movieModel.year}    ${movieModel.runtime}")
        }

        val context = LocalContext.current
        IconButton(
            onClick = {
                isWillWatch.value = !isWillWatch.value
                val sharedPrefs = SharedPrefs()
                sharedPrefs.setParamBoolean(
                    context,
                    movieModel.title,
                    isWillWatch.value
                )
                Toast.makeText(
                    context,
                    if (isWillWatch.value) "Added to watch" else "Removed from watch",
                    Toast.LENGTH_SHORT
                ).show()
                if (isWillWatch.value) {
                    addToWatchList(context, sharedPrefs, movieModel.title)
                } else {
                    removeFromWatchList(context, sharedPrefs, movieModel.title)
                }

            },
            modifier = Modifier
                .size(64.dp)
                .background(
                    Color(0xffFE6D8E),
                    RoundedCornerShape(16.dp)
                )
        ) {

            Icon(
                painterResource(id = if (isWillWatch.value) R.drawable.tick else R.drawable.add),
                contentDescription = "Add to watch list or remove from watch list"
            )
        }
    }
}

@Composable
fun TopHeader(poster: String) {
    Box(modifier = Modifier.height(((LocalConfiguration.current.screenHeightDp * 0.35) + 56).dp)) { //306.dp
        GlideImage(
            imageModel = poster,//"https://cdn.myanimelist.net/images/anime/10/75815.jpg",
            modifier = Modifier
                .fillMaxWidth()
                .height((LocalConfiguration.current.screenHeightDp * 0.35).dp)
                .clip(RoundedCornerShape(0.dp, 0.dp, 32.dp, 32.dp)),
            imageOptions = ImageOptions(
                contentDescription = "Movie Image"
            ),


            //requestOptions = { RequestOptions.bitmapTransform(RoundedCorners(64)) }
        )
        CardViewStar(
            modifier = Modifier
                .height(112.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp, 0.dp, 0.dp, 0.dp),
            tenScore = "10",
            hundredScore = "213,123",
            metaScore = 20,
            metaScoreReviews = "433,234 critic reviews"
        )
    }
}

@Composable
fun TextBold(text: String, fontSizeValue: Int, paddingEnabled: Boolean) {
    Text(
        text,
        modifier = if (paddingEnabled) Modifier.padding(16.dp, 4.dp, 16.dp, 0.dp) else Modifier,
        fontWeight = FontWeight.Bold,
        fontSize = fontSizeValue.sp
    )
}


@Composable
fun ActorsRow(actors: List<ActorModel>) {
    Row(
        modifier = Modifier
            .padding(16.dp, 4.dp, 8.dp, 0.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        for (item in actors) {
            ActorColumn(item)
        }
    }
}

@Composable
fun ActorColumn(actorModel: ActorModel) {
    Column(
        modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            imageModel = actorModel.userPhotoURL,//"https://cdn.myanimelist.net/images/anime/10/75815.jpg",
            modifier = Modifier
                .size(64.dp),
            //.clip(RoundedCornerShape(corner = CornerSize(64))),
            imageOptions = ImageOptions(
                contentDescription = "Actor Image"
            ),
            requestOptions = { RequestOptions.circleCropTransform() }
            //requestOptions = { RequestOptions.bitmapTransform(RoundedCorners(64)) }
        )
        TextBold(actorModel.userName, 16, false)
        Text(actorModel.userRole)
    }
}

private fun removeFromWatchList(context: Context, sharedPrefs: SharedPrefs, title: String) {
    var index = sharedPrefs.getParamMenuIndex(context)
    val currentIndex = sharedPrefs.getParamMenuName(
        context,
        title,
        index
    )
    /*sharedPrefs.removeParam(
        requireContext(),
        "${Constants.MENU_ITEM_SUFFIX}${index}"
    )*/
    sharedPrefs.reorderParamMenu(
        context,
        currentIndex,
        index
    )
    sharedPrefs.setParamMenuIndex(
        context,
        --index
    )

}

private fun addToWatchList(context: Context, sharedPrefs: SharedPrefs, title: String) {
    var index = sharedPrefs.getParamMenuIndex(context)
    if (index < 0) index = 0
    sharedPrefs.setParamString(
        context,
        "${Constants.MENU_ITEM_SUFFIX}${index}",
        title
    )
    sharedPrefs.setParamMenuIndex(
        context,
        ++index
    )

}

@Composable
fun CardViewStar(
    modifier: Modifier,
    tenScore: String,
    hundredScore: String,
    metaScore: Int,
    metaScoreReviews: String
) {
    Card(modifier, shape = RoundedCornerShape(64.dp, 0.dp, 0.dp, 64.dp)) {
        Row(

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painterResource(id = R.drawable.star),
                    contentDescription = "Star",
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                )
                TextBold(tenScore, 16, false)
                Text(hundredScore)
            }
            Column(
                modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painterResource(id = R.drawable.star1),
                    contentDescription = "Star",
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                )
                TextBold("Rate this", 16, false)
                Text("")

            }
            Column(
                modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    metaScore.toString(),
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .background(Color.Green)
                )
                TextBold("Metascore", 16, false)
                Text(metaScoreReviews, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}