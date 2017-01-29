<div align="left">
  <img src="https://raw.githubusercontent.com/cj3ns3n/simple-account-service/master/account-icon.png"><br><br>
</div>

## Overview
This is a basic account service example.  It can be used to manage accounts and basic password authentication.

## Building
The typical Maven building commands are available.
To build the project execute:
mvn clean package 

## Startup
After a package is created, it can be deployed to your favorite servlet container like Tomcat.
The service can also be executed directly with Spring boot with the java command:
Java –jar target/simple-account-service-0.1.0.jar

## Usage
The service supports four end points:

* **URL**
/v1/accounts/create

* **Method:**
  `POST`
    
    *  **URL Params**

       **Required:**
        
`loginName=[string]`
`firstName=[string]`
`lastName=[string]`
`password=[string]`

* **Success Response:**

* **Code:** 200 <br />
**Content:** `{ status:”success”, acountId:[Integer value for the new accound] }`
			   
* **Error Response:**

* **Code:** 404 NOT FOUND<br />
**Content:** `{ status:”fail”}`

* **Code:** 500 INTERNAL SERVER ERROR<br />
**Content:** `Error: 404; Session: [Alphanumeric]`

* **Sample Call:**
curl -X POST -F "loginName=david" -F "password=something" –F "firstName=david" -F "lastName=jones" http://localhost:8080/v1/accounts/create

* **Notes:**
Currently there are no limits on the input values, so 500 is the only error response.  Later 400 could be a response with content describing the reason for the failure.

## Future Development
[ ] Authentication to return a token which can be used later to authenticate the user without requiring the
[ ] Non-global salt
[ ] Validation checks on account creation input values (password length, username uniqueness, etc)
[ ] Admin page


