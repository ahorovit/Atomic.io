package com.drspaceman.atomicio.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

//@InstallIn(ActivityRetainedComponent)
//@Module
//abstract class ViewModeModule {
//
//
//}



//@InstallIn(ActivityRetainedComponent)
//@Module
//abstract class ExampleModule {
    //// @Binds approach is more complex, and doesn't work with third party classes

//    @Singleton
//    @Binds
//    abstract fun bindSomeInterface(
//        someImpl: SomInterfaceImpl
//    ): SomeInterface


    ////@Provides is simpler, and will cover third party classes (and all other @Bind situations)

//    @Singleton
//    @Provides
//    fun provideSomeInterface(): SomeInterface {
//        return SomeIntercaceImpl()
//    }


    //// Again, but with Chained dependency: SomeDependency

//
//    @Singleton
//    @Provides
//    fun provideSomeDependency(): SomeDependency {
//        return SomeDependency()
//    }
//
//    @Singleton
//    @Provides
//    fun provideSomeInterface(
//        someDependency: SomeDependency
//    ): SomeInterface {
//        return SomeIntercaceImpl(someDependency)
//    }
//
//
//    //// How to differentiate different implementations for different contexts:
//
//    @Impl1
//    @Singleton
//    @Provides
//    fun provideSomeInterface1(): SomeInterface{
//        return SomeInterfaceImpl1()
//    }
//
//    @Impl2
//    @Singleton
//    @Provides
//    fun provideSomeInterface2(): SomeInterface{
//        return SomeInterfaceImpl2()
//    }
//}
//
//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class Impl1
//
//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class Impl2

//// EX consumer

//class SomeClass
//@Inject
//constructor(
//    @Impl1 private val someInterfaceImpl1: SomeInterface,
//    @Impl2 private val someInterfaceImpl2: SomeInterface
//) {
//    fun foobar() = "Baz"
//}