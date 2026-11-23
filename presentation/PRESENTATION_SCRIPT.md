# Professor Presentation Speaking Script (10-Minute Complete Walkthrough)

**Project Title:** Design and Implementation of an End-to-End DevOps Pipeline for Cloud-Native Application Deployment  
**Course:** DevOps & Automation Lab (ENSP461) — Semester VII Capstone  
**Presenter:** Harsh Jha  
**Target Duration:** 10 Minutes  

---

## 00:00 – 01:30 | Introduction & Problem Statement

> *"Good morning, Professor. My name is Harsh Jha, and today I am excited to present my DevOps Capstone Project: **'Design and Implementation of an End-to-End DevOps Pipeline for Cloud-Native Application Deployment'**.*
>
> *In traditional software engineering, moving code from a developer's laptop to production is slow, manual, and prone to human error. Different development and staging environments cause the classic 'it works on my machine' syndrome. Deployment delays impact business value, and detecting regressions or performance bottlenecks after deployment is painful.*
>
> *To solve this, my project implements an end-to-end, automated DevOps lifecycle that integrates industry-standard tools: **Git** for distributed version control, **Jenkins** for Continuous Integration, **Docker** for multi-stage containerization, **Kubernetes** for container orchestration, **Ansible** for configuration automation, **Terraform** for Infrastructure as Code, and **Prometheus & Grafana** for real-time observability."*

---

## 01:30 – 03:00 | The Application Architecture (Spring Boot / REST / Java 21)

> *"Before diving into the pipeline, let me discuss the workload being delivered. I developed a Cloud-Native **Student Management System** adhering to all lab requirements:*
> 1. *It runs on modern **Java 21** using an event-driven, high-concurrency architecture.*
> 2. *It exposes clean **RESTful APIs** supporting full CRUD operations: registering students, fetching student directories, updating details, and deleting records.*
> 3. *It includes user authentication logic at `/api/login`.*
> 4. *It provides a responsive, modern HTML5 web dashboard for interactive operations.*
> 5. *Most importantly for cloud-native readiness, it includes dedicated operational endpoints: `/health` for Kubernetes liveness and readiness probes, and `/metrics` exposing Prometheus-compatible metrics such as JVM memory utilization, request counts, and application uptime.*
>
> *Everything compiles cleanly into a self-contained, lightweight executable JAR artifact via Maven."*

---

## 03:00 – 05:00 | Continuous Integration Pipeline with Jenkins & Security

> *"Now, let us examine the automated CI pipeline. In my repository, you will find a declarative **`Jenkinsfile`** structured into seven clear, production-grade stages:*
>
> 1. ***Checkout & SCM Inspection:*** *The pipeline pulls source code from GitHub upon every git commit.*
> 2. ***Static Code Analysis & Security:*** *Using **SonarQube**, it verifies code quality, test coverage, and flags security vulnerabilities.*
> 3. ***Automated Build & Unit Testing:*** *Maven compiles the Java 21 source code and runs JUnit 5 test suites. If any test fails, the build breaks immediately, protecting the mainline branch.*
> 4. ***Artifact Packaging:*** *It packages the application into `devops-lab-1.0-SNAPSHOT.jar` and archives the artifact.*
> 5. ***Vulnerability Scanning:*** *Tools like Trivy scan filesystem dependencies for high and critical CVEs.*
> 6. ***Multi-Stage Docker Build:*** *We build a secure container image.*
> 7. ***Registry Push & Staging Deployment:*** *The image is tagged and pushed to Docker Hub under `harshj00/devops-lab:1.0`."*

---

## 05:00 – 06:45 | Containerization & Kubernetes Orchestration

> *"Why did we choose Docker and Kubernetes?*
>
> *In our **`Dockerfile`**, I implemented an optimized **multi-stage build**:*
> - *The build stage uses `eclipse-temurin:21-jdk-alpine` to compile source code.*
> - *The final runner stage uses a stripped-down `eclipse-temurin:21-jre-alpine`.*
> - *We create a non-root system user `appuser` so the container never runs as root, adhering to container security best practices.*
> - *We also define `docker-compose.yml` defining multi-container dependencies with PostgreSQL, isolated bridge networks, and automated container health checks.*
>
> *Moving to Kubernetes orchestration in the **`k8s/`** directory, I authored declarative manifests:*
> - *A **Deployment** running 3 resilient pod replicas with CPU and memory limits.*
> - *A **NodePort Service** routing traffic across the pods on port `30080`.*
> - *A **ConfigMap** for decoupling application settings and a **Secret** for sensitive database credentials.*
> - *A **PersistentVolumeClaim (PVC)** to guarantee state persistence.*
> - *And for deployment strategies, I implemented a **Blue-Green Deployment** configuration in `k8s/blue-green-deployment.yaml`. When rolling out version 2.0, the green deployment is tested in isolation, and traffic is switched instantly with zero downtime simply by updating the Service selector."*

---

## 06:45 – 08:15 | Infrastructure as Code (Terraform) & Ansible Automation

> *"To avoid manual server configuration, I automated the underlying infrastructure:*
>
> - ***Terraform (`terraform/main.tf`):*** *Used for declarative Infrastructure as Code. We parameterized environment variables, instance counts, and network ports using standard HCL (`variables.tf` and `outputs.tf`), ensuring that the infrastructure is reproducible across staging and production.*
> - ***Ansible (`ansible/`):*** *Provides idempotent configuration management. In `ansible/docker_install.yml`, it automates Docker engine prerequisites and repository keys. In `ansible/app_deploy.yml`, it copies compose manifests, pulls container images, starts services, and verifies readiness via automated HTTP status polling.*
> - ***GitOps (`k8s/argocd-application.yaml`):*** *Defines an ArgoCD Custom Resource Definition that continuously synchronizes the live Kubernetes cluster state with our GitHub repository."*

---

## 08:15 – 09:15 | Observability: Prometheus & Grafana Monitoring

> *"In production, deploying software is only half the battle; monitoring its operational health is vital.*
>
> *My application natively exports Prometheus metrics format at `/metrics`. In `monitoring/prometheus.yml`, Prometheus is configured with a 10-second scrape interval targeting our application.*
>
> *In `monitoring/grafana-dashboard.json`, we built visualization panels tracking:*
> 1. *Real-time application uptime in seconds.*
> 2. *Total HTTP throughput and request rate.*
> 3. *Live student record counts.*
> 4. *JVM heap memory usage over time.*
>
> *If JVM memory exceeds the threshold or HTTP error rates spike, alerts can be triggered immediately."*

---

## 09:15 – 10:00 | Conclusion & Q&A Transition

> *"To conclude, this project successfully bridges software development with cloud-native operations:*
> - *We have a fully working, tested Java 21 REST application with responsive UI and CRUD capabilities.*
> - *An automated, secure CI/CD pipeline defined in code.*
> - *Production-grade Kubernetes manifests with self-healing, rolling updates, and Blue-Green deployment.*
> - *Reproducible IaC with Terraform, Ansible, and Prometheus monitoring.*
>
> *I am now ready to demonstrate the live application, the Git history, and the pipeline configurations in the terminal. Thank you, Professor! I welcome your questions."*
