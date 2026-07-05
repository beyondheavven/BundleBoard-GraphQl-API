# BundleBoard API

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![GraphQL](https://img.shields.io/badge/GraphQL-E10098?style=for-the-badge&logo=graphql&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

High-performance, fully reactive backend API driving the **BundleBoard** marketplace—a specialized e-commerce and digital asset aggregation platform optimized for design resources (Adobe Photoshop and Adobe Illustrator templates, textures, brushes, and gradient maps).

---

## 🚀 Core Technical Features

*   **Fully Reactive Architecture:** Engineered with an asynchronous, non-blocking network-to-database pipeline leveraging **Spring WebFlux** and **Spring Data R2DBC** . This design achieves massive I/O throughput, handling thousands of concurrent connections and resource-intensive asset uploads/downloads while maintaining ultra-low memory overhead compared to traditional blocking Thread-per-Request models .
*   **Flexible GraphQL API:** Driven by **Spring for GraphQL** over a single unified endpoint. It provides optimal client-side data fetching flexibility, eliminating over-fetching/under-fetching issues and streamlining API evolution without aggressive endpoint versioning.
*   **Decoupled Background Processing:** Offloads long-running, non-blocking infrastructure workflows (such as event-driven email transmissions and transaction verifications) to **RabbitMQ** to guarantee resilient asynchronous system coordination.
*   **Secure Cloud-Native Infrastructure:** Integrated with global proxies, cloud storage layers, and secure external transaction gateways to handle data integrity and piracy protection out of the box.

---

## 🏗️ System Architecture

The service adopts a clean, layered architectural approach wrapped into an event-driven, reactive framework . All incoming traffic is routed via a secure **Cloudflare Web Application Firewall (WAF)** and CDN proxy, protecting internal infrastructure from direct attacks and optimizing data flow .

<!-- Placeholder for Rysunek 10.3: Component Diagram – Server Application -->
> 🗺️ *[Architecture Diagram Placeholder: Insert `Rysunek 10.3` here to visualize Spring Boot component interactions]* 

### Module & Packages Organization
*   `controller` / `presentation`: Houses GraphQL query/mutation mappings (`@SchemaMapping`), processing typed incoming operations and global exception handling .
*   `service` / `business logic`: Implements central marketplace business rules, utilizing **MapStruct** for lightning-fast DTO/Entity transformations .
*   `repository` / `data`: Manages asynchronous relational persistence using non-blocking R2DBC configurations .
*   `rabbit`: Handles async consumer queues (`AMQP`) for distributed background job execution .
*   `payment` / `webhook`: Manages e-commerce ledger states, handling transactional webhook payloads from the Stripe API .
*   `storage`: Connects directly to external object store environments to generate temporary, cryptographically safe resource links .

---

## 💾 Persistence & Asset Distribution Model

The persistence layer separates business metadata from physical objects to protect intellectual property and minimize server storage overhead .

<!-- Placeholder for Rysunek 13.1: Entity Relationship Diagram (ERD) -->
> 📊 *[Database Schema Placeholder: Insert `Rysunek 13.1` here to display the complete ERD structure]* 

### Advanced PostgreSQL Schema
The database schema utilizes strict relational normalization and taps into advanced PostgreSQL engines :
*   **Inheritance Optimization:** Implements a base `users` table linked with clean `clients` and `authors` extending tables to minimize redundant `NULL` footprints .
*   **Brutalist Query Performance:** Uses native PostgreSQL array types for roles and **GIN (Generalized Inverted Indexes)** for high-speed permission filtering .
*   **Social & External Integrations:** Stores dynamic metadata structures using highly-efficient binary **JSONB** structures .
*   **Transactional Price Freezing:** The `purchase_items` ledger captures a static `snapshot_price` at the moment of payment, protecting accounting data against subsequent creator pricing adjustments .

### Digital Piracy Protection Layer
Asset file distribution is split into two distinct **Supabase Storage** (S3-compatible) buckets :
1.  `previews` (Public): Stores user avatars and promotional covers with wide-open public read paths for optimized cache performance .
2.  `vault` (Private): Secures the core compressed source files (ZIP/RAR) . Public paths are entirely blocked by **Row Level Security (RLS)** . Access requires authenticating a token, verifying the purchase ledger inside Spring Security, and compiling a time-locked, cryptographically **Signed URL** .

---

## 🔒 Security & Data Governance

*   **Stateless Token Rotation:** Architecture operates completely stateless via an asymmetric JWT mechanism . Client requests supply short-lived **Access Tokens (15 min)** . Token generation relies on long-lived database-encrypted **Refresh Tokens (30 days)** . Endpoint rules are guarded declaratively via granular `@PreAuthorize` method controls .
*   **Infrastructure-Level RLS:** Tables explicitly implement an `allow_backend_access` policy . It isolates access to the core `postgres` backend system role, preventing raw public interface leaks .
*   **Data Leak & Log Masking:** Integrates a secure custom pipeline with Promtail, Loki, and Grafana (PLG) . A native `RegexMaskHelper` interceptor intercepts out-of-the-box logs to mask sensitive information (Access Tokens, Refresh Tokens, and user passwords) using strict regex algorithms before outputting standard JSON .
*   **Input Sanitization:** Intercepts GraphQL parameters via structural `Jakarta Bean Validation` annotations (`@Email`, `@NotBlank`) . It prevents injection anomalies and blocks compromised parameter processing ahead of the business layer .

---

## 📬 Cloud-Native Communication (Resend API)

Moving from dev to staging environments, the engine dropped traditional SMTP mail routing due to standard PaaS cloud provider egress limitations (blocked ports 25, 465, 587) and the blocking synchronous signature of classic JavaMailSender utilities . 

The system utilizes an asynchronous HTTP gateway connected to the **Resend API** . The server creates fluid HTML templates via **Thymeleaf**, offloads payloads through RabbitMQ, and triggers high-speed transaction confirmations without impacting thread scheduling .

---

## 🧪 Testing & Quality Assurance

The project adopts a lean testing methodology, prioritizing rapid-execution unit tests over heavy integration containers to ensure agility during schema scaling .

*   **Frameworks:** Built on **JUnit 5**, **Mockito**, and **AssertJ** fluent validation engines .
*   **Metrics Reporting:** Monitored seamlessly via the **JaCoCo Maven Plugin** .

<!-- Placeholder for Rysunek 14.1: JaCoCo Coverage Report -->
> 📈 *[JaCoCo Report Placeholder: Insert `Rysunek 14.1` here to show the code coverage layout]* 

The business service layer maintains a **71% overall code coverage** index, which fully adheres to standard production baselines (70%–80%) . Boilerplate models, abstract exceptions, and auto-generated data transfer utilities (Lombok/MapStruct) are deliberately omitted from test pipelines .

---

## 🛠️ Local Development Setup

The repository ships with an isolated environment manager configuration to emulate the full cloud infrastructure network offline .

### Prerequisites
*   Java 21 SDK
*   Maven 3.x+
*   Docker & Docker Compose

### Spinning up local infrastructure
Run the local orchestrator configuration package to stand up auxiliary database systems, log monitors, and broker queues :
```bash
docker-compose up -d
