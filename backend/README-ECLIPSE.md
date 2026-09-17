# Running the complete ERP from Eclipse

The project is configured so one Spring Boot process can serve both the REST API and the static ERP frontend.

## Requirements
- Eclipse IDE with Maven support (m2e)
- JDK 21
- PostgreSQL

## Import
Use **File → Import → Maven → Existing Maven Projects** and select the `backend` folder.

## Start the complete application
1. Make sure PostgreSQL is running and the `erp_management` database exists.
2. Right-click the Spring Boot application class and choose **Run As → Spring Boot App**.
3. Use the `eclipse` profile for local development.
4. Open **http://localhost:8080/** in a browser.

Maven copies the top-level `frontend` directory into Spring Boot's static resources during `process-resources`. The frontend therefore runs from the same origin as `/api`, so no separate frontend server or CORS setup is required for the normal Eclipse workflow.

## Local Eclipse profile
The `eclipse` profile supplies local PostgreSQL defaults and a development admin bootstrap.

Default development credentials:
- Username: `admin`
- Password: `ChangeMe12345!`

These are development-only defaults. Do not use them for a shared or production deployment.

## Maven fallback
From the `backend` directory:
`mvn spring-boot:run -Dspring-boot.run.profiles=eclipse`

## Editing frontend files
Keep source files under the top-level `frontend` directory. After frontend changes, run **Maven → Update Project** and restart the application when necessary so the copied static resources are refreshed.
