package com.viliussutkus89.codenet.tt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viliussutkus89.codenet.tt.ui.theme.TestTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTaskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TestTaskComposeApp()
                }
            }
        }
    }
}

internal class TestTaskViewModel: ViewModel() {
    private val _isLoggedIn = MutableLiveData(false)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    fun login(email: String, password: String) {
        _isLoggedIn.postValue(true)
    }
}

@Composable
internal fun TestTaskComposeApp(viewModel: TestTaskViewModel = TestTaskViewModel()) {
    val isLoggedIn by viewModel.isLoggedIn.observeAsState(false)
    if (isLoggedIn) {
        Text("Placeholder for Search screen")
    } else {
        LoginScreen { email, password -> viewModel.login(email, password) }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTestTaskComposeApp() {
    TestTaskComposeApp()
}
