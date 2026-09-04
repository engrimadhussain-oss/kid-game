package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.AlphabetItem
import com.example.data.AlphabetRepository
import com.example.data.db.AppDatabase
import com.example.data.db.GameProgressEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class GameTab {
    LEARN,
    BUBBLE_POP,
    MATCH,
    TRACE,
    STICKERS
}

enum class LetterCase {
    BOTH,
    UPPERCASE,
    LOWERCASE
}

data class BubbleItem(
    val id: Int,
    val letter: Char,
    val colorHex: Long,
    val initialOffsetX: Float, // 0.0f to 1.0f relative
    val initialOffsetY: Float, // 0.0f to 1.0f relative
    val sizeDp: Int = 72
)

data class StickerReward(
    val id: String,
    val title: String,
    val emoji: String,
    val requiredStars: Int,
    val description: String
)

data class AbcGameState(
    val currentTab: GameTab = GameTab.LEARN,
    val selectedLetterIndex: Int = 0,
    val letterCase: LetterCase = LetterCase.BOTH,
    val stars: Int = 0,
    val lettersExplored: Set<Char> = emptySet(),
    val stickersUnlocked: Set<String> = emptySet(),
    // Bubble Pop Game State
    val bubbleTargetLetter: Char = 'A',
    val bubbles: List<BubbleItem> = emptyList(),
    val poppedBubbleIds: Set<Int> = emptySet(),
    val bubbleSuccessCelebration: Boolean = false,
    // Picture Match Game State
    val matchTarget: AlphabetItem = AlphabetRepository.alphabetList.first(),
    val matchOptions: List<AlphabetItem> = emptyList(),
    val matchSelectedOption: AlphabetItem? = null,
    val matchIsCorrect: Boolean? = null,
    val matchCelebration: Boolean = false,
    // Tracing Screen State
    val traceLetter: Char = 'A',
    val traceColorHex: Long = 0xFFFF5964,
    val traceShowCelebration: Boolean = false
)

class AbcGameViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.gameProgressDao()
    val soundManager = SoundManager(application)

    val allStickers = listOf(
        StickerReward("first_star", "First Star", "⭐", 1, "Earned your very first star!"),
        StickerReward("balloon_pop", "Pop Explorer", "🎈", 3, "Popped letter balloons!"),
        StickerReward("magic_bubble", "Bubble Wiz", "🫧", 6, "Mastered letter pop!"),
        StickerReward("curious_cat", "Playful Cat", "🐱", 10, "Explored the alphabet!"),
        StickerReward("safari_lion", "Brave Lion", "🦁", 15, "Matched letters & pictures!"),
        StickerReward("shiny_crown", "Letter Royal", "👑", 20, "Traced and learned so much!"),
        StickerReward("magic_rainbow", "Rainbow Champ", "🌈", 30, "ABC superstar brilliance!"),
        StickerReward("master_trophy", "ABC Master", "🏆", 50, "You know the whole alphabet!")
    )

    private val _uiState = MutableStateFlow(AbcGameState())
    val uiState: StateFlow<AbcGameState> = _uiState.asStateFlow()

    init {
        loadProgress()
        startBubbleRound()
        startMatchRound()
    }

    private fun loadProgress() {
        viewModelScope.launch {
            val progress = dao.getProgressOnce() ?: GameProgressEntity(id = 1, stars = 0)
            val explored = progress.lettersExplored
                .split(",")
                .filter { it.isNotBlank() }
                .mapNotNull { it.firstOrNull() }
                .toSet()
            val stickers = progress.stickersUnlocked
                .split(",")
                .filter { it.isNotBlank() }
                .toSet()

            _uiState.update {
                it.copy(
                    stars = progress.stars,
                    lettersExplored = explored,
                    stickersUnlocked = stickers
                )
            }
            checkAndUnlockStickers(progress.stars)
        }
    }

    private fun saveProgress() {
        viewModelScope.launch {
            val current = _uiState.value
            val entity = GameProgressEntity(
                id = 1,
                stars = current.stars,
                lettersExplored = current.lettersExplored.joinToString(","),
                stickersUnlocked = current.stickersUnlocked.joinToString(",")
            )
            dao.saveProgress(entity)
        }
    }

    fun selectTab(tab: GameTab) {
        _uiState.update { it.copy(currentTab = tab) }
        when (tab) {
            GameTab.LEARN -> {
                val currentLetter = AlphabetRepository.alphabetList[_uiState.value.selectedLetterIndex]
                soundManager.speakLetterAndWord(currentLetter.letter, currentLetter.word)
            }
            GameTab.BUBBLE_POP -> {
                speakBubblePrompt(_uiState.value.bubbleTargetLetter)
            }
            GameTab.MATCH -> {
                speakMatchPrompt(_uiState.value.matchTarget)
            }
            GameTab.TRACE -> {
                soundManager.speak("Trace the letter ${_uiState.value.traceLetter}!")
            }
            GameTab.STICKERS -> {
                soundManager.speak("Look at your awesome sticker collection!")
            }
        }
    }

    // --- Learn Screen Actions ---
    fun selectLetter(index: Int) {
        val safeIndex = index.coerceIn(0, AlphabetRepository.alphabetList.size - 1)
        val item = AlphabetRepository.alphabetList[safeIndex]
        _uiState.update { state ->
            val updatedExplored = state.lettersExplored + item.letter
            state.copy(
                selectedLetterIndex = safeIndex,
                lettersExplored = updatedExplored,
                traceLetter = item.letter
            )
        }
        soundManager.speakLetterAndWord(item.letter, item.word)
        saveProgress()
    }

    fun nextLetter() {
        val next = (_uiState.value.selectedLetterIndex + 1) % AlphabetRepository.alphabetList.size
        selectLetter(next)
    }

    fun prevLetter() {
        val prev = if (_uiState.value.selectedLetterIndex - 1 < 0) {
            AlphabetRepository.alphabetList.size - 1
        } else {
            _uiState.value.selectedLetterIndex - 1
        }
        selectLetter(prev)
    }

    fun toggleLetterCase() {
        val nextCase = when (_uiState.value.letterCase) {
            LetterCase.BOTH -> LetterCase.UPPERCASE
            LetterCase.UPPERCASE -> LetterCase.LOWERCASE
            LetterCase.LOWERCASE -> LetterCase.BOTH
        }
        _uiState.update { it.copy(letterCase = nextCase) }
    }

    fun playCurrentPhonics() {
        val item = AlphabetRepository.alphabetList[_uiState.value.selectedLetterIndex]
        soundManager.speak(item.phonicsSound)
    }

    // --- Bubble Pop Game Actions ---
    fun startBubbleRound() {
        val randomItem = AlphabetRepository.alphabetList.random()
        val target = randomItem.letter
        val otherLetters = AlphabetRepository.alphabetList
            .filter { it.letter != target }
            .shuffled()
            .take(5)
            .map { it.letter }

        val bubbleLetters = (otherLetters + target).shuffled()
        val colors = listOf(
            0xFFFF5964, 0xFF4CC9F0, 0xFFFFD166, 0xFF06D6A0, 0xFF8338EC, 0xFFFF9F1C
        ).shuffled()

        val positions = listOf(
            Pair(0.12f, 0.15f),
            Pair(0.60f, 0.12f),
            Pair(0.20f, 0.42f),
            Pair(0.65f, 0.45f),
            Pair(0.15f, 0.72f),
            Pair(0.62f, 0.75f)
        ).shuffled()

        val bubbles = bubbleLetters.mapIndexed { idx, char ->
            val pos = positions.getOrElse(idx) { Pair(0.4f, 0.4f) }
            BubbleItem(
                id = idx,
                letter = char,
                colorHex = colors[idx % colors.size],
                initialOffsetX = pos.first,
                initialOffsetY = pos.second
            )
        }

        _uiState.update {
            it.copy(
                bubbleTargetLetter = target,
                bubbles = bubbles,
                poppedBubbleIds = emptySet(),
                bubbleSuccessCelebration = false
            )
        }
        speakBubblePrompt(target)
    }

    fun onBubbleTapped(bubble: BubbleItem) {
        if (_uiState.value.poppedBubbleIds.contains(bubble.id)) return

        if (bubble.letter == _uiState.value.bubbleTargetLetter) {
            // Correct bubble popped!
            soundManager.playPopSound()
            soundManager.playCelebrationChime()
            val newPopped = _uiState.value.poppedBubbleIds + bubble.id
            addStars(1)
            _uiState.update {
                it.copy(
                    poppedBubbleIds = newPopped,
                    bubbleSuccessCelebration = true
                )
            }
            soundManager.speakPraise()
            viewModelScope.launch {
                kotlinx.coroutines.delay(1600)
                startBubbleRound()
            }
        } else {
            // Wrong bubble
            soundManager.playGentleMiss()
            soundManager.speak("That is ${bubble.letter}! Find ${_uiState.value.bubbleTargetLetter}!")
        }
    }

    private fun speakBubblePrompt(letter: Char) {
        soundManager.speak("Pop the letter $letter!")
    }

    // --- Picture Match Game Actions ---
    fun startMatchRound() {
        val target = AlphabetRepository.alphabetList.random()
        val distractors = AlphabetRepository.alphabetList
            .filter { it.letter != target.letter }
            .shuffled()
            .take(2)
        val options = (distractors + target).shuffled()

        _uiState.update {
            it.copy(
                matchTarget = target,
                matchOptions = options,
                matchSelectedOption = null,
                matchIsCorrect = null,
                matchCelebration = false
            )
        }
        speakMatchPrompt(target)
    }

    private fun speakMatchPrompt(target: AlphabetItem) {
        soundManager.speak("What starts with the letter ${target.letter}?")
    }

    fun onMatchOptionTapped(item: AlphabetItem) {
        if (_uiState.value.matchIsCorrect == true) return

        val isMatch = (item.letter == _uiState.value.matchTarget.letter)
        if (isMatch) {
            soundManager.playCelebrationChime()
            soundManager.playStarSound()
            addStars(1)
            _uiState.update {
                it.copy(
                    matchSelectedOption = item,
                    matchIsCorrect = true,
                    matchCelebration = true
                )
            }
            soundManager.speak("Yes! ${item.letter} is for ${item.word}! Great job!")
            viewModelScope.launch {
                kotlinx.coroutines.delay(1800)
                startMatchRound()
            }
        } else {
            soundManager.playGentleMiss()
            _uiState.update {
                it.copy(
                    matchSelectedOption = item,
                    matchIsCorrect = false
                )
            }
            soundManager.speak("${item.word} starts with ${item.letter}. Try again!")
        }
    }

    // --- Tracing Actions ---
    fun setTraceLetter(letter: Char) {
        _uiState.update { it.copy(traceLetter = letter, traceShowCelebration = false) }
        soundManager.speak("Trace the letter $letter!")
    }

    fun setTraceColor(colorHex: Long) {
        _uiState.update { it.copy(traceColorHex = colorHex) }
    }

    fun completeTracing() {
        soundManager.playCelebrationChime()
        soundManager.playStarSound()
        soundManager.speakPraise()
        addStars(1)
        _uiState.update { it.copy(traceShowCelebration = true) }
    }

    fun dismissTraceCelebration() {
        _uiState.update { it.copy(traceShowCelebration = false) }
    }

    // --- General Rewards & Stars ---
    fun addStars(amount: Int = 1) {
        _uiState.update {
            val newStars = it.stars + amount
            it.copy(stars = newStars)
        }
        checkAndUnlockStickers(_uiState.value.stars)
        saveProgress()
    }

    private fun checkAndUnlockStickers(currentStars: Int) {
        val unlocked = allStickers
            .filter { currentStars >= it.requiredStars }
            .map { it.id }
            .toSet()

        _uiState.update { it.copy(stickersUnlocked = unlocked) }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.shutdown()
    }
}
