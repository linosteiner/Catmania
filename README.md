# Catmania

Manage your cats!

## Instructions

Running the project locally
Prerequisites

- Java 21 (SDK)
- Node.js 20+ and npm (package manager)
- Unix/macOS or Windows terminal

Project overview

- Backend: Micronaut (Gradle). Default local port is typically 8080.
- Frontend: React + Vite. Default dev server port is 5173.

## 1. Database

- Make sure to have a local PostgreSQL server with version 17 running.
- Make sure you have the default postgres user.
- The database "catmania" will be created automatically by the DatabaseBootstrapper.
- If not, create it manually.

## 2. Backend (Micronaut, Gradle)

- Install dependencies and run:
    - macOS/Linux:
        - ./gradlew run

- Access API at:
    - [http://localhost:8080](http://localhost:8080)

## 3. Frontend (React + Vite)

- From the project root (where package.json is located):
    - Install: npm install
    - Run dev server: npm run dev

- Access UI at:
    - [http://localhost:5173](http://localhost:5173)
    - The default login credentials for the UI are:
      - Username: user
      - Password: secret123
