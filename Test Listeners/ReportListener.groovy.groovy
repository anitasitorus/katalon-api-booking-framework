import com.kms.katalon.core.annotation.*
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.context.TestSuiteContext
import internal.GlobalVariable
import java.text.SimpleDateFormat

class ReportListener {

    static boolean initialized = false

    @BeforeTestSuite
    def beforeSuite(TestSuiteContext context) {

        if (initialized) {
            println "⚠️ Report already initialized, skipping BeforeTestSuite"
            return
        }
        initialized = true

        GlobalVariable.REPORT_TS =
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())

        String suiteName = context.getTestSuiteId()
                .replaceAll('[\\\\/:*?"<>|]', '_')
                .replaceAll('\\s+', '_')

        String reportsDir = RunConfiguration.getProjectDir() + "/Reports"
        new File(reportsDir).mkdirs()

        GlobalVariable.REPORT_PATH =
    "${reportsDir}/${suiteName}-${GlobalVariable.REPORT_TS}.html"


        File report = new File(GlobalVariable.REPORT_PATH)
report.delete()
report.createNewFile()
report.text = """
<!DOCTYPE html>
<html>
<head>
    <title>Booking API Report</title>
    <style>
        body { font-family: Arial; padding: 20px; }
        table { border-collapse: collapse; width: 80%; margin-bottom: 20px; }
        th, td { border: 1px solid #ccc; padding: 10px; }
        th { background-color: #f4f4f4; }
        .success { color: green; font-weight: bold; }
        .failed { color: red; font-weight: bold; }
        pre { background: #f9f9f9; padding: 10px; }
        hr { margin: 30px 0; }
    </style>
</head>
<body>
<h2>Booking API Test Report</h2>
<hr/>
"""

        println "📄 REPORT INITIALIZED ONCE: ${GlobalVariable.REPORT_PATH}"
    }

    @AfterTestSuite
    def afterSuite(TestSuiteContext context) {

        if (!initialized) return

        new File(GlobalVariable.REPORT_PATH) << """
<p><b>Generated at:</b> ${new Date()}</p>
</body>
</html>
"""
        println "✅ REPORT CLOSED"
    }
}
