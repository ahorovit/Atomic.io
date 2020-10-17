package com.drspaceman.atomicio.adapter

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandledOrReturnNull(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}


//class MyViewModel() : ViewModel(){
//    val networkError = MutableLiveData<Event<String>>()
//
//}
//
//viewModel.networkError.observe(this, Observer { event ->
//    event?.getContentIfNotHandledOrReturnNull()?.let {
//        showToast(it)
//    }
//})