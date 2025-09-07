# Catmania

Running the project locally
Prerequisites

- Java 21 (SDK)
- Node.js 20+ and npm (package manager)
- Unix/macOS or Windows terminal

Project overview

- Backend: Micronaut (Gradle). Default local port is typically 8080.
- Frontend: React + Vite. Default dev server port is 5173.
- The backend can also serve static assets from src/main/resources/public (useful for production or a single-process
  local run after building the frontend).

## 1. Backend (Micronaut, Gradle)

- Install dependencies and run:
    - macOS/Linux:
        - ./gradlew run

    - Windows:
        - gradlew.bat run

- Access API at:
    - [http://localhost:8080](http://localhost:8080)

- Useful commands:
    - Run tests: ./gradlew test
    - Build: ./gradlew build
    - Clean: ./gradlew clean

- Configuration:
    - Edit src/main/resources/application.yml for local settings (e.g., DB, ports).
    - You can override properties with environment variables or JVM system properties when needed.

## 2. Frontend (React + Vite)

- From the project root (where package.json is located):
    - Install: npm install
    - Run dev server: npm run dev

- Access UI at:
    - [http://localhost:5173](http://localhost:5173)
- The default Login User for the UI is:
    - Username: user
    - Password: secret123

## 3. Database

- Make sure to have a local PostgreSQL Server with Version 17 running.
- Make sure you have the default postgres user.
- The database "catmania" will be created automatically by the DataBaseBootstrapper.
- If not, create it manually.
