# Employee service

Employee service helps end to users to manager employee data.


## Approach
1. I have used spring boot framework.
2. I have used resillence4j and rest-template to call mock-server endpoints
3. Swagger library is used to provide appropriate API documentation.

## Getting Started

Instructions to get the application up and running on local setup.

### Prerequisites

Install following list of software

```
JDK 17 installed on the machine

```

### Deploying the service on local setup

A step by step series of examples that tell you how to get a development env running


1. Create a temp folder with command on your machine

```
   mkdir employee-serice
```

2. Navigate to the folder

```
   cd employee-serice
```


3. Clone the git repository

```
   git clone https://github.com/chinmay12/ReliaQuest.git
```

4. Navigate to the folder

```
   cd ReliaQuest
```

5. Gradle build the employee service application using the below command

```
./gradlew clean build

```

6. Start the mock server with command 

```
./gradlew server:bootRun
```

7. Start the api server with command

```
./gradlew api:bootRun
```

8. Check for successful start of application

```
Tomcat started on port 8111 (http) with context path '/'
```

## API details
```
UI format: http://localhost:8111/swagger-ui/index.html

API docs: http://localhost:8080/v2/api-docs

```
## Implementation details

1. I have utilised the mock-server endpoints /api/v1/employee, /api/v1/employee/{id}, /api/v1/employee to handle getAllEmployees, getEmployeeById, createEmployee.
2. I have added some filtering logic over /api/v1/employee response to address functionality getEmployeesByNameSearch, getHighestSalaryOfEmployees, getTop10HighestEarningEmployeeNames.
3. To implement deleteEmployeeById, I have used /api/v1/employee/{id} to get name details of employee and call mock server endpoint /api/v1/employee/{name}.
4. I have implemented a auto-retry 3 times for 5xx errors received from mock-server with exponential backoff.
5. I have handled 429 error code returned by the mock server.
6. I have gracefully handled error response from the mock server in api server.

## Code repository

https://github.com/chinmay12/ReliaQuest


## Authors

Chinmay Nalawade email: nalawade.chinmay@gmail.com
