# Prography 11th Backend Assignment

프로그라피 11기 백엔드 과제 (출결 관리 시스템) 저장소입니다.

## 🛠️ 개발 환경 (Tech Stack)

* **Language**: Java 21
* **Framework**: Spring Boot 3.5.11
* **Database**: H2 (In-memory Mode)
* **ORM**: JPA (Hibernate), QueryDSL
* **Build Tool**: Gradle

## 🚀 실행 방법 (Getting Started)

이 프로젝트는 H2 데이터베이스를 **In-Memory 모드**로 사용합니다.
별도의 데이터베이스 설치나 설정 없이, 프로젝트를 빌드하고 실행하기만 하면 됩니다.

### 1. 사전 요구사항 (Prerequisites)

* **Java 17** 이상이 설치되어 있어야 합니다.

### 2. 프로젝트 클론 및 실행 (Build & Run)

터미널(또는 명령 프롬프트)에서 아래 명령어를 순서대로 입력해주세요.

**1. 프로젝트 클론**

```bash
git clone https://github.com/hcg0127/prography-11th-backend.git
cd prography-11th-backend
```

**2. 빌드 및 실행 (Mac/Linux)**

```bash
./gradlew clean build
java -jar build/libs/api-0.0.1-SNAPSHOT.jar
# (또는 ./gradlew bootRun)
```

**3. 빌드 및 실행 (Windows)**

```bash
gradlew.bat clean build
java -jar build/libs/api-0.0.1-SNAPSHOT.jar
# (또는 gradlew.bat bootRun)
```

---

## 💾 데이터베이스 접속 (H2 Console)

서버가 정상적으로 실행되면, 브라우저를 통해 내장 데이터베이스(H2)에 접속하여 데이터를 확인할 수 있습니다.

* **접속 URL**: `http://localhost:8080/h2-console`
* **JDBC URL**: `jdbc:h2:mem:testdb`
* **User**: `sa`
* **Password**: (공란/입력하지 않음)

> **⚠️ 주의사항**
> * 접속이 안 될 경우, `application.yml`의 `spring.datasource.url` 설정이 `jdbc:h2:mem:testdb`로 되어 있는지 확인해주세요.
> * `Connect` 버튼을 누르면 접속됩니다.

---

## 🔐 관리자 계정 정보 (Admin Credentials)

서버 시작 시, 테스트를 위한 초기 데이터(Seed Data)가 자동으로 로드됩니다.
아래 계정으로 로그인하여 관리자 기능을 테스트할 수 있습니다.

| Role      | ID      | Password    | 비고     |
|:----------|:--------|:------------|:-------|
| **Admin** | `admin` | `admin1234` | 관리자 권한 |

> **참고**: 비밀번호는 `BCrypt`로 암호화되어 저장되므로, DB에서 직접 수정 시 로그인이 되지 않을 수 있습니다.

---

## 🏗️ 시스템 아키텍처 (System Architecture)

* **[System Architecture](./docs/SYSTEM_ARCHITECTURE.md)**
    * 시스템 아키텍처 설계 과정을 정리했습니다.

---

## 🗂️ ERD (Entity Relationship Diagram)

* **[Entity-Relationship Diagram](./docs/ERD.md)**
    * 간단한 Entity와 Relationship 설명을 작성했습니다.

---

## 📝 문서 (Documentation)

과제 진행 과정에서의 고민과 기술적 의사결정, 상세 기능 명세는 `docs/` 디렉토리에 기록하였습니다.

* **[AI 활용 사례](./docs/AI_USE_CASE.md)**
    * 과제 진행 중 AI를 어떻게 활용하여 생산성을 높였는지 기술했습니다.
* **[기능 구현 목록](./docs/FEATURE_LIST.md)**
    * 전체 요구사항 대비 구현 현황을 확인할 수 있습니다.
* **[Swagger API 명세서](http://localhost:8080/docs)**
    * 서버 실행 시 실제 작동하는 API의 상세 스펙과 테스트 환경을 제공합니다.

---

### 📮 Contact

* **GitHub**: [https://github.com/hcg0127](https://github.com/hcg0127)