<div align="left">
  <img src="https://raw.githubusercontent.com/cj3ns3n/simple-account-service/master/account-icon.png"><br><br>
</div>

# Overview
This is a basic account service example.  It can be used to manage accounts and basic password authentication.

# Building
The typical Maven building commands are available.
To build the project execute:

mvn clean package 

# Startup
After a package is created, it can be deployed to your favorite servlet container like Tomcat.

The service can also be executed directly with Spring boot with the java command:<br>
java -jar target/simple-account-service-0.1.0.jar

# Usage
The service supports four end points:

## Create
**URL**
/v1/accounts/create

**Method:** `POST`    

**URL Params**

**Required:**
- `loginName=[string]`
- `firstName=[string]`
- `lastName=[string]`
- `password=[string]`

**Success Response:**

***Code:*** 201 <br />
***Headers*** `Location,[URL of the created resource]` <br/>
***Content:*** `{}`
			   
**Sample Call:**

curl -X POST -F "loginName=david" -F "password=something" –F "firstName=david" -F "lastName=jones" http://localhost:8080/v1/accounts/create

**Notes:**
Currently there are no limits on the input values, so 500 is the only error response.  In later versions, 400 could be a response with content describing the reason for the failure.

## Get Account
**URL**
/v1/accounts/{account-id}

**Method:** `GET`    

**URL Params**
- None

**Success Response:**

***Code:*** 200 <br />
***Content:*** `
{
  "id" : [Integer],
  "loginName" : [String],
  "firstName" : [String],
  "lastName" : [String]
}`
			   
**Error Response:**

***Code:*** 404 NOT FOUND<br />
***Content:*** ``

**Sample Call:**

curl http://localhost:8080/v1/accounts/1000000

## List Accounts
**URL**
/v1/accounts/list

**Method:** `GET`    

**URL Params**

**Optional:**
- `limit=[Integer]` - limits the number of accounts in the response

**Success Response:**

***Code:*** 200 <br />
***Content:*** `
[{
  "id" : [Integer],
  "loginName" : [String],
  "firstName" : [String],
  "lastName" : [String]
}, .....]`
			   
**Error Response:**

**Sample Call:**

curl http://localhost:8080/v1/accounts/list

## Authenticate
**URL**
/v1/accounts/authenticate

**Method:** `POST`    

**URL Params**

**Required:**
- `accountId=[integer]`
- `password=[string]`

**Success Response:**

***Code:*** 200 <br />
***Content:*** `{ "status":"success", "roles":"" }`
			   
**Error Response:**

***For invalid passwords or accound Id*** <br>
***Code:*** 400 BAD REQUEST<br />
***Content:*** ``

**Sample Call:**

curl -X POST -F "id=99" -F "password=something" http://localhost:8080/v1/accounts/authenticate

**Notes:**
- Authenticate uses a post method so that the parameters can be encrypted when deployed on a web server with SSL enabled.
- Response body has an unused "roles" field.  This is for planned future capabilities of providing authorization roles.

## General Notes:
Internal errors are returned with a session ID that can be used to track down errors within the logs

## Future Development
 - Authentication to return a token which can be used later to authenticate the user without requiring the account password.
 - Authorization roles
 - Non-global salt
 - Validation checks on account creation input values (password length, username uniqueness, etc)
 - Admin page
 - Exception handler controller

