package com.example.peer_pulse.presentation.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.peer_pulse.presentation.AuthViewModel
import com.example.peer_pulse.presentation.postUI.PostViewModel
import com.example.peer_pulse.R
import com.example.peer_pulse.data.room.post
import com.example.peer_pulse.domain.model.preferences

import com.example.peer_pulse.presentation.postUI.PostUI
import com.example.peer_pulse.presentation.preferences.PreferencesViewModel
import com.example.peer_pulse.utilities.Screens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    postViewModel: PostViewModel,


    ){
    val userFeedState = postViewModel.userFeedState
    val lazyPagingItems = userFeedState.collectAsLazyPagingItems()

    val LastWeek=postViewModel.mostLikedLastWeek
    val lazyPagingItems10=LastWeek.collectAsLazyPagingItems()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { hometabs.size})
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
    Scaffold(
        topBar = { TopAppBarWithSearch(navController)},
        bottomBar = {
            BottomNavigation(
                selectedButton = BottomNavigationScreens.Main,
                navController = navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screens.AddPostScreen.route)
                }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription =null )
            }
        }
    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier.fillMaxWidth(),
                //containerColor = Color(0xFFffffffff),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        color = Color.Gray,
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.value])
                    )
                }

            ) {
                hometabs.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        selectedContentColor = Color(0xFFffffff),
                        unselectedContentColor = MaterialTheme.colorScheme.outline,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(currentTab.index)
                            }
                        },
                        text = { Text(text = hometabs[index].txt1) },

                        )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column (modifier = Modifier.fillMaxSize()){
                        when (hometabs[selectedTabIndex.value].txt1) {
                            "HOME" -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    for (post in lazyPagingItems.itemSnapshotList.items) {
                                        post?.let {
                                            PostUI(post = it, navController,postViewModel)
                                            HorizontalDivider(
                                                Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                            "POPULAR" -> {
                                val selectedFilter = remember { mutableStateOf("Past Week") }
                                val mostLikedPosts = when (selectedFilter.value) {
                                    "Past Week" -> postViewModel.mostLikedLastWeek
                                    "Past Month" -> postViewModel.mostLikedLastMonth
                                    "Past Year" -> postViewModel.mostLikedLastYear
                                    else -> postViewModel.mostLikedLastWeek
                                }
                                Column (Modifier.fillMaxSize()){
                                    Filter(selectedFilter.value) { filter ->
                                        selectedFilter.value = filter
                                        when (filter) {
                                            "Past Week" -> postViewModel.fetchMostLikedLastWeek()
                                            "Past Month" -> postViewModel.fetchMostLikedLastMonth()
                                            "Past Year" -> postViewModel.fetchMostLikedLastYear()
                                        }
                                    }
                                    val lazyPagingItems2 = mostLikedPosts.collectAsLazyPagingItems()
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        for (post1 in lazyPagingItems2.itemSnapshotList.items) {
                                            post1?.let {
                                                PostUI(post = it, navController,postViewModel)
                                                HorizontalDivider(
                                                    Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }
                                }

                            }

                        }

                    }


                }
            }
        }}}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithSearch(navController: NavController) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 25.dp, end = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.applogoblue),
            contentDescription = "App Logo",
            Modifier
                .size(38.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        CustomTextFieldWithSuggestions(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Search for Topic",
            preferences = preferences,
            navController = navController
        )
    }
}

data class HomeTabs(
    val index: Int,
    var txt1: String
)

val hometabs= listOf(
    HomeTabs(
        0,
        "HOME"
    ),
    HomeTabs(
        1,
        "POPULAR"
    )

)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Filter(selectedFilter: String, onFilterSelected: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xff0a0a0a))
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                scope.launch { showBottomSheet = true }
            }
    ) {
        Text(
            text = selectedFilter,
            color = Color.Gray,
            modifier = Modifier
                .padding(start = 15.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
            imageVector = Icons.Outlined.ArrowDropDown,
            contentDescription = "Dropdown Icon"
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { scope.launch { showBottomSheet = false } },
            sheetState = bottomSheetState
        ) {
            BottomSheetContent(onOptionSelected = { filter ->
                onFilterSelected(filter)
                scope.launch { showBottomSheet = false }
            })
        }
    }
}

@Composable
fun BottomSheetContent(onOptionSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Past Week",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    onOptionSelected("Past Week")
                })
                .padding(8.dp)
        )
        Text(
            text = "Past Month",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onOptionSelected("Past Month") })
                .padding(8.dp)
        )
        Text(
            text = "Past Year",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onOptionSelected("Past Year") })
                .padding(8.dp)
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextFieldWithSuggestions(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    preferences: List<String>,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    placeholderColor: Color = Color.Gray,
    backgroundColor: Color = Color(0xff262626),
    shape: Shape = RoundedCornerShape(20.dp),
    navController: NavController
) {
    var filteredPreferences by remember { mutableStateOf(listOf<String>()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }



    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(color = backgroundColor, shape = shape)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                if (value.text.isEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            tint = placeholderColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = placeholder,
                            color = placeholderColor,
                            fontSize = 14.sp,
                        )
                    }
                }
                BasicTextField(
                    value = value,
                    onValueChange = {
                        onValueChange(it)
                        filteredPreferences = preferences
                            .filter { pref ->
                                pref.contains(it.text, ignoreCase = true)
                            }
                            .sorted()
                        isDropdownExpanded = filteredPreferences.isNotEmpty()
                        focusRequester.requestFocus() // Ensure the TextField keeps focus
                        keyboardController?.show()

                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                )
            }

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredPreferences.forEach { suggestion ->
                    DropdownMenuItem(
                        onClick = {
                            onValueChange(TextFieldValue(suggestion))
                            isDropdownExpanded = false
                            keyboardController?.hide()
                            navController.navigate(Screens.PagesScreen.createRoute(suggestion))
                        },
                        text = {
                            Text(text = suggestion, fontSize = 14.sp)
                        }
                    )
                }
            }
        }
    }
}



