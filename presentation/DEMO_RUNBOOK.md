# Capstone Project Live Demonstration Runbook (5–7 Minutes)

**Course:** DevOps & Automation Lab (ENSP461)  
**Student:** Harsh Jha  
**Objective:** Step-by-step terminal execution guide to achieve full marks during live evaluation.

---

## Pre-Demo Checklist (Do this 2 minutes before presenting)
- [ ] Open PowerShell in: `c:\Users\admin\Desktop\Python\devops-lab`
- [ ] Open Google Chrome browser tabs:
  - Tab 1: `http://localhost:8080/` (Application Web UI)
  - Tab 2: `http://localhost:8080/health` (Health Probe)
  - Tab 3: `http://localhost:8080/metrics` (Prometheus Metrics)
- [ ] Keep this runbook visible on your screen.

---

## DEMO STEP 1: Git Repository & Branching Structure (1 Minute)

### Action & Command
```powershell
git status
git branch -a
git log --oneline -n 5
```

### Expected Output
- Clean working tree on branch `master`.
- Branches listed: `master`, `develop`, `feature/auth-crud`.
- Meaningful commit message: `feat(devops): implement end-to-end cloud-native capstone architecture and configurations`.

### What to Explain to Professor
> *"Here is our Git version control setup. We follow a feature-branching workflow with `master`, `develop`, and feature branches. Every change has atomic, descriptive commit messages."*

---

## DEMO STEP 2: Java Application Compilation & Packaging (1 Minute)

### Action & Command
```powershell
& "C:\Program Files\Java\jdk-21\bin\javac.exe" -d target/classes (Get-ChildItem src/main/java/com/devopslab/*.java).FullName
& "C:\Program Files\Java\jdk-21\bin\jar.exe" --create --file target/devops-lab-1.0-SNAPSHOT.jar --main-class com.devopslab.App -C target/classes .
Get-Item target/devops-lab-1.0-SNAPSHOT.jar
```

### Expected Output
- Clean compilation without errors.
- Successful generation of `target/devops-lab-1.0-SNAPSHOT.jar` (~15 KB).

### What to Explain to Professor
> *"We compile the Student Management System using Java 21 LTS and package it into an executable JAR containing our compiled classes and embedded web server."*

---

## DEMO STEP 3: Launch Live Application & Web Dashboard (1.5 Minutes)

### Action & Command
```powershell
# Launch application in a new dedicated terminal window
Start-Process powershell -ArgumentList '-NoExit', '-Command', '& "C:\Program Files\Java\jdk-21\bin\java.exe" -jar target\devops-lab-1.0-SNAPSHOT.jar'
```

### What to Show
1. Switch to Google Chrome and refresh: `http://localhost:8080`
2. **Show the Web UI:** Point out the header showing Java 21, Docker, Kubernetes, Jenkins badges.
3. **Perform a Live CRUD Operation:**
   - In "Register New Student", type:
     - Name: `Professor Demo`
     - Email: `evaluator@university.edu`
     - Course: `Cloud-Native DevOps`
     - GPA: `9.9`
   - Click **"Add Student to System"**.
   - Show the new entry instantly appearing in the Enrolled Students Directory table.
4. **Show Health Endpoint:** Open `http://localhost:8080/health` (Shows `{"status":"UP", "uptimeSeconds": ...}`).
5. **Show Prometheus Metrics:** Open `http://localhost:8080/metrics` (Shows `app_uptime_seconds`, `jvm_memory_used_bytes`, `app_students_total`).

### What to Explain to Professor
> *"The application is running live on port 8080. Here is our responsive frontend performing real-time REST CRUD operations. The `/health` endpoint serves as our Kubernetes liveness probe, while `/metrics` feeds our Prometheus and Grafana monitoring pipeline."*

---

## DEMO STEP 4: Jenkins CI/CD Pipeline Walkthrough (1 Minute)

### Action & Command
```powershell
Get-Content Jenkinsfile -TotalCount 55
```

### What to Show & Explain to Professor
> *"Here is our declarative Jenkinsfile. It models our complete delivery pipeline:*
> 1. *Checkout from Git.*
> 2. *SonarQube static code analysis.*
> 3. *Maven compilation and automated JUnit test execution.*
> 4. *Packaging the executable artifact.*
> 5. *Trivy security vulnerability scan.*
> 6. *Multi-stage Docker container build and push to Docker Hub.*
> 7. *Automated deployment to Kubernetes with rolling update verification."*

---

## DEMO STEP 5: Docker & Kubernetes Manifests Review (1 Minute)

### Action & Command
```powershell
Get-Content Dockerfile
Get-Content k8s/deployment.yaml -TotalCount 45
Get-Content k8s/blue-green-deployment.yaml -TotalCount 40
```

### What to Show & Explain to Professor
> *"In our Dockerfile, we use a 2-stage build: a JDK build stage and a lightweight JRE Alpine runtime with a non-root system user for security. In `k8s/`, we have our Deployment with 3 pod replicas, resource limits, and health probes, alongside our Blue-Green zero-downtime deployment strategy where traffic switches cleanly between releases."*

---

## DEMO STEP 6: Infrastructure as Code (Terraform) & Ansible (1 Minute)

### Action & Command
```powershell
Get-Content terraform/main.tf
Get-Content ansible/docker_install.yml -TotalCount 35
```

### What to Show & Explain to Professor
> *"Finally, here is our Infrastructure as Code. In `terraform/main.tf`, we declare our environment parameters and replica sizing using HashiCorp HCL. In `ansible/`, our playbooks automate host provisioning, Docker daemon installation, and application deployment with automated verification."*

---

## Contingency Plan: What If Something Goes Wrong?

| Potential Issue | Quick Remediation Command |
| :--- | :--- |
| Port 8080 is already in use | Run `Stop-Process -Name java -Force` and restart application. |
| Browser doesn't refresh | Open Chrome Incognito window or press `Ctrl + F5`. |
| Professor asks for raw API calls | Run in PowerShell: `Invoke-RestMethod http://localhost:8080/api/students \| Format-Table` |
| Professor asks about database | Explain: *"The application uses an in-memory concurrent thread-safe data store for zero-dependency live demonstration, and connects to PostgreSQL in the Docker Compose and Kubernetes configurations."* |
