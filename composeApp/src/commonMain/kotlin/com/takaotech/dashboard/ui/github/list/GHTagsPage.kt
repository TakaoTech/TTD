package com.takaotech.dashboard.ui.github.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.compose.collectAsLazyPagingItems
import com.takaotech.dashboard.ui.platform.components.TagChip
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object GHTagsList

@Composable
fun GHTagsPage(viewModel: GHHomepageTagsPageViewModel = koinViewModel()) {
    val tagList = viewModel.tagList.collectAsLazyPagingItems()
    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(tagList.itemCount) {
                val item = tagList[it]
                item?.let {
                    TagChip(
                        text = it.name,
                        color = it.color,
                    ) {
                    }
                }
            }

            tagList.loadState.apply {
                when {
                    refresh is LoadStateNotLoading && tagList.itemCount < 1 -> {
                        item(key = "LoadStateNotLoading") {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "No Items",
                                    modifier = Modifier.align(Alignment.Center),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    refresh is LoadStateLoading -> {
                        item(key = "LoadStateLoading-refresh") {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    Modifier.align(Alignment.Center),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }

                    append is LoadStateLoading -> {
                        item(key = "LoadStateLoading-append") {
                            Row {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .padding(16.dp)
                                            .wrapContentWidth(Alignment.CenterHorizontally),
                                )
                            }
                        }
                    }

                    refresh is LoadStateError -> {
                        item(key = "LoadStateError-refresh") {
                            // TODO
// 							ErrorView(
// 								message = "No Internet Connection",
// 								onClickRetry = { data.retry() },
// 								modifier = Modifier.fillParentMaxSize()
// 							)
                        }
                    }

                    append is LoadStateError -> {
                        item(key = "LoadStateError-append") {
                            // TODO
// 							ErrorItem(
// 								message = "No Internet Connection",
// 								onClickRetry = { data.retry() },
// 							)
                        }
                    }
                }
            }
        }
    }
}
