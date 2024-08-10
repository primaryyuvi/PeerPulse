package com.example.peer_pulse.presentation.postUI

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.peer_pulse.data.room.post
import com.example.peer_pulse.domain.model.Post
import com.example.peer_pulse.domain.model.Preferences
import com.example.peer_pulse.domain.repository.PostsRepository
import com.example.peer_pulse.utilities.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val auth : FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    init {
        auth.addAuthStateListener {
            userId = it.currentUser?.uid
        }
    }

    private val _userPreferences = MutableStateFlow<List<String>>(emptyList())
    val userPreferences: StateFlow<List<String>> get() = _userPreferences

    private val _postState = mutableStateOf<ResponseState<Post?>>(ResponseState.Success(null))
    val postState : State<ResponseState<Post?>> = _postState

    private val _savePostState = mutableStateOf<ResponseState<Boolean?>>(ResponseState.Success(null))
    val savePostState: State<ResponseState<Boolean?>> = _savePostState

    private val _deletePostState = mutableStateOf<ResponseState<String>>(ResponseState.Success(""))
    val deletePostState: State<ResponseState<String>> = _deletePostState

    var userId = auth.currentUser?.uid

    private val _userFeedState = MutableStateFlow<PagingData<post>>(PagingData.empty())
    val userFeedState: StateFlow<PagingData<post>> get() = _userFeedState

    private val _mostLikedLastWeek = MutableStateFlow<PagingData<post>>(PagingData.empty())
    val mostLikedLastWeek: StateFlow<PagingData<post>> get() = _mostLikedLastWeek

    private val _mostLikedLastMonth = MutableStateFlow<PagingData<post>>(PagingData.empty())
    val mostLikedLastMonth: StateFlow<PagingData<post>> get() = _mostLikedLastMonth

    private val _mostLikedLastYear = MutableStateFlow<PagingData<post>>(PagingData.empty())
    val mostLikedLastYear: StateFlow<PagingData<post>> get() = _mostLikedLastYear

    private val _likePostState = MutableStateFlow<ResponseState<Boolean>>(ResponseState.Success(false))
    val likePostState: StateFlow<ResponseState<Boolean>> = _likePostState.asStateFlow()

    init {
        fetchPosts()
        fetchMostLikedLastWeek()
    }
    private fun fetchUserPreferences() {
        viewModelScope.launch {
            try {
                val userDocument = userId?.let { firestore.collection("users").document(it).get().await() }
                val preferences = userDocument?.get("preferences") as? List<String> ?: emptyList()
                _userPreferences.value = preferences
                Log.d("pref","$_userPreferences")
            } catch (e: Exception) {
                _userPreferences.value = emptyList()
                Log.d("pref","empty")
            }
        }
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            val userDocument = userId?.let { firestore.collection("users").document(it).get().await() }
            val preferences1 = userDocument?.get("preferences") as? List<String> ?: emptyList()
            postsRepository.getPosts(preferences1)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _userFeedState.value = pagingData
                }
        }
    }

    fun refreshFeed() {
        fetchPosts()
    }

    fun fetchMostLikedLastWeek() {
        viewModelScope.launch {
            val userDocument = userId?.let { firestore.collection("users").document(it).get().await() }
            val preferences1 = userDocument?.get("preferences") as? List<String> ?: emptyList()
            postsRepository.getMostLikedPostsLastWeek(preferences1)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _mostLikedLastWeek.value = pagingData
                }
        }
    }


    fun fetchMostLikedLastMonth() {
        viewModelScope.launch {
            val userDocument = userId?.let { firestore.collection("users").document(it).get().await() }
            val preferences1 = userDocument?.get("preferences") as? List<String> ?: emptyList()
            postsRepository.getMostLikedPostsLastMonth(preferences1)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _mostLikedLastMonth.value = pagingData
                }
        }
    }

    fun fetchMostLikedLastYear() {
        viewModelScope.launch {
            val userDocument = userId?.let { firestore.collection("users").document(it).get().await() }
            val preferences1 = userDocument?.get("preferences") as? List<String> ?: emptyList()
            postsRepository.getMostLikedPostsLastYear(preferences1)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _mostLikedLastYear.value = pagingData
                }
        }
    }

    fun getPost(postId: String){
        viewModelScope.launch {
            postsRepository.getPost(postId).collect { state ->
                _postState.value = state
            }
        }
    }
    fun savePost(
        title : String,
        description : String,
        images : List<Uri?>,
        preferences : String,
        preferencesId : String
    ){
        val image : List<String> =  images.map { it.toString() }
        viewModelScope.launch {
            postsRepository.savePost(
                title,
                description,
                image,
                preferences,
                preferencesId,
                userId!!
            ).collect{ state ->
                _savePostState.value = state
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            postsRepository.deletePost(postId).collect { state ->
                _deletePostState.value = state
            }
        }
    }

    fun likePost(postId: String, userId: String) {
        viewModelScope.launch {
           postsRepository.likePost(postId, userId)
                .onEach { responseState ->
                    _likePostState.value = responseState
                }
                .launchIn(this)
        }
    }

  /*  private val _selectedFilter = MutableStateFlow("Past Week")
    val selectedFilter: StateFlow<String> = _selectedFilter

    private val _mostLikedPosts = MutableStateFlow<PagingData<post>>(PagingData.empty())
    val mostLikedPosts: StateFlow<PagingData<post>> = _mostLikedPosts

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        fetchMostLikedPosts()
    }

    private fun fetchMostLikedPosts() {
        viewModelScope.launch {
            val preferences = _userPreferences.value
            when (_selectedFilter.value) {
                "Past Week" -> postsRepository.getMostLikedPostsLastWeek(preferences)
                "Past Month" -> postsRepository.getMostLikedPostsLastMonth(preferences)
                "Past Year" -> postsRepository.getMostLikedPostsLastYear(preferences)
                else -> throw IllegalArgumentException("Invalid filter")
            }.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _mostLikedPosts.value = pagingData
                }
        }
    }*/

}