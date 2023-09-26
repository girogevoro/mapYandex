package com.girogevoro.mapyandex.domain.repository

import android.location.Location

interface LocationProvider {
    fun getLocation(): Location?
}