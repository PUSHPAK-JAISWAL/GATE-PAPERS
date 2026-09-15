package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.PaperEntity
import com.example.data.repository.PaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProgressStats(
    val totalPapers: Int = 0,
    val finishedCount: Int = 0,
    val pendingCount: Int = 0,
    val unattemptedCount: Int = 0,
    val csTotal: Int = 0,
    val csFinished: Int = 0,
    val csPending: Int = 0,
    val daTotal: Int = 0,
    val daFinished: Int = 0,
    val daPending: Int = 0
) {
    val overallPercentage: Int
        get() = if (totalPapers > 0) (finishedCount * 100) / totalPapers else 0

    val csPercentage: Int
        get() = if (csTotal > 0) (csFinished * 100) / csTotal else 0

    val daPercentage: Int
        get() = if (daTotal > 0) (daFinished * 100) / daTotal else 0
}

data class FilterCriteria(
    val section: String = PapersUiState.SECTION_ALL,
    val status: String = PapersUiState.STATUS_ALL,
    val query: String = ""
)

data class PapersUiState(
    val papers: List<PaperEntity> = emptyList(),
    val filteredPapers: List<PaperEntity> = emptyList(),
    val selectedSection: String = SECTION_ALL,
    val selectedStatusFilter: String = STATUS_ALL,
    val searchQuery: String = "",
    val activePaperForViewing: PaperEntity? = null,
    val stats: ProgressStats = ProgressStats(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val syncStatusMessage: String? = null
) {
    companion object {
        const val SECTION_ALL = "ALL"
        const val SECTION_CS = "GATE CS"
        const val SECTION_DA = "GATE DA"

        const val STATUS_ALL = "ALL"
        const val STATUS_PENDING = "PENDING"
        const val STATUS_FINISHED = "FINISHED"
        const val STATUS_UNATTEMPTED = "UNATTEMPTED"
    }
}

class PaperViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PaperRepository

    private val _selectedSection = MutableStateFlow(PapersUiState.SECTION_ALL)
    private val _selectedStatusFilter = MutableStateFlow(PapersUiState.STATUS_ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _activePaperForViewing = MutableStateFlow<PaperEntity?>(null)
    private val _isSyncing = MutableStateFlow(false)
    private val _syncStatusMessage = MutableStateFlow<String?>("Live GitHub Repos Connected")

    val uiState: StateFlow<PapersUiState>

    init {
        val db = AppDatabase.getInstance(application)
        repository = PaperRepository(db.paperDao())

        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
            // Dynamically fetch latest papers from GitHub on launch
            syncWithGitHub()
        }

        val filtersFlow = combine(
            _selectedSection,
            _selectedStatusFilter,
            _searchQuery
        ) { section, status, query ->
            FilterCriteria(section, status, query)
        }

        uiState = combine(
            repository.allPapers,
            filtersFlow,
            _activePaperForViewing,
            _isSyncing,
            _syncStatusMessage
        ) { allPapers, filters, viewingPaper, syncing, syncMsg ->

            val csList = allPapers.filter { it.section == PaperEntity.SECTION_CS }
            val daList = allPapers.filter { it.section == PaperEntity.SECTION_DA }

            val stats = ProgressStats(
                totalPapers = allPapers.size,
                finishedCount = allPapers.count { it.status == PaperEntity.STATUS_FINISHED },
                pendingCount = allPapers.count { it.status == PaperEntity.STATUS_PENDING || it.isFlaggedToRevisit },
                unattemptedCount = allPapers.count { it.status == PaperEntity.STATUS_UNATTEMPTED && !it.isFlaggedToRevisit },
                csTotal = csList.size,
                csFinished = csList.count { it.status == PaperEntity.STATUS_FINISHED },
                csPending = csList.count { it.status == PaperEntity.STATUS_PENDING || it.isFlaggedToRevisit },
                daTotal = daList.size,
                daFinished = daList.count { it.status == PaperEntity.STATUS_FINISHED },
                daPending = daList.count { it.status == PaperEntity.STATUS_PENDING || it.isFlaggedToRevisit }
            )

            // Filtering
            val filtered = allPapers.filter { paper ->
                // Section match
                val matchSection = when (filters.section) {
                    PapersUiState.SECTION_CS -> paper.section == PaperEntity.SECTION_CS
                    PapersUiState.SECTION_DA -> paper.section == PaperEntity.SECTION_DA
                    else -> true
                }

                // Status match
                val matchStatus = when (filters.status) {
                    PapersUiState.STATUS_FINISHED -> paper.status == PaperEntity.STATUS_FINISHED
                    PapersUiState.STATUS_PENDING -> paper.status == PaperEntity.STATUS_PENDING || paper.isFlaggedToRevisit
                    PapersUiState.STATUS_UNATTEMPTED -> paper.status == PaperEntity.STATUS_UNATTEMPTED && !paper.isFlaggedToRevisit
                    else -> true
                }

                // Pure name-based searching: strictly match the file name as requested
                val matchQuery = if (filters.query.isBlank()) {
                    true
                } else {
                    val clean = filters.query.trim().lowercase()
                    paper.githubFileName.lowercase().contains(clean) ||
                            paper.title.lowercase().contains(clean)
                }

                matchSection && matchStatus && matchQuery
            }

            // Sync updated active paper with database state if currently open
            val currentActive = viewingPaper?.let { curr ->
                allPapers.find { it.id == curr.id } ?: curr
            }

            PapersUiState(
                papers = allPapers,
                filteredPapers = filtered,
                selectedSection = filters.section,
                selectedStatusFilter = filters.status,
                searchQuery = filters.query,
                activePaperForViewing = currentActive,
                stats = stats,
                isSyncing = syncing,
                syncStatusMessage = syncMsg
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PapersUiState()
        )
    }

    /**
     * Dynamically fetches latest PDFs from both GitHub repositories:
     * https://github.com/PUSHPAK-JAISWAL/gatecs
     * https://github.com/PUSHPAK-JAISWAL/gateda
     */
    fun syncWithGitHub() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            _syncStatusMessage.value = "Fetching latest papers from GitHub..."
            val result = repository.syncWithGitHub()
            _isSyncing.value = false
            if (result.isSuccess) {
                val count = result.getOrDefault(0)
                _syncStatusMessage.value = "Synced $count papers from GitHub repos"
            } else {
                _syncStatusMessage.value = "Offline / Cached mode (Ready)"
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun selectSection(section: String) {
        _selectedSection.value = section
    }

    fun selectStatusFilter(filter: String) {
        _selectedStatusFilter.value = filter
    }

    fun openPaperViewer(paper: PaperEntity) {
        _activePaperForViewing.value = paper
    }

    fun closePaperViewer() {
        _activePaperForViewing.value = null
    }

    fun markPaperFinished(paper: PaperEntity) {
        viewModelScope.launch {
            if (paper.status == PaperEntity.STATUS_FINISHED) {
                repository.resetStatus(paper.id)
            } else {
                repository.markFinished(paper.id)
            }
        }
    }

    fun markPaperPending(paper: PaperEntity) {
        viewModelScope.launch {
            if (paper.status == PaperEntity.STATUS_PENDING || paper.isFlaggedToRevisit) {
                repository.resetStatus(paper.id)
            } else {
                repository.markPending(paper.id)
            }
        }
    }

    fun toggleRevisit(paper: PaperEntity) {
        viewModelScope.launch {
            repository.toggleRevisit(paper.id, paper.isFlaggedToRevisit)
        }
    }

    fun saveNotes(paperId: String, notes: String) {
        viewModelScope.launch {
            repository.updateNotes(paperId, notes)
        }
    }
}
