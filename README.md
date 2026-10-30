# SmartServe — Smart Canteen Token Management System

> **A Modern, Payment-First Digital Food Ordering & Kitchen Token Lifecycle Application**  
> Developed with **JavaFX 21**, **JDBC**, **MySQL 8.x**, and **Background Multithreading**, accompanied by a responsive client-side **Web Application Demo**.

---

## 1. Project Overview

In traditional college and university canteens, manual ordering processes create severe bottlenecks:
1. **Physical Counter Queues**: Students lose valuable break time waiting in line.
2. **Food Wastage & Uncollected Orders**: In unpaid token setups, users order remotely and abandon food, causing heavy losses.
3. **Kitchen Miscommunication**: Paper-based tokens get lost, causing delays and order mix-ups.
4. **Lack of Status Visibility**: Students crowd counters without knowing their preparation progress.

**SmartServe** eliminates these bottlenecks through a **Payment-First Architecture**:
- A token is issued **strictly after payment authorization** (UPI, Card, or Campus Wallet).
- An atomic, thread-safe sequential token generator ensures zero duplicate token numbers.
- Background multithreaded workers advance order preparation (`PLACED` → `PREPARING` → `READY` → `COMPLETED`) without freezing the JavaFX desktop UI.
- Kitchen managers monitor real-time sales and stage progression via an interactive Admin Dashboard.
- A standalone, zero-dependency browser-based web version (`src/main/resources/web/index.html`) is provided for web demonstration.

---

## 2. Complete Project Directory Structure

```
Smart_canteen/
│
├── .gitignore                          # Git ignore rules (ignores target/, IDE files, OS caches)
├── pom.xml                             # Maven POM descriptor (Java 17+, JavaFX 21, MySQL Connector/J)
├── PROJECT_REVIEW.md                   # Comprehensive project architecture and feature documentation
├── README.md                           # Master project and team distribution guide
│
├── build_and_sync.ps1                  # Compiles Java sources and syncs resources to target/classes
├── run_javafx.bat                      # Windows batch launcher (runs run_javafx.ps1)
├── run_javafx.ps1                      # Windows PowerShell launcher for JavaFX application
├── test_registration.ps1               # Automated test runner for user registration flows
│
├── sql/                                # ==================== AUTHORITATIVE SQL SCRIPT ===========
│   └── smart_canteen.sql               # Single authoritative MySQL DDL & DML schema, tables & seed
│
└── src/
    ├── main/
    │   ├── java/com/smartcanteen/      # ==================== ONLY FRONTEND JAVA PACKAGE =========
    │   │   ├── Main.java               # Application entrypoint (starts thread pool & JavaFX UI)
    │   │   │
    │   │   ├── frontend/               # --- 1. FRONTEND PACKAGE (JavaFX UI & Controllers) ---
    │   │   │   ├── AdminDashboardController.java    # Admin KPI dashboard & analytics controller
    │   │   │   ├── CartController.java              # Shopping cart operations & item breakdown
    │   │   │   ├── LiveOrdersController.java        # Kitchen live token display board
    │   │   │   ├── LoginController.java             # User authentication controller
    │   │   │   ├── ManageMenuController.java        # Menu catalog CRUD & item availability toggle
    │   │   │   ├── MenuController.java              # Categorized food catalog browser & search
    │   │   │   ├── OrderConfirmationController.java # Order receipt & token confirmation screen
    │   │   │   ├── PaymentController.java           # Payment gateway simulation controller
    │   │   │   ├── RegisterController.java          # Student registration controller
    │   │   │   ├── StudentDashboardController.java  # Student landing portal & navigation
    │   │   │   ├── TokenStatusController.java       # Visual order preparation stepper
    │   │   │   └── util/                            # JavaFX Presentation Utilities
    │   │   │       ├── AlertUtil.java               # JavaFX modal alert dialog helper
    │   │   │       ├── FoodImageResolver.java       # JavaFX Image loading & in-memory cache
    │   │   │       └── SceneNavigator.java          # JavaFX Stage and FXML scene switcher
    │   │   │
    │   │   ├── backend/                # --- 2. BACKEND PACKAGE (Business Logic Layer) ---
    │   │   │   └── service/            # Core Domain Services & Business Operations
    │   │   │       ├── AdminService.java            # KPI calculation and kitchen orders service
    │   │   │       ├── AuthService.java             # Login, session, and registration logic
    │   │   │       ├── CartService.java             # In-memory cart manager & price calculations
    │   │   │       ├── MenuService.java             # Food catalog querying and category filtering
    │   │   │       ├── OrderService.java            # Order creation & JDBC transaction coordinator
    │   │   │       ├── PaymentService.java          # Multi-mode payment validation & demo banking
    │   │   │       └── TokenService.java            # Token lifecycle management & database sync
    │   │   │
    │   │   ├── database/               # --- 3. DATABASE PACKAGE (Data Access & Persistence) ---
    │   │   │   ├── connection/         # Database Connection Management
    │   │   │   │   └── DBConnection.java            # MySQL JDBC Connection provider (reads db.properties)
    │   │   │   ├── dao/                # DAO Interfaces
    │   │   │   │   ├── IMenuItemDAO.java            # Food item catalog DAO interface
    │   │   │   │   ├── IOrderDAO.java               # Order entity DAO interface
    │   │   │   │   ├── IOrderItemDAO.java           # Order line-item DAO interface
    │   │   │   │   ├── IPaymentDAO.java             # Payment transaction DAO interface
    │   │   │   │   ├── ITokenDAO.java               # Sequential token DAO interface
    │   │   │   │   └── IUserDAO.java                # User authentication DAO interface
    │   │   │   └── impl/               # DAO Implementations (JDBC SQL operations)
    │   │   │       ├── MenuItemDAO.java             # JDBC queries for menu_items table
    │   │   │       ├── OrderDAO.java                # JDBC queries for orders table
    │   │   │       ├── OrderItemDAO.java            # JDBC queries for order_items table
    │   │   │       ├── PaymentDAO.java              # JDBC queries for payments table
    │   │   │       ├── TokenDAO.java                # JDBC queries for tokens table
    │   │   │       └── UserDAO.java                 # JDBC queries for users table
    │   │   │
    │   │   ├── threading/              # --- 4. THREADING PACKAGE (Concurrency & Workers) ---
    │   │   │   ├── AppThreadPool.java               # Centralized ExecutorService & thread lifecycle
    │   │   │   ├── OrderProcessor.java              # Runnable background worker simulating kitchen stages
    │   │   │   ├── OrderQueueManager.java           # Thread-safe BlockingQueue for kitchen pipeline
    │   │   │   └── TokenDisplayUpdater.java         # Runnable background poller for live status refresh
    │   │   │
    │   │   ├── model/                  # --- 5. MODEL PACKAGE (Domain Entities & Enums) ---
    │   │   │   ├── User.java                        # Base abstract user entity
    │   │   │   ├── Student.java                     # Inherits User (adds roll number)
    │   │   │   ├── Admin.java                       # Inherits User (staff role)
    │   │   │   ├── MenuItem.java                    # Food item catalog entity
    │   │   │   ├── CartItem.java                    # In-memory cart line-item model
    │   │   │   ├── Order.java                       # Order header entity
    │   │   │   ├── OrderItem.java                   # Order line-item entity
    │   │   │   ├── Payment.java                     # Payment transaction entity
    │   │   │   ├── Token.java                       # Sequential token entity
    │   │   │   ├── AdminOrderDTO.java               # Flattened DTO for kitchen live displays
    │   │   │   ├── Role.java                        # Enum: STUDENT, ADMIN
    │   │   │   ├── OrderStatus.java                 # Enum: PLACED, PREPARING, READY, COMPLETED, CANCELLED
    │   │   │   ├── PaymentMethod.java               # Enum: UPI, CARD, WALLET
    │   │   │   ├── PaymentStatus.java               # Enum: SUCCESS, FAILED, CANCELLED
    │   │   │   └── TokenStatus.java                 # Enum: ACTIVE, COLLECTED, EXPIRED
    │   │   │
    │   │   └── common/                 # --- 6. COMMON PACKAGE (Exceptions & Cross-Cutting) ---
    │   │       ├── exception/          # Custom Domain Exceptions
    │   │       │   ├── AuthenticationException.java # Invalid login or unauthorized session access
    │   │       │   ├── DatabaseException.java       # JDBC errors, SQL failures & communication timeouts
    │   │       │   ├── OrderException.java          # Cart empty, out-of-stock or invalid orders
    │   │       │   ├── PaymentException.java        # Validation, balance or gateway failures
    │   │       │   └── ValidationException.java     # Registration & form field validation errors
    │   │       └── util/               # Cross-Cutting Utilities
    │   │           ├── MenuDataSeeder.java          # Auto-seeds 85+ Indian dishes into MySQL on startup
    │   │           ├── PasswordHasher.java          # Cryptographic SHA-256 password hashing
    │   │           ├── SessionManager.java          # Current logged-in user thread-safe session
    │   │           ├── TokenGenerator.java          # Thread-safe atomic sequential token issuer
    │   │           └── Validator.java               # Input validator (username, roll no, UPI, card)
    │   │
    │   └── resources/                  # ==================== MAVEN RESOURCES ====================
    │       ├── db.properties                   # MySQL connection string, username & password
    │       ├── db.properties.example           # Sanitized credentials template for repository/Git
    │       ├── css/
    │       │   └── style.css                   # Master JavaFX stylesheet (-fx- CSS rules)
    │       ├── fxml/                           # 11 FXML declarative UI screen definitions
    │       │   ├── admin_dashboard.fxml
    │       │   ├── cart.fxml
    │       │   ├── live_orders.fxml
    │       │   ├── login.fxml
    │       │   ├── manage_menu.fxml
    │       │   ├── menu.fxml
    │       │   ├── order_confirmation.fxml
    │       │   ├── payment.fxml
    │       │   ├── register.fxml
    │       │   ├── student_dashboard.fxml
    │       │   └── token_status.fxml
    │       ├── images/
    │       │   └── food/                       # 107 Food image assets loaded by JavaFX FoodImageResolver
    │       └── web/                            # ==================== ONLY AUTHORITATIVE WEB ASSETS ======
    │           ├── index.html                  # Standalone single-page web UI layout
    │           ├── style.css                   # Web CSS design system (tokens, responsive grid)
    │           ├── app.js                      # Web client logic, catalog data & local state controller
    │           └── images/food/                # Web food images
    │
    └── test/java/com/smartcanteen/     # ==================== TEST SUITE ========================
        ├── PaymentFlowVerification.java        # Comprehensive 9-test payment & transaction test suite
        └── RegistrationFlowTest.java           # Mock-based user registration verification tests
```

---

## 3. Team Module Distribution

| Team Member | Module Name | Primary Directories & Packages | Key Responsibilities |
| :--- | :--- | :--- | :--- |
| **Member 1** | **Frontend Development** | `src/main/java/com/smartcanteen/frontend/`<br>`src/main/resources/fxml/`<br>`src/main/resources/css/`<br>`src/main/resources/images/`<br>`src/main/resources/web/` | • JavaFX UI layouts & event handlers (`com.smartcanteen.frontend`)<br>• JavaFX stylesheets & visual styling<br>• FXML scene design & controller bindings<br>• Web frontend application under `src/main/resources/web/`<br>• UI helper utilities (`frontend.util`) |
| **Member 2** | **Backend Development** | `src/main/java/com/smartcanteen/backend/service/` | • Core business logic implementation<br>• Multi-mode payment simulation (UPI/Card/Wallet)<br>• Order calculation & atomic checkout coordination<br>• Authentication & registration rules<br>• Dynamic cart calculation |
| **Member 3** | **Database Management** | `src/main/java/com/smartcanteen/database/dao/`<br>`src/main/java/com/smartcanteen/database/impl/`<br>`src/main/java/com/smartcanteen/database/connection/`<br>`src/main/resources/db.properties`<br>`sql/smart_canteen.sql` | • MySQL database schema design & maintenance<br>• JDBC connection management & connection pooling (`DBConnection`)<br>• DAO interfaces and implementation isolation<br>• Transaction management (`setAutoCommit(false)`, rollback)<br>• SQL data seeding and database migrations |
| **Member 4** | **Multithreading & Concurrency** | `src/main/java/com/smartcanteen/threading/` | • Background thread execution (`ExecutorService`, `ScheduledExecutorService`)<br>• Asynchronous kitchen simulation (`OrderProcessor`)<br>• Thread-safe live token polling (`TokenDisplayUpdater`)<br>• Thread-safe blocking queue (`OrderQueueManager`)<br>• Centralized thread pool management (`AppThreadPool`) |
| **Member 5** | **Models & OOP Architecture** | `src/main/java/com/smartcanteen/model/` | • OOP hierarchy (`User` $\rightarrow$ `Student`, `Admin`)<br>• Encapsulation of entities (`MenuItem`, `Order`, `Token`)<br>• DTO definitions (`AdminOrderDTO`)<br>• State and type safety enums (`OrderStatus`, `PaymentMethod`, etc.) |
| **Member 6 / QA** | **Common Utilities & Testing** | `src/main/java/com/smartcanteen/common/`<br>`src/test/java/` | • Custom domain exceptions (`common.exception`)<br>• Shared security and hashing (`PasswordHasher`, `Validator`)<br>• Token sequence generation (`TokenGenerator`)<br>• Automated test suites & QA verification |

---

## 4. Database Setup & Credential Protection

1. **Install MySQL Server 8.x**: Ensure MySQL is running on `localhost:3306`.
2. **Execute Authoritative Schema**:
   ```bash
   mysql -u root -p < sql/smart_canteen.sql
   ```
3. **Configure Connection & Protect Secrets**:
   - For version control, a template [`src/main/resources/db.properties.example`](file:///src/main/resources/db.properties.example) is provided.
   - For your active local environment, configure [`src/main/resources/db.properties`](file:///src/main/resources/db.properties):
     ```properties
     db.url=jdbc:mysql://localhost:3306/smart_canteen
     db.user=root
     db.password=root
     ```
   - Never commit sensitive production database passwords to public repositories.

---

## 5. How to Run the Project

### Method 1: Using Maven (Cross-Platform)
```bash
# Compile
mvn clean compile

# Launch JavaFX Application
mvn javafx:run
```

### Method 2: Using Windows PowerShell Script
```powershell
.\run_javafx.ps1
```

### Method 3: Using Windows Batch Script (Double-Click)
- Double-click [`run_javafx.bat`](file:///run_javafx.bat) in Windows Explorer to build and launch immediately.

### Method 4: Opening the Web Client Demo
- Open [`src/main/resources/web/index.html`](file:///src/main/resources/web/index.html) in any browser (Chrome, Edge, Firefox).

---

## 6. Running the Automated Test Suite

### Registration Flow Tests
```powershell
.\test_registration.ps1
```

### Transaction & Payment Flow Tests
```powershell
& "C:\Program Files\Java\jdk-26.0.2\bin\java.exe" -cp "target/classes;target/test-classes;C:\Users\NEHA KUMARI\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar" com.smartcanteen.PaymentFlowVerification
```
