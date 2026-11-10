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
                subtitle = "2.8 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2026_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2026_CS2.pdf",
                fileSizeBytes = 2905404L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2026_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2026,
                title = "2026_CS1.pdf",
                subtitle = "2.3 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2026_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2026_CS1.pdf",
                fileSizeBytes = 2455522L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2025_CS2.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2025,
                title = "2025_CS2.pdf",
                subtitle = "766 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2025_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2025_CS2.pdf",
                fileSizeBytes = 784221L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2025_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2025,
                title = "2025_CS1.pdf",
                subtitle = "1001 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2025_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2025_CS1.pdf",
                fileSizeBytes = 1024592L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2024_CS2.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2024,
                title = "2024_CS2.pdf",
                subtitle = "865 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2024_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2024_CS2.pdf",
                fileSizeBytes = 886167L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2024_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2024,
                title = "2024_CS1.pdf",
                subtitle = "815 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2024_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2024_CS1.pdf",
                fileSizeBytes = 834514L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2023_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2023,
                title = "2023_CS.pdf",
                subtitle = "414 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2023_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2023_CS.pdf",
                fileSizeBytes = 424406L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2022_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2022,
                title = "2022_CS.pdf",
                subtitle = "554 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2022_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2022_CS.pdf",
                fileSizeBytes = 567522L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2021_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2021,
                title = "2021_CS.pdf",
                subtitle = "17.5 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2021_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2021_CS.pdf",
                fileSizeBytes = 18388574L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2020_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2020,
                title = "2020_CS.pdf",
                subtitle = "2.2 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2020_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2020_CS.pdf",
                fileSizeBytes = 2283406L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2019_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2019,
                title = "2019_CS.pdf",
                subtitle = "2.4 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2019_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2019_CS.pdf",
                fileSizeBytes = 2514739L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2018_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2018,
                title = "2018_CS.pdf",
                subtitle = "388 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2018_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2018_CS.pdf",
                fileSizeBytes = 397332L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2017_CS2.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2017,
                title = "2017_CS2.pdf",
                subtitle = "3.9 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 2,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2017_CS2.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2017_CS2.pdf",
                fileSizeBytes = 4085742L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Shift 2"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2017_CS1.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2017,
                title = "2017_CS1.pdf",
                subtitle = "8.5 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2017_CS1.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2017_CS1.pdf",
                fileSizeBytes = 8911960L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Shift 1"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2016_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2016,
                title = "2016_CS.pdf",
                subtitle = "557 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2016_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2016_CS.pdf",
                fileSizeBytes = 570576L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2015_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2015,
                title = "2015_CS.pdf",
                subtitle = "9.0 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2015_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2015_CS.pdf",
                fileSizeBytes = 9455569L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2014_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2014,
                title = "2014_CS.pdf",
                subtitle = "2.4 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2014_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2014_CS.pdf",
                fileSizeBytes = 2494424L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2013_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2013,
                title = "2013_CS.pdf",
                subtitle = "735 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2013_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2013_CS.pdf",
                fileSizeBytes = 753124L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2012_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2012,
                title = "2012_CS.pdf",
                subtitle = "299 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2012_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2012_CS.pdf",
                fileSizeBytes = 305852L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2011_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2011,
                title = "2011_CS.pdf",
                subtitle = "1.3 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2011_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2011_CS.pdf",
                fileSizeBytes = 1377241L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2010_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2010,
                title = "2010_CS.pdf",
                subtitle = "190 KB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2010_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2010_CS.pdf",
                fileSizeBytes = 194309L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2009_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2009,
                title = "2009_CS.pdf",
                subtitle = "2.4 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2009_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2009_CS.pdf",
                fileSizeBytes = 2565530L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2008_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2008,
                title = "2008_CS.pdf",
                subtitle = "2.9 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2008_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2008_CS.pdf",
                fileSizeBytes = 3086066L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Computer Science & IT Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gatecs_2007_CS.pdf",
                section = PaperEntity.SECTION_CS,
                year = 2007,
                title = "2007_CS.pdf",
                subtitle = "3.2 MB • PUSHPAK-JAISWAL/gatecs",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gatecs",
                githubFileName = "2007_CS.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/2007_CS.pdf",
                fileSizeBytes = 3304840L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
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
                subtitle = "2.0 MB • PUSHPAK-JAISWAL/gateda",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gateda",
                githubFileName = "DA2026.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gateda/main/DA2026.pdf",
                fileSizeBytes = 2116753L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Data Science & AI Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gateda_DA2025.pdf",
                section = PaperEntity.SECTION_DA,
                year = 2025,
                title = "DA2025.pdf",
                subtitle = "7.5 MB • PUSHPAK-JAISWAL/gateda",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gateda",
                githubFileName = "DA2025.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gateda/main/DA2025.pdf",
                fileSizeBytes = 7897137L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Data Science & AI Official Paper"
            )
        )
        list.add(
            PaperEntity(
                id = "gateda_DA2024.pdf",
                section = PaperEntity.SECTION_DA,
                year = 2024,
                title = "DA2024.pdf",
                subtitle = "968 KB • PUSHPAK-JAISWAL/gateda",
                setNumber = 1,
                githubRepo = "PUSHPAK-JAISWAL/gateda",
                githubFileName = "DA2024.pdf",
                rawFileUrl = "https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gateda/main/DA2024.pdf",
                fileSizeBytes = 991009L,
                totalMarks = 100,
                durationMinutes = 180,
                totalQuestions = 65,
                coreTopics = "Data Science & AI Official Paper"
            )
        )
        return list
    }

    fun getQuestionsForPaper(paperId: String): List<QuestionItem> {
        val isCs = !paperId.startsWith("gateda")
        return if (isCs) {
            listOf(
                QuestionItem(
                    number = 1,
                    sectionName = "General Aptitude",
                    type = "MCQ",
                    marks = 1,
                    negativeMarks = 0.33,
                    topic = "Verbal Ability",
                    questionText = "The committee decided to _________ the proposal until further environmental feasibility studies were completed.",
                    options = listOf("A) defer", "B) differ", "C) deflect", "D) degrade"),
                    correctAnswer = "A",
                    explanation = "'Defer' means to put off to a later time or postpone."
                ),
                QuestionItem(
                    number = 2,
                    sectionName = "General Aptitude",
                    type = "NAT",
                    marks = 2,
                    negativeMarks = 0.0,
                    topic = "Quantitative Aptitude",
                    questionText = "A sequence of numbers satisfies x_(n+1) = 2*x_n + 1 with x_0 = 1. The value of x_5 is _______.",
                    options = emptyList(),
                    correctAnswer = "63",
                    explanation = "x_0=1, x_1=3, x_2=7, x_3=15, x_4=31, x_5=63 (General form: 2^(n+1) - 1)."
                ),
                QuestionItem(
                    number = 3,
                    sectionName = "Computer Science Core",
                    type = "MCQ",
                    marks = 2,
                    negativeMarks = 0.66,
                    topic = "Algorithms & Data Structures",
                    questionText = "What is the worst-case running time of QuickSort on an array of n elements when median-of-three partitioning is used?",
                    options = listOf("A) O(n log n)", "B) O(n^2)", "C) O(n)", "D) O(n^(1.5))"),
                    correctAnswer = "B",
                    explanation = "Even with median-of-three pivot selection, pathological adversarial inputs can still yield O(n^2) worst-case time."
                ),
                QuestionItem(
                    number = 4,
                    sectionName = "Computer Science Core",
                    type = "MSQ",
                    marks = 2,
                    negativeMarks = 0.0,
                    topic = "Operating Systems",
                    questionText = "Which of the following conditions are necessary for a deadlock to occur in a system with non-shareable resources?",
                    options = listOf(
                        "A) Mutual Exclusion",
                        "B) Hold and Wait",
                        "C) No Preemption",
                        "D) Circular Wait"
                    ),
                    correctAnswer = "A, B, C, D",
                    explanation = "All four Coffman conditions (Mutual exclusion, Hold and wait, No preemption, and Circular wait) are simultaneously necessary."
                ),
                QuestionItem(
                    number = 5,
                    sectionName = "Computer Science Core",
                    type = "NAT",
                    marks = 2,
                    negativeMarks = 0.0,
                    topic = "Computer Networks",
                    questionText = "A TCP connection uses an RTT of 20 ms with a maximum segment size (MSS) of 1 KB. If the congestion window is 16 KB, what is the maximum achievable throughput in Mbps? (Enter integer value)",
                    options = emptyList(),
                    correctAnswer = "6.4",
                    explanation = "Throughput = (Window Size) / RTT = (16 * 1024 * 8 bits) / (0.020 s) = 6.55 Mbps ≈ 6.4 Mbps with standard decimal calculation."
                )
            )
        } else {
            listOf(
                QuestionItem(
                    number = 1,
                    sectionName = "General Aptitude",
                    type = "MCQ",
                    marks = 1,
                    negativeMarks = 0.33,
                    topic = "Analytical Reasoning",
                    questionText = "In a bivariate dataset, if the Pearson correlation coefficient r is 0, this implies:",
                    options = listOf(
                        "A) Variables are completely independent",
                        "B) No linear association exists between the variables",
                        "C) The scatter plot is a horizontal straight line",
                        "D) Both variables have zero variance"
                    ),
                    correctAnswer = "B",
                    explanation = "Zero Pearson correlation specifically indicates the absence of a linear relationship; non-linear associations may still exist."
                ),
                QuestionItem(
                    number = 2,
                    sectionName = "Data Science Core",
                    type = "MCQ",
                    marks = 2,
                    negativeMarks = 0.66,
                    topic = "Machine Learning",
                    questionText = "Which loss function is strictly convex and commonly used for training Binary Logistic Regression models?",
                    options = listOf(
                        "A) Mean Squared Error",
                        "B) Binary Cross-Entropy (Log Loss)",
                        "C) Hinge Loss",
                        "D) Huber Loss"
                    ),
                    correctAnswer = "B",
                    explanation = "Binary Cross-Entropy (Negative Log Likelihood) is strictly convex for linear combinations, ensuring a single global minimum."
                ),
                QuestionItem(
                    number = 3,
                    sectionName = "Data Science Core",
                    type = "NAT",
                    marks = 2,
                    negativeMarks = 0.0,
                    topic = "Probability & Statistics",
                    questionText = "A fair coin is tossed 4 times independently. The probability of getting at least 3 heads is _______ (round off to 4 decimal places).",
                    options = emptyList(),
                    correctAnswer = "0.3125",
                    explanation = "P(X >= 3) = (4C3 + 4C4) / 2^4 = (4 + 1)/16 = 5/16 = 0.3125."
                ),
                QuestionItem(
                    number = 4,
                    sectionName = "Artificial Intelligence Core",
                    type = "MSQ",
                    marks = 2,
                    negativeMarks = 0.0,
                    topic = "Search Algorithms",
                    questionText = "Which properties are true for A* tree search with heuristic function h(n)?",
                    options = listOf(
                        "A) A* is optimal if h(n) is admissible",
                        "B) A* is complete on finite graphs with positive step costs",
                        "C) If h(n) = 0 for all nodes, A* behaves like Breadth-First Search",
                        "D) Consistent heuristics are always admissible"
                    ),
                    correctAnswer = "A, B, D",
                    explanation = "When h(n)=0, A* behaves like Uniform Cost Search (Dijkstra), not BFS (unless all edge costs are uniform). A, B, and D are true."
                )
            )
        }
    }
}
