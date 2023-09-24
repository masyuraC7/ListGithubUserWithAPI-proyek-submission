package com.example.aplikasigithubuser.domain.repository

import androidx.lifecycle.LiveData
import com.example.aplikasigithubuser.data.Result
import com.example.aplikasigithubuser.data.remote.response.ItemsItem

interface FollowRepository {
    fun getFollowers(username: String): LiveData<Result<List<ItemsItem>>>
    fun getFollowing(username: String): LiveData<Result<List<ItemsItem>>>
}