import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords.*

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import internal.GlobalVariable
import java.text.SimpleDateFormat

// ===============================
// INIT REPORT STATUS
// ===============================
String testStatus = "PASS"
String statusClass = "success"
String errorMessage = ""

// ===============================
// PRE-CONDITION
// ===============================
assert GlobalVariable.token?.trim() : "❌ Token is EMPTY. Run TC_CreateToken first"
assert bookingId?.toString()?.trim() : "❌ bookingId from Excel is EMPTY"

// ===============================
// DATE NORMALIZER
// ===============================
String normalizeDate(String d) {
    def inFmt = new SimpleDateFormat("dd/MM/yyyy")
    def outFmt = new SimpleDateFormat("yyyy-MM-dd")
    return outFmt.format(inFmt.parse(d))
}

// ===============================
// NORMALIZE INPUT
// ===============================
boolean depositPaidBool = depositpaid.toString().toBoolean()
String checkinIso  = normalizeDate(checkin)
String checkoutIso = normalizeDate(checkout)

// ===============================
// BUILD REQUEST BODY
// ===============================
def requestBody = [
    firstname      : firstname,
    lastname       : lastname,
    totalprice     : totalprice as Integer,
    depositpaid    : depositPaidBool,
    bookingdates   : [
        checkin  : checkinIso,
        checkout : checkoutIso
    ],
    additionalneeds: additionalneeds
]

String requestJson = new JsonBuilder(requestBody).toPrettyString()

def response

try {

    // ===============================
    // PREPARE REQUEST OBJECT
    // ===============================
    RequestObject request = findTestObject(
        'Object Repository/Booking/UpdateBooking',
        ['bookingId': bookingId.toString()]
    )

    request.getHttpHeaderProperties().clear()

    request.getHttpHeaderProperties().add(
        new TestObjectProperty('Cookie', ConditionType.EQUALS, "token=${GlobalVariable.token}")
    )
    request.getHttpHeaderProperties().add(
        new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')
    )
    request.getHttpHeaderProperties().add(
        new TestObjectProperty('Accept', ConditionType.EQUALS, 'application/json')
    )

    request.setBodyContent(
        new HttpTextBodyContent(requestJson, 'UTF-8', 'application/json')
    )

    // ===============================
    // SEND REQUEST
    // ===============================
    response = sendRequest(request)
    verifyResponseStatusCode(response, 200)

    // ===============================
    // RESPONSE ASSERTION
    // ===============================
    def json = new JsonSlurper().parseText(response.getResponseBodyContent())

    assert json.firstname == firstname
    assert json.lastname == lastname
    assert json.totalprice == totalprice as Integer
    assert json.depositpaid == depositPaidBool
    assert json.additionalneeds == additionalneeds
    assert json.bookingdates.checkin == checkinIso
    assert json.bookingdates.checkout == checkoutIso

} catch (Throwable e) {
    testStatus = "FAIL"
    statusClass = "failed"
    errorMessage = e.message
}

// ===============================
// APPEND RESULT TO LISTENER REPORT
// ===============================
File reportFile = new File(GlobalVariable.REPORT_PATH)

reportFile << """
<table>
    <tr>
        <th>Result</th>
        <td class="${statusClass}">${testStatus}</td>
    </tr>
    <tr>
        <th>Booking ID</th>
        <td>${bookingId}</td>
    </tr>
    <tr>
        <th>Request</th>
        <td><pre>${requestJson}</pre></td>
    </tr>
    <tr>
        <th>Response</th>
        <td><pre>${response ? response.getResponseBodyContent() : 'NO RESPONSE'}</pre></td>

    </tr>
    ${errorMessage ? """
    <tr>
        <th>Error</th>
        <td><pre>${errorMessage}</pre></td>
    </tr>
    """ : ""}
</table>
<hr/>
"""
