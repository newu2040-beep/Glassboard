package com.example.data

object PredictionEngine {
    private val nextWordPredictions = mapOf(
        "how" to listOf("are", "is", "about", "did", "to"),
        "what" to listOf("is", "are", "do", "about", "time"),
        "where" to listOf("are", "is", "did", "do", "can"),
        "why" to listOf("did", "are", "is", "would", "do"),
        "who" to listOf("is", "are", "was", "has", "would"),
        "thank" to listOf("you", "goodness", "fully", "someone", "them"),
        "please" to listOf("let", "help", "send", "find", "call"),
        "meet" to listOf("me", "you", "up", "at", "tomorrow"),
        "happy" to listOf("birthday", "anniversary", "to", "new", "hour"),
        "good" to listOf("morning", "afternoon", "evening", "night", "luck"),
        "i" to listOf("am", "will", "have", "would", "think", "dont", "love"),
        "you" to listOf("are", "should", "want", "can", "know", "will"),
        "this" to listOf("is", "was", "will", "app", "keyboard", "design"),
        "the" to listOf("best", "weather", "keyboard", "apple", "app", "ui"),
        "just" to listOf("wanted", "let", "checking", "fine", "curious"),
        "let" to listOf("us", "me", "know", "them", "go"),
        "call" to listOf("me", "you", "later", "back", "when"),
        "sorry" to listOf("for", "about", "i", "to", "bother"),
        "are" to listOf("you", "we", "they", "there", "the"),
        "can" to listOf("you", "i", "we", "be", "get", "do")
    )

    private val commonCorrections = mapOf(
        "teh" to "the",
        "recieve" to "receive",
        "wierd" to "weird",
        "definately" to "definitely",
        "seperate" to "separate",
        "untill" to "until",
        "truely" to "truly",
        "apporach" to "approach",
        "agrement" to "agreement",
        "realy" to "really",
        "dont" to "don't",
        "cant" to "can't",
        "im" to "i'm",
        "wont" to "won't",
        "whats" to "what's"
    )

    fun getPredictions(lastWord: String): List<String> {
        val word = lastWord.lowercase().trim()
        val defaultPredictions = listOf("the", "and", "to", "i", "you", "it")
        return nextWordPredictions[word] ?: defaultPredictions
    }

    fun checkAutoCorrection(word: String): String? {
        val cleanWord = word.lowercase().trim()
        return commonCorrections[cleanWord]
    }
}
