package com.girogevoro.mapyandex.di

import com.girogevoro.mapyandex.data.RepositoryImpl
import com.girogevoro.mapyandex.domain.repository.Repository
import com.girogevoro.mapyandex.ui.map.MapViewModelImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Di{

    val repositoryModule = module {
        single<Repository> { RepositoryImpl(androidContext()) }
    }

    val viewModulesModule = module {
        viewModel {
            MapViewModelImpl(repository = get())
        }
    }

}
