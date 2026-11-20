# DynoCloud

**DynoCloud** is a self-hostable, privacy-focused object storage backend built using **Java and Spring Boot**.

It provides secure user authentication, file management, object storage integration, and encrypted file storage.

## Features

- User registration and login.
- JWT authentication using RSA (RS256).
- BCrypt password hashing.
- PostgreSQL database integration.
- File upload, download, and deletion.
- Garage and AWS S3 storage support.
- Storage quota management.
- Presigned URLs for secure file downloads.
- AES-256-GCM envelope encryption.
- Self-hosted key management using OpenBao (in progress).
- Recovery-phrase-based password recovery (in progress).

## Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Security:** Spring Security, JWT
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Storage:** Garage, Amazon S3 (can be switchable but Amazon s3 business logic not implemented)
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