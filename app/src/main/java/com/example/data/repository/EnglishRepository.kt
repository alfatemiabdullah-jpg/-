package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EnglishRepository(
    private val database: EnglishDatabase,
    private val geminiService: GeminiService
) {
    private val wordDao = database.wordDao()
    private val phraseDao = database.phraseDao()
    private val userProgressDao = database.userProgressDao()

    val allWords: Flow<List<WordItem>> = wordDao.getAllWords().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val bookmarkedWords: Flow<List<WordItem>> = wordDao.getBookmarkedWords().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val learnedWords: Flow<List<WordItem>> = wordDao.getLearnedWords().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val allPhrases: Flow<List<PhraseItem>> = phraseDao.getAllPhrases().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val userProgress: Flow<UserProgress> = userProgressDao.getUserProgress().map { entity ->
        entity?.toDomainModel() ?: UserProgress()
    }

    suspend fun toggleBookmark(wordId: Int, isBookmarked: Boolean) {
        wordDao.updateBookmark(wordId, isBookmarked)
    }

    suspend fun toggleLearned(wordId: Int, isLearned: Boolean) {
        wordDao.updateLearned(wordId, isLearned)
        if (isLearned) {
            userProgressDao.incrementWordsLearned()
        }
    }

    suspend fun recordQuizScore(score: Int) {
        userProgressDao.addQuizResult(score)
    }

    suspend fun sendMessageToAi(userText: String, history: List<ChatMessage>, topic: String): ChatMessage {
        return geminiService.sendMessageToAi(userText, history, topic)
    }

    suspend fun checkAndSeedInitialData() {
        if (wordDao.getWordCount() == 0) {
            wordDao.insertWords(initialWordsList)
        }
        if (phraseDao.getPhraseCount() == 0) {
            phraseDao.insertPhrases(initialPhrasesList)
        }
        userProgressDao.insertOrUpdateProgress(UserProgressEntity(id = 1))
    }

    val grammarTopics: List<GrammarTopic> = listOf(
        GrammarTopic(
            id = "present_simple",
            titleAr = "المضارع البسيط (Present Simple)",
            titleEn = "Present Simple Tense",
            level = "A1",
            summaryAr = "يُستخدم للتعبير عن الحقائق والمواعيد والعادات اليومية.",
            explanationAr = "نستخدم المضارع البسيط للحديث عن أشياء نفعلها بشكل متكرر أو حقائق ثابتة. مع الهي والشي والإت (He / She / It) نضيف حرف s للفعل.",
            examples = listOf(
                "I drink coffee every morning." to "أنا أشرب القهوة كل صباح.",
                "She works in a bank." to "هي تعمل في بنك.",
                "The sun rises in the east." to "الشمس تشرق من الشرق."
            )
        ),
        GrammarTopic(
            id = "past_simple",
            titleAr = "الماضي البسيط (Past Simple)",
            titleEn = "Past Simple Tense",
            level = "A1",
            summaryAr = "يُستخدم للحديث عن أحداث اكتملت في الماضي.",
            explanationAr = "نضيف (ed) للأفعال المنتظمة للحديث عن حدث انتهى بالماضي، أو نستخدم التصريف الثاني للأفعال الشاذة.",
            examples = listOf(
                "I visited London last year." to "زرت لندن العام الماضي.",
                "They bought a new car." to "هم اشتروا سيارة جديدة.",
                "He studied hard for the test." to "هو درس بجد للاختبار."
            )
        ),
        GrammarTopic(
            id = "future_simple",
            titleAr = "المستقبل البسيط (Future with Will / Going to)",
            titleEn = "Future Tense",
            level = "A2",
            summaryAr = "التعبير عن الخطط والقرارات المستقبلية.",
            explanationAr = "نستخدم (Will) للقرارات السريعة والوعود، بينما نستخدم (Going to) للخطط المدروسة مسبقاً.",
            examples = listOf(
                "I will call you tonight." to "سأتصل بك الليلة.",
                "We are going to travel tomorrow." to "نحن مسافرون غداً."
            )
        ),
        GrammarTopic(
            id = "modal_verbs",
            titleAr = "أفعال المودال والقدرة (Can / Must / Should)",
            titleEn = "Modal Verbs",
            level = "A2",
            summaryAr = "التعبير عن الاستطاعة، النصيحة، والواجب.",
            explanationAr = "تُستخدم Can للقدرة، Should للنصيحة، و Must للواجب أو الإلزام الصارم.",
            examples = listOf(
                "I can speak English fluently." to "أستطيع تحدث الإنجليزية بطلاقة.",
                "You should rest well." to "يجب عليك أن ترتاح جيداً.",
                "You must follow the traffic rules." to "يجب عليك اتباع قواعد المرور."
            )
        ),
        GrammarTopic(
            id = "present_perfect",
            titleAr = "المضارع التام (Present Perfect)",
            titleEn = "Present Perfect Tense",
            level = "B1",
            summaryAr = "ربط الماضي بالحاضر أو التجارب الشخصية.",
            explanationAr = "يُصاغ باستخدام Have / Has + التصريف الثالث (Past Participle). يُستخدم للخبرات والتجارب التي لا نحدد زمن حدوثها بالضبط.",
            examples = listOf(
                "I have lived here for five years." to "لقد عشت هنا لمدة خمس سنوات.",
                "Have you ever been to Japan?" to "هل سبق لك أن زرت اليابان؟"
            )
        )
    )

    val quizQuestionsList: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = 1,
            questionTextAr = "ما معنى كلمة 'Opportunity' باللغة العربية؟",
            questionTextEn = "Choose the correct meaning of 'Opportunity'",
            options = listOf("فرصة", "تحدي", "قرار", "نجاح"),
            correctAnswerIndex = 0,
            explanationAr = "كلمة Opportunity تعني فرصة (مثال: Equal opportunities = فرص متكافئة).",
            type = QuizType.TRANSLATE_EN_AR
        ),
        QuizQuestion(
            id = 2,
            questionTextAr = "اختر الترجمة الإنجليزية الصحيحة لجملة: 'أنا أبحث عن عمل'",
            options = listOf("I am looking for a job", "I am working a job", "I looked at job", "I will lose a job"),
            correctAnswerIndex = 0,
            explanationAr = "'Look for' يعني يبحث عن، بينما 'Look at' يعني ينظر إلى.",
            type = QuizType.TRANSLATE_AR_EN
        ),
        QuizQuestion(
            id = 3,
            questionTextAr = "أكمل الجملة بالكلمة المناسبة: She _____ to school every morning.",
            options = listOf("goes", "go", "went", "going"),
            correctAnswerIndex = 0,
            explanationAr = "نستخدم المضارع البسيط مع الفاعل المفرد (She) بتمثيل إضافة es للفعل (goes).",
            type = QuizType.FILL_BLANK
        ),
        QuizQuestion(
            id = 4,
            questionTextAr = "استمع للكلمة واختر المعنى الصحيح:",
            questionTextEn = "Welcome",
            options = listOf("أهلاً وسهلاً", "مع السلامة", "شكراً جزيلاً", "إلى اللقاء"),
            correctAnswerIndex = 0,
            explanationAr = "كلمة Welcome تعني أهلاً وسهلاً أو مرحباً بك.",
            type = QuizType.LISTENING,
            audioText = "Welcome to our English application"
        ),
        QuizQuestion(
            id = 5,
            questionTextAr = "ما معنى الجملة: 'Break a leg' في الثقافة الإنجليزية؟",
            options = listOf("أتمنى لك التوفيق (حظاً سعيداً)", "اكسر ساقك", "ابتعد عن المكان", "خذ استراحة"),
            correctAnswerIndex = 0,
            explanationAr = "هذا تعبير اصطلاحي (Idiom) يُستخدم في الإنجليزية لتمني التوفيق والمرح قبل الأداء.",
            type = QuizType.TRANSLATE_EN_AR
        ),
        QuizQuestion(
            id = 6,
            questionTextAr = "ما هو التصريف الماضي للفعل الإنجليزي 'Buy' (يشترِ)؟",
            options = listOf("Bought", "Buyed", "Buying", "Buys"),
            correctAnswerIndex = 0,
            explanationAr = "الفعل Buy هو فعل غير منتظم (Irregular verb)، وماضيه هو Bought.",
            type = QuizType.FILL_BLANK
        )
    )

    private val initialWordsList = listOf(
        // Level A1 - Beginners
        WordEntity(
            english = "Welcome",
            arabic = "مرحباً / أهلاً بك",
            phonetic = "/ˈwel.kəm/",
            category = "GREETINGS",
            exampleEn = "Welcome to our learning app!",
            exampleAr = "أهلاً بك في تطبيقنا التعليمي!",
            level = "A1"
        ),
        WordEntity(
            english = "Opportunity",
            arabic = "فرصة",
            phonetic = "/ˌɒp.əˈtʃuː.nə.ti/",
            category = "GENERAL",
            exampleEn = "Learning English opens new opportunities.",
            exampleAr = "تعلم الإنجليزية يفتح فرصاً جديدة.",
            level = "A2"
        ),
        WordEntity(
            english = "Achieve",
            arabic = "يحقق / ينجز",
            phonetic = "/əˈtʃiːv/",
            category = "SUCCESS",
            exampleEn = "You can achieve your goals with practice.",
            exampleAr = "يمكنك تحقيق أهدافك بالممارسة.",
            level = "B1"
        ),
        WordEntity(
            english = "Fluent",
            arabic = "طلق اللسان / فصيح",
            phonetic = "/ˈfluː.ənt/",
            category = "EDUCATION",
            exampleEn = "She wants to become fluent in English.",
            exampleAr = "هي تريد أن تصبح طليقة باللغة الإنجليزية.",
            level = "B1"
        ),
        WordEntity(
            english = "Journey",
            arabic = "رحلة",
            phonetic = "/ˈdʒɜː.ni/",
            category = "TRAVEL",
            exampleEn = "Enjoy your learning journey!",
            exampleAr = "استمتع برحلتك التعليمية!",
            level = "A1"
        ),
        WordEntity(
            english = "Confidence",
            arabic = "ثقة بالنفس",
            phonetic = "/ˈkɒn.fɪ.dəns/",
            category = "MINDSET",
            exampleEn = "Speak with confidence.",
            exampleAr = "تحدث بثقة.",
            level = "A2"
        ),
        WordEntity(
            english = "Passport",
            arabic = "جواز سفر",
            phonetic = "/ˈpɑːs.pɔːt/",
            category = "TRAVEL",
            exampleEn = "Keep your passport in a safe place.",
            exampleAr = "احتفظ بجواز سفرك في مكان آمن.",
            level = "A1"
        ),
        WordEntity(
            english = "Restaurant",
            arabic = "مطعم",
            phonetic = "/ˈres.trɒnt/",
            category = "FOOD",
            exampleEn = "Let's meet at the Italian restaurant.",
            exampleAr = "لنلتقِ في المطعم الإيطالي.",
            level = "A1"
        ),
        WordEntity(
            english = "Experience",
            arabic = "خبرة / تجربة",
            phonetic = "/ɪkˈspɪə.ri.əns/",
            category = "BUSINESS",
            exampleEn = "He has five years of work experience.",
            exampleAr = "لديه خمس سنوات من الخبرة العملية.",
            level = "B1"
        ),
        WordEntity(
            english = "Perspective",
            arabic = "منظور / وجهة نظر",
            phonetic = "/pəˈspek.tɪv/",
            category = "ADVANCED",
            exampleEn = "Try to see things from a different perspective.",
            exampleAr = "حاول رؤية الأشياء من منظور مختلف.",
            level = "B2"
        ),
        WordEntity(
            english = "Ambitious",
            arabic = "طموح",
            phonetic = "/æmˈbɪʃ.əs/",
            category = "PERSONALITY",
            exampleEn = "She is an ambitious student.",
            exampleAr = "إنها طالبة طموحة.",
            level = "B2"
        ),
        WordEntity(
            english = "Schedule",
            arabic = "جدول زمني / مواعيد",
            phonetic = "/ˈʃed.juːl/",
            category = "DAILY_LIFE",
            exampleEn = "I have a busy schedule today.",
            exampleAr = "لدي جدول أعمال مزدحم اليوم.",
            level = "A2"
        )
    )

    private val initialPhrasesList = listOf(
        PhraseEntity(
            english = "How are you doing today?",
            arabic = "كيف حالك اليوم؟",
            category = "GREETINGS",
            phonetic = "هاو آر يو دوينغ تو داي"
        ),
        PhraseEntity(
            english = "Could you please speak a bit slower?",
            arabic = "هل يمكنك التحدث ببطء أكثر من فضلك؟",
            category = "DAILY_LIFE",
            phonetic = "كود يو بليز سبيك أبِت سلوور"
        ),
        PhraseEntity(
            english = "I would like to order coffee, please.",
            arabic = "أود طلب القهوة من فضلك.",
            category = "RESTAURANT",
            phonetic = "آي وود لايك تو أوردر كوفي بليز"
        ),
        PhraseEntity(
            english = "Where is the nearest subway station?",
            arabic = "أين أقرب محطة مترو أنفاق؟",
            category = "TRAVEL",
            phonetic = "وير إز ذا نيرست ساب واي ستيشن"
        ),
        PhraseEntity(
            english = "Nice to meet you!",
            arabic = "سُعدت بلقائك!",
            category = "GREETINGS",
            phonetic = "نايس تو ميت يو"
        ),
        PhraseEntity(
            english = "What do you do for a living?",
            arabic = "ماذا تعمل لكسب عيشك؟",
            category = "BUSINESS",
            phonetic = "وات دو يو دو فور أ ليفينغ"
        )
    )
}
