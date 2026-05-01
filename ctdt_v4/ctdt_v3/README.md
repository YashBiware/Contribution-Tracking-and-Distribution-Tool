# CTDT — Contribution Tracking & Distribution Tool
## Quick Start

### 1. Database
```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

### 2. Backend — edit application.properties (set your MySQL password)
```bash
cd backend
mvn spring-boot:run
# → http://0.0.0.0:8080
```

### 3. Frontend — edit .env (set VITE_API_BASE for LAN)
```bash
cd frontend
npm install
npm run dev
# → http://localhost:3000
```

## Formula
weighted_value = content_size × weight_factor   (per retained log)
percentage     = (contributor_total / project_total) × 100
BigDecimal only — no float/double anywhere.

## Weight Table
.c/.cpp=1.5  .py=1.2  .java/.js/.ts=1.0  .html/.css=0.8  .txt=0.5  default=1.0

## API Endpoints
POST /api/projects
GET  /api/projects
POST /api/projects/{id}/contributors
GET  /api/projects/{id}/contributors
POST /api/logs
POST /api/projects/{id}/recalculate
GET  /api/projects/{id}/results
GET  /api/projects/{id}/recalculation-history
GET  /api/projects/{id}/analytics/distribution
GET  /api/projects/{id}/analytics/performance
GET  /api/projects/{id}/analytics/summary
