package com.girogevoro.mapyandex.di

import androidx.room.Room
import com.girogevoro.mapyandex.data.RepositoryImpl
import com.girogevoro.mapyandex.data.locationProvider.LocationProvider
import com.girogevoro.mapyandex.data.locationProvider.LocationProviderImpl
import com.girogevoro.mapyandex.data.room.MarkerDatabase
import com.girogevoro.mapyandex.domain.repository.Repository
import com.girogevoro.mapyandex.ui.map.MapViewModelImpl
import com.girogevoro.mapyandex.ui.markers.MarkersViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Di {
    private const val DB_NAME = "MarkerDataBase"

    val repositoryModule = module {
        single<Repository> {
            RepositoryImpl(
                androidContext(),
                locationProvider = get(),
                markerDatabase = get()
            )
        }

        single<LocationProvider> { LocationProviderImpl(androidContext()) }
    }

    val viewModulesModule = module {
        viewModel {
            MapViewModelImpl(repository = get())
        }

        viewModel{
            MarkersViewModel(repository = get())
        }
    }

    fun storageModule() = module {
        single<MarkerDatabase> {
            Room.databaseBuilder(androidContext(), MarkerDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}
