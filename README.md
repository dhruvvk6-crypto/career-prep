# CareerPrep

A full-stack career-preparation platform that helps learners build momentum through structured learning paths, coding practice, interview simulations, and progress insights.

## Highlights

- Secure account registration and sign-in with JWT-based authentication
- Role-focused learning paths and topic-level progress tracking
- Practice challenges with attempt history and feedback
- Configurable mock interviews, answer capture, and scoring
- Personal dashboard for interview scores, completed sessions, and skill progress
- Responsive React workspace backed by a Spring Boot REST API

## Tech Stack

| Area | Technologies |
| --- | --- |
| Frontend | React, Vite, JavaScript, CSS |
| Backend | Java 21, Spring Boot, Spring Security, Spring Data JPA |
| Data | MySQL, Hibernate |
| Authentication | JSON Web Tokens (JWT) |
| Testing | JUnit, Spring Boot Test |

## Project Structure

```text
career-prep/
├── frontend/                  # React + Vite single-page application
├── src/main/java/             # Spring Boot API, domain, services, and security
├── src/main/resources/        # Application configuration
└── src/test/                  # Service-level tests
```

## Run Locally

### 1. Start MySQL

Create a database named `careerprep` (or provide a different connection string through `DB_URL`).

### 2. Configure the backend

Set these environment variables before starting the API:

```text
DB_URL=jdbc:mysql://localhost:3306/careerprep
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_base64_encoded_32_byte_or_longer_secret
FRONTEND_ORIGIN=http://localhost:5173
```

### 3. Start the API

```bash
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd spring-boot:run`.

### 4. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Open the Vite URL shown in the terminal (normally `http://localhost:5173`).

## Recruiter Notes

CareerPrep demonstrates end-to-end product development: an authenticated client experience, RESTful backend services, persistent domain modeling, business logic for practice and interview workflows, and automated service tests.

