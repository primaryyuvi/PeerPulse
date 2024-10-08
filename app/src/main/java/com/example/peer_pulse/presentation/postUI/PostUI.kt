package com.example.peer_pulse.presentation.postUI

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.peer_pulse.R
import com.example.peer_pulse.data.room.post
import com.example.peer_pulse.domain.model.Post
import com.example.peer_pulse.domain.model.colleges
import com.example.peer_pulse.domain.model.getPreferenceById
import com.example.peer_pulse.utilities.ResponseState
import com.example.peer_pulse.utilities.Screens
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalGlideComposeApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostUI(
    post: post,
    navController: NavController,
    postViewModel: PostViewModel
){
    val likePostState by postViewModel.likePostState.collectAsState()
    val likedPosts by remember { mutableStateOf(mutableMapOf<String, Boolean>()) }
    var likeCount by remember(post.id) { mutableStateOf(post.likes) }


    LaunchedEffect(likePostState) {
        when (likePostState) {
            is ResponseState.Success -> {
                if ((likePostState as ResponseState.Success<Boolean>).data) {
                    likedPosts[post.id] = true
                }
            }
            is ResponseState.Error -> {
                // Optionally rollback the like UI
                /*isLiked = false
                likeCount -= 1*/
                // Toast.makeText(context, "Failed to like the post.", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Card(onClick = { navController.navigate(Screens.PostViewScreen.createRoute(post)) {
        launchSingleTop = true
    } },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(0.dp)

    ) {
        val pref = getPreferenceById(post.preferences)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
        ){
            Column (
                Modifier
                    .fillMaxSize()
                    .padding(15.dp)){
                Row (Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically){
                    if (pref != null) {
                        Image(
                            painter = painterResource(id = pref.logo),
                            contentDescription = "topicimage",
                            Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                        )
                    }
                    else{
                        post.collegeCode?.let { getCollegelogo(it) }
                            ?.let { painterResource(it) }?.let {
                                Image(painter = it, contentDescription ="topicimage" ,
                                    Modifier
                                        .size(35.dp)
                                        .clip(CircleShape))
                            }
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Column(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if(post.preferences==""){
                                Text(text = "${post.collegeCode?.let { findCollegeshortnameBYname(it) }}" ,fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    /*modifier = Modifier.clickable { navController.navigate(Screens.PagesScreen.createRoute(post.preferences)){
                                        launchSingleTop = true
                                    } }*/
                                )
                            }
                            else{
                                Text(text = "${post.preferences}" ,fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { navController.navigate(Screens.PagesScreen.createRoute(post.preferences)){
                                        launchSingleTop = true
                                    } }
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = getTimeAgo(post.timestamp),fontSize = 10.sp, color=Color.Gray)
                        }

                        Text(text = "Dr.Ait",
                            fontSize = 10.sp,
                            color=Color.Gray
                            //fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (post.images.isNotEmpty()) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "${post.title}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${post.description}",
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(7.dp))
                    GlideImage(model = post.images[0], contentDescription ="" ,
                        modifier = Modifier
                            .height(70.dp).width(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .align(Alignment.Top))
                        /*AsyncImage(
                            model = post.images[0],
                            contentDescription = "Post Image",
                            modifier = Modifier
                                .height(70.dp).width(70.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .align(Alignment.Top),
                           // placeholder = painterResource(R.drawable.ic_launcher_background),
                           // error = painterResource(R.drawable.ic_launcher_background)
                        )*/}}

                   else{
                    Row(Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            text = "${post.title}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${post.description}",
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Gray
                        )
                    }}
                    }

                Spacer(modifier = Modifier.height(13.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (post.likes > 0) "${post.likes}" else "Likes",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            painter = if (likedPosts.getOrDefault(post.id, false)) {
                                painterResource(id = R.drawable.baseline_thumb_up_24)
                            } else {
                                painterResource(id = R.drawable.likes_vector)
                            },
                            contentDescription = "likes",
                            Modifier
                                .size(20.dp)
                                .clickable {
                                    if (!likedPosts.getOrDefault(post.id, false)) {
                                        likedPosts[post.id] = true
                                        likeCount += 1
                                        postViewModel.userId?.let { postViewModel.likePost(post.id, it) }
                                    } else {
                                        likedPosts[post.id] = false
                                        likeCount-=1
                                    }
                                },
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Replies",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            painter = painterResource(id = R.drawable.comment_vector),
                            contentDescription = "replies",
                            Modifier.size(13.dp),
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bookmark",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            painter = painterResource(id = R.drawable.bookmarks_vector),
                            contentDescription = "bookmark",
                            Modifier
                                .size(20.dp)
                                .clickable { },
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray)
                        )
                    }
                }
            }

        }

    }}


fun findCollegeshortnameBYname(collegeCode: String): String {
    val college = colleges.find { it.name == collegeCode }
    return college?.shortName ?: "College Not Found"
}

fun getCollegelogo(collegeCode: String):Int{
    val college = colleges.find { it.name == collegeCode }
    return college?.logo?:0

}

@RequiresApi(Build.VERSION_CODES.O)
fun getTimeAgo(timestamp: Long): String {
    val postDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val now = LocalDateTime.now()
    val duration = ChronoUnit.SECONDS.between(postDateTime, now)

    val minutes = duration / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "$days days ago"
        hours > 0 -> "$hours hours ago"
        minutes > 0 -> "$minutes minutes ago"
        else -> "Just now"
    }
}
@Preview
@Composable
fun prev(){
    LazyColumn {
        items(10) { // Example with 10 items
          //  PostUI()
            HorizontalDivider(color = Color.Gray)
        }
    }


}