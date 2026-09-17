# Running the ERP backend in Eclipse

## Requirements
- Eclipse IDE for Enterprise Java and Web Developers (or Eclipse IDE with Maven support)
- JDK 21
- Maven support (m2e)
- PostgreSQL running locally

## Import
1. Clone the repository.
2. In Eclipse choose **File → Import → Maven → Existing Maven Projects**.
3. Select the `backend` folder.
4. Finish the import and allow Maven to download dependencies.
5. In **Window → Preferences → Java → Installed JREs**, select a JDK 21 installation.
6. Right-click the project → **Maven → Update Project**.

The backend uses Java 21 and Spring Boot 3.5.5. Eclipse metadata is included for Java 21, while Maven remains the source of truth for dependencies.

## Database
Create a PostgreSQL database named `erp_management`, or change the connection using environment variables:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

## JWT
Set `JWT_SECRET` before starting the application. The secret is intentionally not hard-coded in the project.

For local development, an optional first-admin bootstrap can be enabled with:
- `ERP_BOOTSTRAP_ADMIN_ENABLED=true`
- `ERP_BOOTSTRAP_ADMIN_USERNAME=...`
- `ERP_BOOTSTRAP_ADMIN_PASSWORD=...` (minimum 12 characters)

Keep these values out of source control.

## Run
In Eclipse:
**Run As → Spring Boot App** on the main `@SpringBootApplication` class.

The API listens on `http://localhost:8080` by default.

## Frontend
The `frontend` directory is a static web client. Serve it with a local static web server rather than opening `index.html` with `file://` if the browser blocks API requests due to CORS.
