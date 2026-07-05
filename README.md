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

*   **Fully Reactive Architecture:** Engineered with an asynchronous, non-blocking network-to-database pipeline leveraging **Spring WebFlux** and **Spring Data R2DBC**[cite: 1]. This design achieves massive I/O throughput, handling thousands of concurrent connections and resource-intensive asset uploads/downloads while maintaining ultra-low memory overhead compared to traditional blocking Thread-per-Request models[cite: 1].
*   **Flexible GraphQL API:** Driven by **Spring for GraphQL** over a single unified endpoint[cite: 1]. It provides optimal client-side data fetching flexibility, eliminating over-fetching/under-fetching issues and streamlining API evolution without aggressive endpoint versioning[cite: 1].
*   **Decoupled Background Processing:** Offloads long-running, non-blocking infrastructure workflows (such as event-driven email transmissions and transaction verifications) to **RabbitMQ** to guarantee resilient asynchronous system coordination[cite: 1].
*   **Secure Cloud-Native Infrastructure:** Integrated with global proxies, cloud storage layers, and secure external transaction gateways to handle data integrity and piracy protection out of the box[cite: 1].

---

## 🏗️ System Architecture

The service adopts a clean, layered architectural approach wrapped into an event-driven, reactive framework[cite: 1]. All incoming traffic is routed via a secure **Cloudflare Web Application Firewall (WAF)** and CDN proxy, protecting internal infrastructure from direct attacks and optimizing data flow[cite: 1].

<!-- Placeholder for Rysunek 10.3: Component Diagram – Server Application -->
> 🗺️ *[Architecture Diagram Placeholder: Insert `Rysunek 10.3` here to visualize Spring Boot component interactions]*[cite: 1]

### Module & Packages Organization
*   `controller` / `presentation`: Houses GraphQL query/mutation mappings (`@SchemaMapping`), processing typed incoming operations and global exception handling[cite: 1].
*   `service` / `business logic`: Implements central marketplace business rules, utilizing **MapStruct** for lightning-fast DTO/Entity transformations[cite: 1].
*   `repository` / `data`: Manages asynchronous relational persistence using non-blocking R2DBC configurations[cite: 1].
*   `rabbit`: Handles async consumer queues (`AMQP`) for distributed background job execution[cite: 1].
*   `payment` / `webhook`: Manages e-commerce ledger states, handling transactional webhook payloads from the Stripe API[cite: 1].
*   `storage`: Connects directly to external object store environments to generate temporary, cryptographically safe resource links[cite: 1].

---

## 💾 Persistence & Asset Distribution Model

The persistence layer separates business metadata from physical objects to protect intellectual property and minimize server storage overhead[cite: 1].

<!-- Placeholder for Rysunek 13.1: Entity Relationship Diagram (ERD) -->
> 📊 *[Database Schema Placeholder: Insert `Rysunek 13.1` here to display the complete ERD structure]*[cite: 1]

### Advanced PostgreSQL Schema
The database schema utilizes strict relational normalization and taps into advanced PostgreSQL engines[cite: 1]:
*   **Inheritance Optimization:** Implements a base `users` table linked with clean `clients` and `authors` extending tables to minimize redundant `NULL` footprints[cite: 1].
*   **Brutalist Query Performance:** Uses native PostgreSQL array types for roles and **GIN (Generalized Inverted Indexes)** for high-speed permission filtering[cite: 1].
*   **Social & External Integrations:** Stores dynamic metadata structures using highly-efficient binary **JSONB** structures[cite: 1].
*   **Transactional Price Freezing:** The `purchase_items` ledger captures a static `snapshot_price` at the moment of payment, protecting accounting data against subsequent creator pricing adjustments[cite: 1].

### Digital Piracy Protection Layer
Asset file distribution is split into two distinct **Supabase Storage** (S3-compatible) buckets[cite: 1]:
1.  `previews` (Public): Stores user avatars and promotional covers with wide-open public read paths for optimized cache performance[cite: 1].
2.  `vault` (Private): Secures the core compressed source files (ZIP/RAR)[cite: 1]. Public paths are entirely blocked by **Row Level Security (RLS)**[cite: 1]. Access requires authenticating a token, verifying the purchase ledger inside Spring Security, and compiling a time-locked, cryptographically **Signed URL**[cite: 1].

---

## 🔒 Security & Data Governance

*   **Stateless Token Rotation:** Architecture operates completely stateless via an asymmetric JWT mechanism[cite: 1]. Client requests supply short-lived **Access Tokens (15 min)**[cite: 1]. Token generation relies on long-lived database-encrypted **Refresh Tokens (30 days)**[cite: 1]. Endpoint rules are guarded declaratively via granular `@PreAuthorize` method controls[cite: 1].
*   **Infrastructure-Level RLS:** Tables explicitly implement an `allow_backend_access` policy[cite: 1]. It isolates access to the core `postgres` backend system role, preventing raw public interface leaks[cite: 1].
*   **Data Leak & Log Masking:** Integrates a secure custom pipeline with Promtail, Loki, and Grafana (PLG)[cite: 1]. A native `RegexMaskHelper` interceptor intercepts out-of-the-box logs to mask sensitive information (Access Tokens, Refresh Tokens, and user passwords) using strict regex algorithms before outputting standard JSON[cite: 1].
*   **Input Sanitization:** Intercepts GraphQL parameters via structural `Jakarta Bean Validation` annotations (`@Email`, `@NotBlank`)[cite: 1]. It prevents injection anomalies and blocks compromised parameter processing ahead of the business layer[cite: 1].

---

## 📬 Cloud-Native Communication (Resend API)

Moving from dev to staging environments, the engine dropped traditional SMTP mail routing due to standard PaaS cloud provider egress limitations (blocked ports 25, 465, 587) and the blocking synchronous signature of classic JavaMailSender utilities[cite: 1]. 

The system utilizes an asynchronous HTTP gateway connected to the **Resend API**[cite: 1]. The server creates fluid HTML templates via **Thymeleaf**, offloads payloads through RabbitMQ, and triggers high-speed transaction confirmations without impacting thread scheduling[cite: 1].

---

## 🧪 Testing & Quality Assurance

The project adopts a lean testing methodology, prioritizing rapid-execution unit tests over heavy integration containers to ensure agility during schema scaling[cite: 1].

*   **Frameworks:** Built on **JUnit 5**, **Mockito**, and **AssertJ** fluent validation engines[cite: 1].
*   **Metrics Reporting:** Monitored seamlessly via the **JaCoCo Maven Plugin**[cite: 1].

<!-- Placeholder for Rysunek 14.1: JaCoCo Coverage Report -->
> 📈 *[JaCoCo Report Placeholder: Insert `Rysunek 14.1` here to show the code coverage layout]*[cite: 1]

The business service layer maintains a **71% overall code coverage** index, which fully adheres to standard production baselines (70%–80%)[cite: 1]. Boilerplate models, abstract exceptions, and auto-generated data transfer utilities (Lombok/MapStruct) are deliberately omitted from test pipelines[cite: 1].

---

## 🛠️ Local Development Setup

The repository ships with an isolated environment manager configuration to emulate the full cloud infrastructure network offline[cite: 1].

### Prerequisites
*   Java 21 SDK
*   Maven 3.x+
*   Docker & Docker Compose

### Spinning up local infrastructure
Run the local orchestrator configuration package to stand up auxiliary database systems, log monitors, and broker queues[cite: 1]:
```bash
docker-compose up -d
