package com.example.aplikasigithubuser.ui

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasigithubuser.R
import com.example.aplikasigithubuser.data.Result
import com.example.aplikasigithubuser.data.remote.response.ItemsItem
import com.example.aplikasigithubuser.databinding.ActivityMainBinding
import com.example.aplikasigithubuser.ui.adaptor.GitHubUserListAdapter
import com.example.aplikasigithubuser.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: MainViewModel by viewModels()

        binding.rvListUserGithub.setHasFixedSize(true)

        viewModel.isLoading.observe(this){
            showLoading(it)
        }

        viewModel.isError.observe(this){
            showError(it)
        }

        if (viewModel.isFilledGitHubUserList()){
            viewModel.getSavedGitHubUserList()?.let { showRvListGitHubUser(it) }
        }else{
            setGitHubUserList(viewModel, "arip")
        }

        binding.svUserGithub.setupWithSearchBar(binding.sbUserGithub)
        binding.svUserGithub.editText
            .setOnEditorActionListener{ searchText, _, _ ->
                binding.sbUserGithub.text = binding.svUserGithub.text
                binding.svUserGithub.hide()

                val userSearch = searchText.text.toString().trim()

                setGitHubUserList(viewModel, userSearch)
                false
            }
    }

    private fun setGitHubUserList(viewModel: MainViewModel, userSearch: String){
        viewModel.getGitHubUserList(userSearch)
            .observe(this){ result ->
                if (result != null){
                    when (result) {
                        is Result.Loading -> {
                            viewModel.isLoading(true)
                        }
                        is Result.Success -> {
                            if (result.data.isEmpty()){
                                val errorMsg = "Data user tidak ditemukan"
                                viewModel.isError(errorMsg)
                            }else{
                                viewModel.isError("")
                            }
                            viewModel.isLoading(false)

                            val newsData = ArrayList<ItemsItem>()
                            newsData.addAll(result.data)
                            viewModel.saveGitHubUserList(newsData)

                            showRvListGitHubUser(newsData)
                        }
                        is Result.Error -> {
                            viewModel.isLoading(false)
                            val msgError = "Terjadi kesalahan" + result.error
                            viewModel.isError(msgError)
                        }
                    }
                }else{
                    val errorMsg = "Gagal memuat API"
                    viewModel.isError(errorMsg)
                }
            }
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(isError: String){
        binding.tvErrorMessage.visibility = if (isError.isNotEmpty()) View.VISIBLE else View.GONE

        binding.tvErrorMessage.text = isError
    }

    private fun showRvListGitHubUser(listGitHubUser: ArrayList<ItemsItem>) {
        binding.rvListUserGithub.layoutManager = LinearLayoutManager(this)
        val listAdapter = GitHubUserListAdapter(listGitHubUser)
        binding.rvListUserGithub.adapter = listAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
