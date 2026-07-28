package com.example.data.remote

import com.example.BuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun sendMessageToAi(
        userText: String,
        history: List<ChatMessage>,
        scenarioTopic: String = "general"
    ): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackResponse(userText, scenarioTopic)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val systemInstructionText = """
                You are Teacher Alex, a friendly, encouraging, and patient English language tutor for Arabic speakers.
                Your task is to engage in a conversation based on topic: $scenarioTopic.
                Guidelines:
                1. Respond in clear, natural English (1-3 sentences max).
                2. If the user made a grammar or spelling mistake in English, provide a gentle correction in English.
                3. Provide the Arabic translation of your English reply in brackets at the end.
                Format your response as valid JSON:
                {
                  "englishResponse": "Your English reply here.",
                  "arabicTranslation": "الترجمة العربية للرد هنا",
                  "grammarCorrection": "Gentle correction if any error, otherwise null"
                }
            """.trimIndent()

            val contentsArray = JSONArray()

            // Add history
            for (msg in history.takeLast(6)) {
                val role = if (msg.sender == ChatSender.USER) "user" else "model"
                val partObj = JSONObject().put("text", msg.textEn)
                val contentObj = JSONObject().put("role", role).put("parts", JSONArray().put(partObj))
                contentsArray.put(contentObj)
            }

            // Add current message
            val currentPartObj = JSONObject().put("text", userText)
            val currentContentObj = JSONObject().put("role", "user").put("parts", JSONArray().put(currentPartObj))
            contentsArray.put(currentContentObj)

            val jsonBody = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemInstructionText))))
                put("generationConfig", JSONObject().put("responseMimeType", "application/json"))
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful && responseString.isNotBlank()) {
                val jsonResponse = JSONObject(responseString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val textResult = parts?.optJSONObject(0)?.optString("text") ?: ""

                    if (textResult.isNotBlank()) {
                        val parsedObj = JSONObject(textResult)
                        val replyEn = parsedObj.optString("englishResponse", "That's great! Tell me more about that.")
                        val replyAr = parsedObj.optString("arabicTranslation", null)
                        val correction = parsedObj.optString("grammarCorrection", null).takeIf { it != "null" && !it.isNull_or_blank() }

                        return@withContext ChatMessage(
                            sender = ChatSender.AI,
                            textEn = replyEn,
                            translationAr = replyAr,
                            correction = correction
                        )
                    }
                }
            }
            return@withContext generateFallbackResponse(userText, scenarioTopic)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext generateFallbackResponse(userText, scenarioTopic)
        }
    }

    private fun String?.isNull_or_blank(): Boolean {
        return this == null || this.trim().isEmpty()
    }

    private fun generateFallbackResponse(userText: String, topic: String): ChatMessage {
        val lower = userText.lowercase().trim()
        val (en, ar, correction) = when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("مرحبا") -> Triple(
                "Hello there! I'm Alex, your AI English teacher. What topic would you like to practice today?",
                "أهلاً بك! أنا ألكس، معلم اللغة الإنجليزية. ما الموضوع الذي تود التدرب عليه اليوم؟",
                null
            )
            lower.contains("how are you") -> Triple(
                "I'm doing wonderful, thank you for asking! How is your day going so far?",
                "أنا بخير تماماً، شكراً لسؤالك! كيف يسير يومك حتى الآن؟",
                null
            )
            lower.contains("food") || lower.contains("restaurant") || topic == "RESTAURANT" -> Triple(
                "Welcome to our restaurant! May I take your order or would you like to see the menu?",
                "أهلاً بك في مطعمنا! هل يمكنني أخذ طلبك أم تود الإطلاع على قائمة الطعام؟",
                if (!userText.contains("order") && !userText.contains("menu")) "Tip: You can say 'I would like to order...' or 'Can I see the menu?'" else null
            )
            lower.contains("airport") || lower.contains("travel") || topic == "TRAVEL" -> Triple(
                "Welcome to the international airport! May I please see your passport and boarding pass?",
                "أهلاً بك في المطار الدولي! هل يمكنني رؤية جواز سفرك وتذكرة الصعود؟",
                null
            )
            lower.contains("job") || lower.contains("interview") || topic == "JOB_INTERVIEW" -> Triple(
                "Welcome to the interview! Could you please introduce yourself and mention your key strengths?",
                "أهلاً بك في المقابلة! هل يمكنك تقديم نفسك وذكر أهم نقاط قوتك؟",
                null
            )
            else -> Triple(
                "That sounds very interesting! Keep going! Could you explain that in one more sentence?",
                "هذا يبدو ممتعاً جداً! استمر! هل يمكنك شرح ذلك في جملة أخرى؟",
                null
            )
        }

        return ChatMessage(
            sender = ChatSender.AI,
            textEn = en,
            translationAr = ar,
            correction = correction
        )
    }
}
