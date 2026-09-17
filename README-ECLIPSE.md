# ERP Management — Eclipse Project Structure

The repository is organized as an Eclipse/Maven multi-module project.

```text
ERP_management/
├── .project
├── .classpath
├── pom.xml                 # Maven parent / aggregator
├── backend/
│   ├── .project            # Eclipse Java project descriptor
│   ├── .classpath          # Eclipse Java 21 + Maven classpath
│   ├── pom.xml             # Spring Boot backend
│   ├── src/
│   │   ├── main/java/      # Controllers, services, entities, repositories, security, AI
│   │   ├── main/resources/ # application configuration
│   │   └── test/java/      # Unit/integration tests
│   └── ...
├── frontend/
│   ├── index.html
│   ├── app.js
│   └── style.css
├── database/
├── docker/
└── ...
```

## Import into Eclipse

1. Clone the repository.
2. In Eclipse choose **File → Import → Maven → Existing Maven Projects**.
3. Select the repository root (`ERP_management`).
4. Eclipse detects the root Maven aggregator and the `backend` Maven module.
5. Use **Maven → Update Project** after import.
6. Configure **JDK 21** for the workspace/project.
7. Run the backend as **Run As → Spring Boot App** from the `backend` module.
8. Open `http://localhost:8080/` to use the frontend served by Spring Boot.

The existing `backend` project remains a normal Eclipse Java project with `src/main/java`, `src/main/resources`, `src/test/java`, Maven dependencies, and Java 21 configuration. The root descriptors make the complete repository importable as one Eclipse/Maven workspace project without changing application code.
