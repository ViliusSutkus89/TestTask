package com.viliussutkus89.codenet.tt

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage


@Composable
private fun Person(person: Person) {
    Card {
        Row {
            SubcomposeAsyncImage(
                model = person.thumbnail,
                contentDescription = null,
                loading = {
                    CircularProgressIndicator()
                },
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(person.name)
                Text(person.email)
                Text(person.address)
            }
        }
    }
}

@Preview
@Composable
fun PreviewPerson() {
    Person(Person(
        name = "Mr. Vilius Sutkus",
        address = "Kaunas, Lietuva",
        email = "vilius@ViliusSutkus89.com",
        thumbnail = "https://www.viliussutkus89.com/images/avatar.jpg"
    ))
}

@Composable
private fun SearchScreenStateless(
    search: String,
    searchOnUpdate: (String) -> Unit,
    persons: List<Person>,
    apiStatus: ApiStatus
) {
    Column {
        Text(stringResource(R.string.search_title))

        TextField(
            value = search,
            onValueChange = searchOnUpdate,
            label = { Text(stringResource(R.string.search_query_label))}
        )

        LazyColumn {
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
    SearchScreen(viewModel = TestTaskViewModel())
}
