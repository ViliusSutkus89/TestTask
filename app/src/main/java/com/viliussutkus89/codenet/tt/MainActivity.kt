package com.viliussutkus89.codenet.tt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.*
import com.viliussutkus89.codenet.tt.ui.theme.TestTaskTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val viewModel: TestTaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTaskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TestTaskComposeApp(viewModel)
                }
            }
        }
    }
}

internal enum class ApiStatus { LOADING, ERROR, DONE }

internal class TestTaskViewModel: ViewModel() {
    private val _isLoggedIn = MutableLiveData(false)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    fun login(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            _isLoggedIn.postValue(true)

            loadPersons()
        }
    }

    private val _search = MutableLiveData("")
    val search: LiveData<String> = _search
    fun updateSearch(search: String) {
        _search.postValue(search)
    }

    private val _apiStatus = MutableLiveData(ApiStatus.LOADING)
    val apiStatus: LiveData<ApiStatus> = _apiStatus

    private val _persons = MutableLiveData<List<Person>>()

    val persons: LiveData<List<Person>> = MergerLiveData.Two(search, _persons) { search, persons ->
        persons.filter { person ->
            person.name.contains(search, ignoreCase = true)
        }
    }


    private fun loadPersons() {
        viewModelScope.launch(Dispatchers.IO) {
            _apiStatus.postValue(ApiStatus.LOADING)
            try {
                val retrofit = PersonsNetwork.retrofitService
                val persons = retrofit.getNetworkResults().asPersons() as MutableList
                _persons.postValue(persons)

                for (i in 2..3) {
                    persons.addAll(retrofit.getNetworkResults().asPersons())
                    _persons.postValue(persons)
                }

                _apiStatus.postValue(ApiStatus.DONE)
            } catch (e: Exception) {
                _persons.postValue(listOf())
                _apiStatus.postValue(ApiStatus.ERROR)
            }
        }
    }
}

@Composable
internal fun TestTaskComposeApp(viewModel: TestTaskViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.observeAsState(false)
    if (isLoggedIn) {
        SearchScreen(viewModel)
    } else {
        LoginScreen { email, password -> viewModel.login(email, password) }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTestTaskComposeApp() {
    TestTaskTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            TestTaskComposeApp(viewModel = TestTaskViewModel())
        }
    }
}
