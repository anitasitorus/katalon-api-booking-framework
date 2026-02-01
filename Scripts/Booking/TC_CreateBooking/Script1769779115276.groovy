import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords.*

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import internal.GlobalVariable

// ===============================
// Build request JSON
// ===============================
def requestData = [
    firstname: firstname,
    lastname: lastname,
    totalprice: totalprice as Integer,
    depositpaid: depositpaid as Boolean,
    bookingdates: [
        checkin: checkin,
        checkout: checkout
    ],
    additionalneeds: additionalneeds
]

def requestJson = new JsonBuilder(requestData).toPrettyString()

String testStatus = "PASS"
String statusClass = "success"
String errorMessage = ""
def bookingId = "N/A"
def postResponse
def getResponse

try {
    // ===============================
    // CREATE BOOKING
    // ===============================
    postResponse = sendRequest(findTestObject(
        'Object Repository/Booking/CreateBooking',
        ['body': requestJson]
    ))
    verifyResponseStatusCode(postResponse, 200)

    bookingId = new JsonSlurper()
            .parseText(postResponse.getResponseBodyContent())
            .bookingid

    // ===============================
    // GET BOOKING
    // ===============================
    getResponse = sendRequest(findTestObject(
        'Object Repository/Booking/GetBooking',
        ['bookingId': bookingId]
    ))
    verifyResponseStatusCode(getResponse, 200)

} catch (Throwable e) {
    testStatus = "FAIL"
    statusClass = "failed"
    errorMessage = e.message
}

// ===============================
// APPEND RESULT TO SUITE REPORT
// (DO NOT CREATE FILE HERE)
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
        <th>POST Response</th>
        <td><pre>${postResponse ? postResponse.getResponseBodyContent() : 'NO RESPONSE'}</pre></td>
    </tr>
    <tr>
        <th>GET Response</th>
        <td><pre>${getResponse ? getResponse.getResponseBodyContent() : 'NO RESPONSE'}</pre></td>
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
