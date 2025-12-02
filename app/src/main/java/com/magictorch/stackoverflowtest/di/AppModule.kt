package com.magictorch.stackoverflowtest.di

import com.magictorch.stackoverflowtest.data.api.StackOverflowApiService
import com.magictorch.stackoverflowtest.data.datasource.FollowLocalDataSource
import com.magictorch.stackoverflowtest.data.repository.UserRepositoryImpl
import com.magictorch.stackoverflowtest.domain.repository.UserRepository
import com.magictorch.stackoverflowtest.presentation.userlist.image.ImageLoader
import com.magictorch.stackoverflowtest.domain.usecase.GetUserListUseCase
import com.magictorch.stackoverflowtest.domain.usecase.ToggleFollowUseCase
import com.magictorch.stackoverflowtest.platform.presentation.image.CoilImageLoader
import com.magictorch.stackoverflowtest.presentation.userlist.UserListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val presentationModule = module {
    viewModel { UserListViewModel(get(), get()) }
}

val domainModule = module {
    single { GetUserListUseCase(get()) }
    single { ToggleFollowUseCase(get()) }
}

val dataModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single { FollowLocalDataSource(get()) }
    single<StackOverflowApiService> {
        Retrofit.Builder()
            .baseUrl("https://api.stackexchange.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StackOverflowApiService::class.java)
    }
}

val platformModule = module {
    single<ImageLoader> { CoilImageLoader() }
}
