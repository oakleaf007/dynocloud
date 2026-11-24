# DynoCloud

**DynoCloud** is a self-hostable, privacy-focused object storage backend built using **Java and Spring Boot**.

It provides secure user authentication, file management, object storage integration, and encrypted file storage.

## Features

- User registration and login (for JWT based auth, check AuthController and the endpoints).
- JWT authentication using RSA (RS256).
- BCrypt password hashing.
- PostgreSQL database integration.
- File upload, download, rename and deletion.
- Garage and AWS S3 storage support (self hosted Garage fully working).
- Storage quota management(Not really management but automated calculation during upload or delete.)
- Native browser download.
- AES-256-GCM envelope encryption.
- Self-hosted key management using OpenBao.
- Recovery-phrase-based password recovery (No Email based recovery available).
- Admin creation with single-time bootstrap pattern.
- Admin panel for storage overview (currently you can see only total used & total files).
- Session based authentication added (seperate endpoints, check SessionAuthController).

## Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Security:** Spring Security, JWT
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Storage:** Garage, Amazon S3 (can be switchable but Amazon s3 service logic not implemented)
- **Encryption:** AES-256-GCM
- **Key Management:** OpenBao
- **Build Tool:** Gradle

## Architecture

```text
Client
  |
  v
Spring Boot REST API
  |
  +---- Spring Security / JWT
  |
  +---- PostgreSQL (Metadata)
  |
  +---- Object Storage
          |
          +---- Garage
          |
          +---- AWS S3