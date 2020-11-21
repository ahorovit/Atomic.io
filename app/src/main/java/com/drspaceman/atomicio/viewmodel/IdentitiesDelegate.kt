package com.drspaceman.atomicio.viewmodel

import androidx.lifecycle.*
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class IdentitiesDelegate
@Inject
constructor(
    private val atomicIoRepo: AtomicIoRepository
) : ViewModel(), IdentitiesViewModelInterface {

    // @todo: use liveData builder
    private val _identities = MediatorLiveData<List<IdentityViewData>>()
    override val identities: LiveData<List<IdentityViewData>>
        get() = _identities

    var isLoaded = false

    init {
        _identities.value = listOf()

        viewModelScope.launch {
            _identities.addSource(Transformations.map(atomicIoRepo.allIdentities) { repoIdentities ->
                repoIdentities.map { identity ->
                    IdentityViewData.of(identity)
                }
            }) { identityViewData ->
                isLoaded = true
                _identities.value = identityViewData
            }
        }
    }
}

interface IdentitiesViewModelInterface {

    val identities: LiveData<List<IdentityViewData>>
}