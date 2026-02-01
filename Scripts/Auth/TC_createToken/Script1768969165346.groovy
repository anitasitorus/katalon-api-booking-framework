import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords.*

import groovy.json.JsonSlurper
import internal.GlobalVariable
import crypto.SimpleCrypto

// ===============================
// Decrypt password
// ===============================
String decryptedPassword = SimpleCrypto.decrypt(GlobalVariable.password)

// ===============================
// Send request
// ===============================
def response = sendRequest(findTestObject(
    'Object Repository/Authentication/CreateToken',
    [
        ('username'): GlobalVariable.username,
        ('password'): decryptedPassword
    ]
))

// ===============================
// Verify HTTP status
// ===============================
verifyResponseStatusCode(response, 200)

// ===============================
// Parse response & set token
// ===============================
def json = new JsonSlurper().parseText(response.getResponseText())
GlobalVariable.token = json.token

assert GlobalVariable.token?.trim() : "❌ Token is EMPTY"

println "🔐 TOKEN GENERATED SUCCESSFULLY"
println "🔐 TOKEN = ${GlobalVariable.token}"
