package com.example.data.repository

import com.example.data.model.PaperEntity
import com.example.data.model.QuestionItem

object DefaultPapers {

    fun getInitialPapers(): List<PaperEntity> {
        val list = mutableListOf<PaperEntity>()

        // ================= GATE CS PAPERS (From https://github.com/PUSHPAK-JAISWAL/gatecs) =================
        list.add(
            PaperEntity(
                id = "gatecs_2026_CS2.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2026,
                title = "2026_CS2.pdf",
                subtitle = "2.9 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2026_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2026_CS2.pdf",
                fileSizeBytes = 2905404L,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2026_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2026,
                title = "2026_CS1.pdf",
                subtitle = "2.4 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2026_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2026_CS1.pdf",
                fileSizeBytes = 2455522L,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2025_CS2.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2025,
                title = "2025_CS2.pdf",
                subtitle = "784 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2025_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2025_CS2.pdf",
                fileSizeBytes = 784221L,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2025_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2025,
                title = "2025_CS1.pdf",
                subtitle = "1.0 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2025_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2025_CS1.pdf",
                fileSizeBytes = 1024592L,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2024_CS2.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2024,
                title = "2024_CS2.pdf",
                subtitle = "886 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2024_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2024_CS2.pdf",
                fileSizeBytes = 886167L,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2024_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2024,
                title = "2024_CS1.pdf",
                subtitle = "834 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2024_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2024_CS1.pdf",
                fileSizeBytes = 834514L,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2023_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2023,
                title = "2023_CS.pdf",
                subtitle = "424 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2023_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2023_CS.pdf",
                fileSizeBytes = 424406L,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2022_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2022,
                title = "2022_CS.pdf",
                subtitle = "567 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2022_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2022_CS.pdf",
                fileSizeBytes = 567522L,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2021_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2021,
                title = "2021_CS.pdf",
                subtitle = "18.3 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2021_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2021_CS.pdf",
                fileSizeBytes = 18388574L,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2020_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2020,
                title = "2020_CS.pdf",
                subtitle = "18.3 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2020_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2020_CS.pdf",
                fileSizeBytes = 18388574L,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2019_CS2.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2019,
                title = "2019_CS2.pdf",
                subtitle = "1.8 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2019_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2019_CS2.pdf",
                fileSizeBytes = 1887436L,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2019_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2019,
                title = "2019_CS1.pdf",
                subtitle = "1.9 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2019_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2019_CS1.pdf",
                fileSizeBytes = 1992294L,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2018_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2018,
                title = "2018_CS.pdf",
                subtitle = "1.2 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2018_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2018_CS.pdf",
                fileSizeBytes = 1258291L,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2017_CS2.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2017,
                title = "2017_CS2.pdf",
                subtitle = "1.4 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2017_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2017_CS2.pdf",
                fileSizeBytes = 1468006L,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2017_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2017,
                title = "2017_CS1.pdf",
                subtitle = "1.3 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2017_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2017_CS1.pdf",
                fileSizeBytes = 1363148L,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2016_CS2.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2016,
                title = "2016_CS2.pdf",
                subtitle = "1.1 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2016_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2016_CS2.pdf",
                fileSizeBytes = 1153433L,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2016_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2016,
                title = "2016_CS1.pdf",
                subtitle = "1.1 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2016_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2016_CS1.pdf",
                fileSizeBytes = 1153433L,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2015_CS3.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2015,
                title = "2015_CS3.pdf",
                subtitle = "950 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 3,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2015_CS3.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2015_CS3.pdf",
                fileSizeBytes = 972800L,
                coreTopics = "Computer Science & IT Shift 3"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2015_CS2.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2015,
                title = "2015_CS2.pdf",
                subtitle = "980 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2015_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2015_CS2.pdf",
                fileSizeBytes = 1003520L,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2015_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2015,
                title = "2015_CS1.pdf",
                subtitle = "960 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2015_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2015_CS1.pdf",
                fileSizeBytes = 983040L,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2014_CS3.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2014,
                title = "2014_CS3.pdf",
                subtitle = "890 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 3,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2014_CS3.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2014_CS3.pdf",
                fileSizeBytes = 911360L,
                coreTopics = "Computer Science & IT Shift 3"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2014_CS2.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2014,
                title = "2014_CS2.pdf",
                subtitle = "910 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2014_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2014_CS2.pdf",
                fileSizeBytes = 931840L,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2014_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2014,
                title = "2014_CS1.pdf",
                subtitle = "920 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2014_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2014_CS1.pdf",
                fileSizeBytes = 942080L,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2013_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2013,
                title = "2013_CS.pdf",
                subtitle = "750 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2013_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2013_CS.pdf",
                fileSizeBytes = 768000L,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )

        // ================= GATE DA PAPERS (From https://github.com/PUSHPAK-JAISWAL/gateda) =================
        list.add(
            PaperEntity(
                id = "gateda_DA2026.pdf",
                section = PaperEntity.SECTION_DA,
                year = 2026,
                title = "DA2026.pdf",
                subtitle = "2.1 MB • PUSHPAK-JAISWAL/gateda",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gateda",
                githubFileName = "DA2026.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gateda/main/DA2026.pdf",
                fileSizeBytes = 2116753L,
                coreTopics = "Data Science & AI Exam Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gateda_DA2025.pdf",
                section = PaperEntity.SECTION_DA,
                year = 2025,
                title = "DA2025.pdf",
                subtitle = "7.9 MB • PUSHPAK-JAISWAL/gateda",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gateda",
                githubFileName = "DA2025.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gateda/main/DA2025.pdf",
                fileSizeBytes = 7897137L,
                coreTopics = "Data Science & AI Exam Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gateda_DA2024.pdf",
                section = PaperEntity.SECTION_DA,
                year = 2024,
                title = "DA2024.pdf",
                subtitle = "991 KB • PUSHPAK-JAISWAL/gateda",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gateda",
                githubFileName = "DA2024.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gateda/main/DA2024.pdf",
                fileSizeBytes = 991009L,
                coreTopics = "Data Science & AI Exam Paper"
            )
        )

        return list
    }

    fun getQuestionsForPaper(paperId: String): List<QuestionItem> {
        val isDA = paperId.contains("da", ignoreCase = true)
        if (isDA) {
            return listOf(
                QuestionItem(
                    number = 1,
                    sectionName = "General Aptitude",
                    type = "MCQ",
                    marks = 1,
                    negativeMarks = 0.33,
                    topic = "Verbal Aptitude",
                    questionText = "The team members discussed the project proposal _____ great detail before arriving at a unanimous decision.",
                    options = listOf("A) at", "B) in", "C) with", "D) on"),
                    correctAnswer = "B) in",
                    explanation = "The correct English idiomatic phrase is 'in great detail'."
                ),
                QuestionItem(
                    number = 2,
                    sectionName = "Probability & Statistics",
                    type = "MCQ",
                    marks = 1,
                    negativeMarks = 0.33,
                    topic = "Random Variables",
                    questionText = "Let X be a continuous random variable uniformly distributed over the interval [2, 10]. What is the expected value E[X] and Variance Var(X)?",
                    options = listOf(
                        "A) E[X] = 6, Var(X) = 5.33",
                        "B) E[X] = 6, Var(X) = 8.00",
                        "C) E[X] = 5, Var(X) = 6.67",
                        "D) E[X] = 8, Var(X) = 4.00"
                    ),
                    correctAnswer = "A) E[X] = 6, Var(X) = 5.33",
                    explanation = "For uniform distribution U(a,b): E[X] = (a+b)/2 = (2+10)/2 = 6. Var(X) = (b-a)^2 / 12 = 8^2 / 12 = 64/12 = 5.33."
                )
            )
        } else {
            return listOf(
                QuestionItem(
                    number = 1,
                    sectionName = "General Aptitude",
                    type = "MCQ",
                    marks = 1,
                    negativeMarks = 0.33,
                    topic = "Verbal Aptitude",
                    questionText = "Identify the word that is an antonym for 'EPHEMERAL':",
                    options = listOf("A) Transient", "B) Fleeting", "C) Permanent", "D) Momentary"),
                    correctAnswer = "C) Permanent",
                    explanation = "Ephemeral means lasting for a very short time. Permanent means lasting indefinitely."
                ),
                QuestionItem(
                    number = 2,
                    sectionName = "Algorithms & Data Structures",
                    type = "MCQ",
                    marks = 2,
                    negativeMarks = 0.66,
                    topic = "Divide and Conquer",
                    questionText = "The recurrence relation T(n) = 2T(n/2) + n*log(n) with T(1) = 1 has an asymptotic time complexity of:",
                    options = listOf(
                        "A) Theta(n * log(n))",
                        "B) Theta(n * log^2(n))",
                        "C) Theta(n^2)",
                        "D) Theta(n^2 * log(n))"
                    ),
                    correctAnswer = "B) Theta(n * log^2(n))",
                    explanation = "By extended Master Theorem: a=2, b=2, log_b(a) = log_2(2) = 1. f(n) = n^1 * log^1(n). Since f(n) = Theta(n^(log_b a) * log^k n) with k=1, T(n) = Theta(n * log^(k+1) n) = Theta(n * log^2(n))."
                )
            )
        }
    }
}
