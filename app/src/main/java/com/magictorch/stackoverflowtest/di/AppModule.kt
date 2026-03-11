package com.magictorch.stackoverflowtest.di

import com.magictorch.stackoverflowtest.data.api.StackOverflowApiService
import com.magictorch.stackoverflowtest.data.repository.UserRepositoryImpl
import com.magictorch.stackoverflowtest.domain.repository.UserRepository
import com.magictorch.stackoverflowtest.domain.usecase.GetUserDetailUseCase
import com.magictorch.stackoverflowtest.domain.usecase.GetUserListUseCase
import com.magictorch.stackoverflowtest.domain.util.StringDecoder
import com.magictorch.stackoverflowtest.platform.presentation.image.CoilImageLoader
import com.magictorch.stackoverflowtest.platform.util.AndroidStringDecoder
import com.magictorch.stackoverflowtest.presentation.image.ImageLoader
import com.magictorch.stackoverflowtest.presentation.userdetail.UserDetailViewModel
import com.magictorch.stackoverflowtest.presentation.userlist.UserListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val presentationModule = module {
    viewModel { UserListViewModel(get()) }
    viewModel { (userId: Int) -> UserDetailViewModel(get(), userId) }
}

val domainModule = module {
    single { GetUserListUseCase(get()) }
    single { GetUserDetailUseCase(get()) }
}

val dataModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<StackOverflowApiService> {
        Retrofit.Builder()
            .baseUrl("https://api.stackexchange.com/2.3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StackOverflowApiService::class.java)
    }
}

val platformModule = module {
    single<ImageLoader> { CoilImageLoader() }
    single<StringDecoder> { AndroidStringDecoder() }
}
