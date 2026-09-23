# Scooty Contact

Secure owner portal for the Honda Activa contact page. The original static contact page is preserved at `/index.html`; the Spring Boot app adds local account registration/login, Google OAuth2, protected sessions, and a responsive dashboard.

## Requirements

- Java 17+
- Maven 3.9+
- MySQL 8+
- Optional: Google Cloud OAuth client for Google sign-in

## Run locally

1. Create a database (the JDBC URL can also create it automatically):

   ```sql
   CREATE DATABASE scooty_contact;
   ```

2. Copy `.env.example` to `.env` or export the variables in your shell. Set `DB_USERNAME` and `DB_PASSWORD` to a MySQL account. Do not commit `.env`.
3. For Google sign-in, create a Web OAuth client in Google Cloud Console and add `http://localhost:8080/login/oauth2/code/google` as an authorized redirect URI. Set `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`.
4. Start the app:

   ```bash
   mvn spring-boot:run
   ```

Open **http://localhost:8080/login.html**. Registration is at `/register.html`, the protected dashboard is `/dashboard.html`, and the public scooter contact page remains at `/index.html`.

## Configuration

`src/main/resources/application.yml` reads database and OAuth credentials from environment variables:

| Variable | Purpose |
| --- | --- |
| `DB_URL` | MySQL JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | Database credentials |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | Google OAuth2 Web client |
| `SESSION_COOKIE_SECURE` | Set `true` when served over HTTPS |

`schema.sql` creates the `users` table. Hibernate validates the schema rather than changing it. Passwords are BCrypt-hashed (strength 12), credentials use server-side sessions with session fixation protection, CSRF protection, HttpOnly cookies, and a 30-minute session timeout.

## Project layout

```text
src/main/java/com/scootycontact/  Spring Boot, security, auth and user code
src/main/resources/application.yml Configuration
src/main/resources/schema.sql      MySQL schema
src/main/resources/static/          Packaged frontend assets
index.html/style.css                Original static contact page source
```
