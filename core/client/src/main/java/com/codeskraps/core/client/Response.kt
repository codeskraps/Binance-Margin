package com.codeskraps.core.client

sealed interface Response<out D, out E: Error> {
    data class Success<D>(val data: D) : Response<D, Nothing>
    data class Failure<E: Error>(val error: E) : Response<Nothing, E>
}

sealed interface Error