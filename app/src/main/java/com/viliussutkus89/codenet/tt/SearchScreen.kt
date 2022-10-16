package com.viliussutkus89.codenet.tt

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.viliussutkus89.codenet.tt.ui.theme.TestTaskTheme


@Composable
private fun Person(person: Person) {
    Card(
        modifier = Modifier
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            SubcomposeAsyncImage(
                model = person.thumbnail,
                contentDescription = null,
                loading = {
                    CircularProgressIndicator(modifier = Modifier.size(64.dp))
                },
                modifier = Modifier.size(64.dp).padding(end = 8.dp)
            )
            Column {
                Text(
                    text = person.name,
                    fontWeight = FontWeight.Bold,
                )
                Text(person.email)
                Text(person.address)
            }
        }
    }
}

@Preview
@Composable
fun PreviewPersons() {
    TestTaskTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column {
                Person(
                    Person(
                        name = "Mr. Vilius Sutkus",
                        address = "Kaunas, Lietuva",
                        email = "vilius@ViliusSutkus89.com",
                        thumbnail = "https://www.viliussutkus89.com/images/avatar.jpg"
                    )
                )
                Person(
                    Person(
                        name = "Mr. Vilius Sutkus",
                        address = "Kaunas, Lietuva",
                        email = "vilius@ViliusSutkus89.com",
                        thumbnail = "https://www.viliussutkus89.com/images/avatar.jpg"
                    )
                )
                Person(
                    Person(
                        name = "Mr. Vilius Sutkus",
                        address = "Kaunas, Lietuva",
                        email = "vilius@ViliusSutkus89.com",
                        thumbnail = "https://www.viliussutkus89.com/images/avatar.jpg"
                    )
                )
            }
        }
    }
}

@Composable
private fun SearchScreenStateless(
    search: String,
    searchOnUpdate: (String) -> Unit,
    persons: List<Person>,
    apiStatus: ApiStatus
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.search_title),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = search,
            onValueChange = searchOnUpdate,
            label = { Text(stringResource(R.string.search_query_label))},
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = MaterialTheme.colors.secondary,

                focusedBorderColor = MaterialTheme.colors.secondary,
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = persons) { item ->
                Person(item)
            }
        }

        when (apiStatus) {
            ApiStatus.LOADING -> CircularProgressIndicator()
            ApiStatus.ERROR -> Image(
                painter = painterResource(id = R.drawable.ic_connection_error),
                contentDescription = stringResource(R.string.failed_to_fetch_from_internet)
            )
            ApiStatus.DONE -> {
                if (persons.isEmpty()) {
                    Text(stringResource(R.string.no_persons_found))
                }
            }
        }
    }
}

@Composable
internal fun SearchScreen(viewModel: TestTaskViewModel) {
    val search by viewModel.search.observeAsState("")
    val persons by viewModel.persons.observeAsState(listOf())
    val apiStatus by viewModel.apiStatus.observeAsState(ApiStatus.LOADING)

    SearchScreenStateless(
        search = search,
        searchOnUpdate = { viewModel.updateSearch(it) },
        persons = persons,
        apiStatus = apiStatus
    )
}

@Preview
@Composable
private fun PreviewSearchScreen() {
    TestTaskTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            SearchScreen(viewModel = TestTaskViewModel())
        }
    }
}
