package com.viliussutkus89.codenet.tt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

// Taken from
// https://medium.com/nerd-for-tech/merging-livedata-like-you-need-it-3abcf6b756ca

// Modifications:
// not caching value before comparison in setValue and postValue

sealed class MergerLiveData<TargetType> : MediatorLiveData<TargetType>() {
    class Two<FirstSourceType, SecondSourceType, TargetType>(
        private val firstSource: LiveData<FirstSourceType>,
        private val secondSource: LiveData<SecondSourceType>,
        private val distinctUntilChanged: Boolean = true,
        private val merging: (FirstSourceType, SecondSourceType) -> TargetType
    ) : MediatorLiveData<TargetType>() {
        override fun onActive() {
            super.onActive()

            addSource(firstSource) { value ->
                postValue(
                    distinctUntilChanged = distinctUntilChanged,
                    newValue = merging(
                        value,
                        secondSource.value ?: return@addSource
                    )
                )
            }

            addSource(secondSource) { value ->
                postValue(
                    distinctUntilChanged = distinctUntilChanged,
                    newValue = merging(
                        firstSource.value ?: return@addSource,
                        value
                    )
                )
            }
        }

        override fun onInactive() {
            removeSource(firstSource)
            removeSource(secondSource)

            super.onInactive()
        }
    }
}

private fun <T> MediatorLiveData<T>.postValue(
    distinctUntilChanged: Boolean,
    newValue: T
) {
    if (distinctUntilChanged && value == newValue) return
    postValue(newValue)
}
