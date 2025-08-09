# Employee service

Employee service helps end to users to manager employee data.


## Approach

1. Implemented the service using the Spring Boot framework.

2. Utilized Resilience4j and RestTemplate for invoking mock-server endpoints.

3. Integrated Swagger to generate comprehensive API documentation.

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

8. Check for successful start of application API server

```
Tomcat started on port 8111 (http) with context path '/'
```

## API details for API server
```
UI format: http://localhost:8111/swagger-ui/index.html

API docs: http://localhost:8111/v2/api-docs

```
## Implementation details

1. Utilized the mock-server endpoints /api/v1/employee, /api/v1/employee/{id}, and /api/v1/employee to implement getAllEmployees, getEmployeeById, and createEmployee.

2. Added filtering logic on the /api/v1/employee response to support getEmployeesByNameSearch, getHighestSalaryOfEmployees, and getTop10HighestEarningEmployeeNames functionality.

3. For deleteEmployeeById, first retrieved the employee’s name using /api/v1/employee/{id}, then invoked the mock-server endpoint /api/v1/employee/{name}.

4. Implemented automatic retries (up to 3 attempts) for 5xx errors from the mock-server, using exponential backoff.

5. Added handling for 429 (Too Many Requests) responses from the mock-server.

6. Gracefully handled error responses from the mock-server within the API server.

## Code repository

https://github.com/chinmay12/ReliaQuest


## Authors

Chinmay Nalawade email: nalawade.chinmay@gmail.com
