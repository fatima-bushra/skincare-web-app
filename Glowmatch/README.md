# GlowMatch — Skincare Web App

A full-stack skincare recommendation app with a K-beauty aesthetic.

---

## Project Structure

```
glowmatch/
├── glowmatch-frontend/         # Pure HTML/CSS/JS frontend
│   ├── index.html              # Landing page
│   ├── quiz.html               # 8-step quiz
│   └── results.html            # Personalized results
│
└── glowmatch-backend/          # Spring Boot REST API
    ├── pom.xml
    └── src/main/java/com/glowmatch/
        ├── GlowMatchApplication.java
        ├── controller/
        │   └── QuizController.java
        ├── model/
        │   ├── QuizQuestion.java
        │   ├── QuizOption.java
        │   ├── Product.java
        │   ├── QuizSubmission.java
        │   └── QuizResult.java
        └── service/
            └── QuizService.java
```

---

## Backend Setup (Spring Boot)

### Requirements
- Java 17+
- Maven 3.8+

### Run

```bash
cd glowmatch-backend
mvn spring-boot:run
```

The API will start at `http://localhost:8080`.

### API Endpoints

| Method | Endpoint          | Description                      |
|--------|-------------------|----------------------------------|
| GET    | /api/health       | Health check                     |
| GET    | /api/questions    | Get all 8 quiz questions         |
| POST   | /api/submit       | Submit answers, get results      |

### POST /api/submit — Request Body

```json
{
  "answers": {
    "1": "oily",
    "2": "acne",
    "3": "korean",
    "4": "budget",
    "5": "minimal",
    "6": "humid",
    "7": "twenties",
    "8": "fragrance_free"
  }
}
```

### POST /api/submit — Response

```json
{
  "profileTags": ["Oily Skin", "Acne-Prone", "Korean Brands", "Budget-Friendly"],
  "products": [
    {
      "id": "p3",
      "brand": "Some By Mi",
      "name": "AHA·BHA·PHA 30 Days Miracle Toner",
      "category": "Toner",
      "matchReason": "Gently exfoliates and clears breakouts...",
      "imageUrl": "...",
      "amazonUrl": "https://amazon.com",
      "tags": ["KOREAN", "BUDGET"],
      "price": 18.0
    }
  ]
}
```

---

## Database Integration (Your Part)

The app currently uses in-memory data. To connect a database:

1. Add `spring-boot-starter-data-jpa` and your DB driver to `pom.xml`
2. Configure `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/glowmatch
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```
3. Add `@Entity` + `@Id` annotations to `Product.java` and `QuizQuestion.java`
4. Create `ProductRepository` and `QuestionRepository` extending `JpaRepository`
5. Inject repositories into `QuizService` and replace the in-memory lists

---

## Frontend Setup

No build tools needed — open directly in a browser.

### With backend running:
```bash
# Open index.html in your browser
open glowmatch-frontend/index.html
```

Or serve with a simple static server:
```bash
cd glowmatch-frontend
npx serve .
# Visit http://localhost:3000
```

### Offline / Without backend:
The frontend has full fallback data built in. The quiz and results pages will work completely even if the Spring Boot server is not running.

---

## Color Palette

| Token      | Value     | Usage              |
|------------|-----------|--------------------|
| Background | `#FDF8F4` | Page background    |
| Primary    | `#E8A598` | Dusty rose         |
| Secondary  | `#A8BBA2` | Soft sage green    |
| Accent     | `#C4614A` | Deep terracotta    |
| Text       | `#2E2622` | Warm charcoal      |
| Muted      | `#8C7B74` | Secondary text     |

---

## Fonts

- **Headings**: Cormorant Garamond (italic, 300–500)
- **Body & UI**: DM Sans (400–600)

Loaded from Google Fonts — no install needed.
