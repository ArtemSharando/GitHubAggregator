# GitHub Repository API Service

This project provides an API service to list GitHub repositories of a user, following specified acceptance criteria.

## Overview

The main functionality of the application is to list all repositories of a given GitHub username which are not forks. The API provides:

- Repository Name
- Owner Login
- For each branch: its name and the last commit SHA

Additionally, it handles specific error scenarios such as a non-existent GitHub user and unsupported `Accept` headers in requests.

## Table of Contents

- [Getting Started](#getting-started)
- [Usage](#usage)
- [Components](#components)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [License](#license)

## Getting Started

### Prerequisites

- Java 21
- Spring Boot 3
- Maven (for building and running)

### Building

To build the project, navigate to the project directory and execute:

```bash
mvn clean install
```

### Running
To run the project after building, execute:
```bash
mvn spring-boot:run
```

### Usage 
To fetch repositories for a specific GitHub user:
```bash
GET /repositories/{username}
Accept: application/json
```

Replace `{username}` with the desired GitHub username.

## **Components**

### Controllers

- `GitHubController`: The main controller handling incoming HTTP requests.

### Services

- `GitHubService`: Responsible for fetching data from the GitHub API.

### Interceptors

- `HeaderFilter`: Intercepts incoming requests to validate the `Accept` header.

### Models

- `Branch`: Represents a branch in a GitHub repository.
- `BranchDetails`: Provides details of a branch.
- `Commit`: Represents a commit in a GitHub repository.
- `Owner`: Represents the owner of a GitHub repository.
- `RepoDetails`: Represents detailed information about a repository.
- `Repository`: Represents a GitHub repository.

### Exceptions

- `UserNotFoundException`: Thrown when a specified GitHub user is not found.

## **Error Handling**

Errors are returned in the following format:

```json
{
    "status": "<HTTP_STATUS_CODE>",
    "Message": "<ERROR_MESSAGE>"
}
```

For example, when a user tries to fetch data for a non-existent GitHub user:

```json
{
  "status": 404,
  "Message": "User not found"
}
```

If a request comes with an Accept header value of application/xml, a 406 response is returned indicating "Unsupported data type".

### Testing

The application includes integration tests to validate its functionality against the acceptance criteria. Refer to GitHubIntegrationTest for details.
