package com.example.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.data.model.PaperEntity
import com.example.data.model.QuestionItem
import com.example.data.repository.DefaultPapers

object PaperPrintHelper {

    /**
     * Prints the question paper using Android's native PrintManager.
     * Generates a clean, A4-formatted, official-style GATE Question Paper layout.
     * The user can print directly to a printer or "Save as PDF".
     */
    fun printQuestionPaper(context: Context, paper: PaperEntity) {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager == null) {
                Toast.makeText(context, "Printing not supported on this device", Toast.LENGTH_SHORT).show()
                return
            }

            val questions = DefaultPapers.getQuestionsForPaper(paper.id)
            val htmlContent = generatePrintableHtml(paper, questions)

            val webView = WebView(context)
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false

                override fun onPageFinished(view: WebView?, url: String?) {
                    val jobName = "${paper.title.replace(" ", "_")}_PrintJob"
                    val printAdapter = webView.createPrintDocumentAdapter(jobName)
                    val printAttributes = PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(PrintAttributes.Resolution("res1", "A4", 300, 300))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build()

                    printManager.print(jobName, printAdapter, printAttributes)
                }
            }

            webView.loadDataWithBaseURL(null, htmlContent, "text/html; charset=UTF-8", "UTF-8", null)
            Toast.makeText(context, "Opening print preview...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error launching print: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Downloads the question paper from the user's GitHub repository
     * using Android's DownloadManager.
     */
    fun downloadQuestionPaper(context: Context, paper: PaperEntity) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            if (downloadManager == null) {
                Toast.makeText(context, "Download manager not available", Toast.LENGTH_SHORT).show()
                return
            }

            val uri = Uri.parse(paper.rawFileUrl)
            val fileName = paper.githubFileName.ifEmpty { "${paper.id}.pdf" }

            val request = DownloadManager.Request(uri)
                .setTitle(paper.title)
                .setDescription("Downloading ${paper.title} from ${paper.githubRepo}")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            try {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            } catch (e: Exception) {
                // Fallback destination if scoped storage restrictions apply
                request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            }

            downloadManager.enqueue(request)
            Toast.makeText(context, "Download started: $fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            // Fallback: Open in browser if direct DownloadManager encounters policy/uri issues
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(paper.rawFileUrl))
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(browserIntent)
                Toast.makeText(context, "Opening download link in browser", Toast.LENGTH_SHORT).show()
            } catch (e2: Exception) {
                Toast.makeText(context, "Unable to start download: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Generates clean, printer-friendly black & white HTML with no gradients,
     * suitable for standard A4 paper printing.
     */
    private fun generatePrintableHtml(paper: PaperEntity, questions: List<QuestionItem>): String {
        val questionsHtml = StringBuilder()

        questions.forEach { q ->
            questionsHtml.append("""
                <div class="question-card">
                    <div class="q-meta">
                        <span class="q-num">Q.${q.number}</span>
                        <span class="q-badge">${q.type}</span>
                        <span class="q-topic">${q.topic}</span>
                        <span class="q-marks">[${q.marks} Mark${if (q.marks > 1) "s" else ""}${if (q.negativeMarks > 0) " | -${q.negativeMarks} Neg" else " | No Neg"}]</span>
                    </div>
                    <div class="q-text">${q.questionText}</div>
            """.trimIndent())

            if (q.options.isNotEmpty()) {
                questionsHtml.append("<div class=\"options-grid\">")
                q.options.forEach { opt ->
                    questionsHtml.append("<div class=\"option-item\">$opt</div>")
                }
                questionsHtml.append("</div>")
            }

            questionsHtml.append("""
                    <div class="answer-key-box">
                        <strong>Answer Key:</strong> ${q.correctAnswer}
                        <br/><span class="explanation"><strong>Explanation:</strong> ${q.explanation}</span>
                    </div>
                </div>
            """.trimIndent())
        }

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <style>
                    @page {
                        size: A4;
                        margin: 15mm 15mm 15mm 15mm;
                    }
                    body {
                        font-family: 'Helvetica Neue', Arial, sans-serif;
                        color: #111111;
                        background: #ffffff;
                        line-height: 1.4;
                        margin: 0;
                        padding: 10px;
                        font-size: 13px;
                    }
                    .header-box {
                        border: 2px solid #000000;
                        padding: 12px;
                        text-align: center;
                        margin-bottom: 18px;
                    }
                    .exam-title {
                        font-size: 18px;
                        font-weight: 800;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                        margin-bottom: 4px;
                    }
                    .exam-sub {
                        font-size: 14px;
                        font-weight: 600;
                        margin-bottom: 6px;
                    }
                    .exam-details {
                        display: flex;
                        justify-content: space-between;
                        font-size: 12px;
                        border-top: 1px solid #333333;
                        padding-top: 6px;
                        margin-top: 6px;
                    }
                    .section-banner {
                        background: #222222;
                        color: #ffffff;
                        padding: 6px 10px;
                        font-weight: bold;
                        font-size: 12px;
                        margin: 14px 0 10px 0;
                        text-transform: uppercase;
                    }
                    .question-card {
                        border-bottom: 1px dashed #777777;
                        padding: 10px 0 14px 0;
                        page-break-inside: avoid;
                    }
                    .q-meta {
                        font-size: 11px;
                        margin-bottom: 5px;
                    }
                    .q-num {
                        font-weight: bold;
                        font-size: 13px;
                        margin-right: 6px;
                    }
                    .q-badge {
                        background: #e2e8f0;
                        color: #0f172a;
                        padding: 2px 6px;
                        border-radius: 3px;
                        font-weight: bold;
                        margin-right: 6px;
                    }
                    .q-topic {
                        color: #475569;
                        margin-right: 6px;
                        font-style: italic;
                    }
                    .q-marks {
                        float: right;
                        font-weight: bold;
                    }
                    .q-text {
                        font-size: 13px;
                        font-weight: 500;
                        margin: 6px 0 8px 0;
                    }
                    .options-grid {
                        display: grid;
                        grid-template-columns: 1fr 1fr;
                        gap: 6px;
                        margin-bottom: 8px;
                    }
                    .option-item {
                        padding: 4px 8px;
                        border: 1px solid #cbd5e1;
                        border-radius: 4px;
                        font-size: 12px;
                    }
                    .answer-key-box {
                        background: #f8fafc;
                        border-left: 3px solid #0f172a;
                        padding: 6px 10px;
                        margin-top: 8px;
                        font-size: 11px;
                    }
                    .explanation {
                        color: #334155;
                    }
                    .footer-print {
                        text-align: center;
                        font-size: 10px;
                        color: #64748b;
                        margin-top: 20px;
                        border-top: 1px solid #e2e8f0;
                        padding-top: 8px;
                    }
                </style>
            </head>
            <body>
                <div class="header-box">
                    <div class="exam-title">GRADUATE APTITUDE TEST IN ENGINEERING</div>
                    <div class="exam-sub">${paper.title} &bull; ${paper.subtitle}</div>
                    <div class="exam-details">
                        <span><strong>Duration:</strong> ${paper.durationMinutes} Minutes</span>
                        <span><strong>Total Marks:</strong> ${paper.totalMarks}</span>
                        <span><strong>Paper Code:</strong> ${if (paper.section == PaperEntity.SECTION_CS) "CS" else "DA"}</span>
                        <span><strong>Questions:</strong> ${paper.totalQuestions}</span>
                    </div>
                </div>

                <div class="section-banner">QUESTION PAPER & REFERENCE SOLUTIONS</div>

                ${questionsHtml}

                <div class="footer-print">
                    Source: ${paper.githubRepo} &bull; Generated for offline printing & self-evaluation &bull; GATE CS & DA Progress Tracker
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}
