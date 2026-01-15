# SYSC 4806 – AddressBook (Labs 4–5)

A Spring Boot web application for managing address books and buddies, built with a focus on Continuous Integration (CI) and Continuous Delivery (CD). The application provides a RESTful API and a Thymeleaf-based web interface to create, view, and manage address books.

## CI Status
![Build](https://github.com/SalimHabbal/SYSC4806-Lab/actions/workflows/maven.yml/badge.svg?branch=main)

## 🌐 Live Deployment
The application is deployed on Azure App Service and automatically updates on every commit to `main`.

**Live URL:** [https://salimaddybook4806-hwfrasemh9hkczaz.canadacentral-01.azurewebsites.net/addressbooks/1](https://salimaddybook4806-hwfrasemh9hkczaz.canadacentral-01.azurewebsites.net/addressbooks/1)

---

## 🏗️ Architecture & Implementation

This project follows a standard **Model-View-Controller (MVC)** architecture using the **Spring Boot** framework. It leverages several key technologies:

*   **Spring Boot:** Simplifies the bootstrapping and development of the application.
*   **Spring Data JPA:** Provides an abstraction over the data access layer (DAO), making it easy to interact with the database.
*   **H2 Database:** An in-memory database used for development and testing simplicity.
*   **Thymeleaf:** A server-side Java template engine used for rendering the web UI (HTML).
*   **Maven:** Used for dependency management and build automation.

### 1. Domain Model (The "M" in MVC)
The core business logic revolves around two main entities:

*   **`AddressBook`**: Represents a collection of contacts.
    *   **One-to-Many Relationship**: An `AddressBook` contains a list of `BuddyInfo` objects.
    *   **Relationships**: It manages the lifecycle of its buddies (CascadeType.ALL), specifically adding and removing them.
*   **`BuddyInfo`**: Represents an individual contact.
    *   **Attributes**: Stores `name`, `phone`, and `address`.
    *   **Persistence**: Both entities are annotated with `@Entity`, allowing them to be automatically mapped to database tables by Hibernate (JPA provider).

### 2. Persistence Layer
The application uses **Spring Data JPA Repositories** to handle database operations without writing boilerplate SQL.

*   **`AddressBookRepository`**: Extends `CrudRepository<AddressBook, Long>`. Handles saving, finding, and deleting AddressBooks.
*   **`BuddyInfoRepository`**: Extends `CrudRepository<BuddyInfo, Long>`. Also includes custom finders method signatures like `findByName` and `findByPhone`, which Spring implements automatically at runtime.

### 3. REST API Layer (The "C" for API)
The **`AddressBookRestController`** exposes a JSON-based API for external clients or single-page applications.

*   **Base URL**: `/api`
*   **Endpoints**:
    *   `POST /api/addressbooks`: Creates a new AddressBook.
    *   `GET /api/addressbooks/{id}`: Retrieves a specific AddressBook (JSON).
    *   `POST /api/addressbooks/{id}/buddies`: Adds a new Buddy to an AddressBook. Expects a JSON body with name, phone, and address.
    *   `DELETE /api/addressbooks/{id}/buddies/{buddyId}`: Removes a specific Buddy from an AddressBook.
    *   `GET /api/buddies`: Lists all buddies, with optional filtering by `name` or `phone`.

### 4. Web View Layer (The "V" & "C" for Web)
The **`AddressBookViewController`** serves HTML pages using **Thymeleaf** templates. This allows users to interact with the application through a browser.

*   **Endpoints**:
    *   `GET /` or `/addressbooks`: Displays the home page with a list of all existing address books.
    *   `GET /addressbooks/{id}`: Displays the details of a specific AddressBook, listing its buddies.
    *   `POST /addressbooks`: Creates a new AddressBook via a form or button action.
    *   `POST /addressbooks/{id}/buddies`: Handles the form submission to add a new Buddy to the current book.
*   **Views**: The controller returns view names (like `"addressbooks"` or `"addressbook"`), which Thymeleaf resolves to HTML files in `src/main/resources/templates`.

---

## 🚀 Usage

### Run Locally
To start the application locally using Maven:

```bash
mvn spring-boot:run
```

Once running, you can access the app at: `http://localhost:8080`

### Test with HTTP Requests
You can interact with the API using `curl` or any API client.

**Create a Book:**
```bash
curl -X POST http://localhost:8080/api/addressbooks
```

**Add a Buddy:**
```bash
curl -X POST -H "Content-Type: application/json" \
     -d '{"name":"Alice", "phone":"613-555-0101", "address":"123 Main St"}' \
     http://localhost:8080/api/addressbooks/1/buddies
```

