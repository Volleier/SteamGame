<p align="center">
  <h1 align="center">SteamGame</h1>
  <p align="center">
    <strong>Record, sync, and explore your Steam game library with a self-hosted dashboard.</strong>
  </p>
  <p align="center">
    <a href="https://www.java.com/"><img src="https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white" alt="Java 21"></a>
    <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 3.4"></a>
    <a href="https://vuejs.org/"><img src="https://img.shields.io/badge/Vue-3.3-4FC08D?logo=vuedotjs&logoColor=white" alt="Vue 3"></a>
    <img src="https://img.shields.io/badge/TypeScript-5.8-3178C6?logo=typescript&logoColor=white" alt="TypeScript">
    <img src="https://img.shields.io/badge/Vite-4.4-646CFF?logo=vite&logoColor=white" alt="Vite">
    <img src="https://img.shields.io/badge/Tailwind-3.4-06B6D4?logo=tailwindcss&logoColor=white" alt="Tailwind CSS">
    <img src="https://img.shields.io/badge/SCSS-1.87-CC6699?logo=sass&logoColor=white" alt="SCSS">
    <img src="https://img.shields.io/badge/H2-embedded-blue?logo=h2&logoColor=white" alt="H2 Database">
    <img src="https://img.shields.io/badge/platform-Windows-0078D6?logo=windows" alt="Windows">
  </p>
</p>

<p align="center">
  <img src="docs/image/Intro.png" alt="SteamGame Dashboard">
</p>

---

SteamGame is a full-stack, self-hosted web dashboard that connects to the Steam Web API, syncs your game library and metadata, and displays them through a sleek, responsive Vue 3 interface. Credentials are encrypted with AES-256-GCM when stored statically. The backend runs on Java 21 and Spring Boot 3.4, and comes with a built-in H2 database—no external database server needed.

## Tech Stack

| Layer          | Technology                               |
| -------------- | ---------------------------------------- |
| **Runtime**    | Java 21                                  |
| **Framework**  | Spring Boot 3.4 (Web, Validation)        |
| **ORM**        | MyBatis 3.0                              |
| **Database**   | H2 (embedded, file-based)                |
| **Security**   | AES-256-GCM credential encryption        |
| **Build**      | Apache Maven (multi-module reactor)      |
| **Frontend**   | Vue 3 (Composition API) + TypeScript 5.8 |
| **State**      | Vuex 4                                   |
| **Routing**    | Vue Router 4                             |
| **Bundler**    | Vite 4                                   |
| **CSS**        | Tailwind CSS 3.4 + SCSS                  |
| **HTTP**       | Axios                                    |
| **Testing**    | JUnit 5 + MyBatis Test                   |
| **Formatting** | Prettier                                 |

## Architecture

```mermaid
flowchart LR
    Browser["Browser"]

    subgraph Frontend["Vue 3 Frontend :8081"]
      Vite["Vite Dev Server"]
      VueApp["Vue 3 SPA"]
      Store["Vuex Store"]
      Router["Vue Router"]
    end

    subgraph Backend["Spring Boot :8080"]
      API["REST Controllers"]
      Services["Service Layer"]
      MyBatis["MyBatis Mappers"]
      H2DB["H2 Embedded DB"]
    end

    subgraph External["External"]
      SteamAPI["Steam Web API"]
    end

    Browser --> Vite
    Vite --> VueApp
    VueApp --> Router
    VueApp --> Store
    VueApp -- "Axios / JSON" --> API
    API --> Services
    Services --> MyBatis
    MyBatis --> H2DB
    Services -- "HTTP" --> SteamAPI
```

## Screenshots

<p align="center">
  <img src="docs/image/screenshot-1.png" alt="Dashboard" width="32%">
  <img src="docs/image/screenshot-2.png" alt="Credential Management" width="32%">
  <img src="docs/image/screenshot-3.png" alt="Game Library" width="32%">
</p>

## Deployment

### Prerequisites

- **Java 21** on `PATH`
- **Node.js 18+** and **npm**
- **Maven Wrapper** is included — no global Maven install needed

### 1. Clone the repo

```bash
git clone https://github.com/Volleier/SteamGame.git
cd SteamGame
```

### 2. Build & launch

```bat
Build.bat
```

This single script handles everything:

1. Builds the backend with Maven (skipping tests)
2. Stops any previously running instance
3. Starts the Spring Boot backend on **port 8080**
4. Auto-installs frontend dependencies if `node_modules/` is missing
5. Starts the Vite dev server on **port 8081** (proxies `/api` → `localhost:8080`)

### 3. Configure credentials

Open `http://localhost:8081` in your browser, navigate to **Credential Config**, and enter your Steam Web API key and Steam ID. Credentials are encrypted with AES-256-GCM and stored in `auth.yaml`.

## Repository Layout

```
SteamGame/
├── steam-common/         # Shared DTOs, error handling, utilities
├── steam-login/          # Credential encryption, validation, scheduling
├── steam-api/            # Steam Web API client, game sync, REST controllers
├── steam-admin/          # Admin management endpoints
├── steam-launcher/       # Sole executable entry point (packs all modules)
├── vue/                  # Vue 3 + TypeScript + SCSS frontend
│   ├── src/api/          # Axios API layer
│   ├── src/components/   # Reusable UI components
│   ├── src/composables/  # Composition API hooks
│   ├── src/features/     # Feature modules (dashboard, games)
│   ├── src/router/       # Vue Router configuration
│   ├── src/store/        # Vuex store modules
│   └── src/views/        # Page-level views
├── docs/                 # API documentation
├── auth.yaml             # Encrypted credentials (git-ignored)
└── Build.bat             # One-click build & launch script
```

## Requirements

- **Java 21** — required by the backend
- **Node.js 18+** — for the Vue frontend
- **Windows 10/11** — the bundled scripts use Windows paths; adapt for Linux/macOS
- A **Steam Web API key** ([get one here](https://steamcommunity.com/dev/apikey))
