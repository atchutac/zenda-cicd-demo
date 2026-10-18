# Zenda CI/CD Demo

Spring Boot → Docker → GitHub Actions → Kubernetes → ArgoCD (GitOps)

```
git push ──▶ GitHub Actions ──▶ test ──▶ build image ──▶ Docker Hub
                    │
                    └──▶ commits new image tag to k8s/deployment.yaml
                                   │
              ArgoCD (auto-sync) ◀─┘ detects Git change ──▶ Kubernetes
```

## Endpoints
| Path | Description |
|------|-------------|
| `/` | HTML homepage (shows serving pod name) |
| `/api/health` | JSON health check |

## 1. Run locally
```bash
mvn spring-boot:run          # http://localhost:8080
mvn verify                   # run tests
```

## 2. Docker
```bash
docker build -t <dockerhub-user>/zenda-demo:1.0.0 .
docker run -p 8080:8080 <dockerhub-user>/zenda-demo:1.0.0
docker login
docker push <dockerhub-user>/zenda-demo:1.0.0
```

## 3. CI/CD (GitHub Actions)
Add repository secrets (Settings → Secrets and variables → Actions):
- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN` (Docker Hub access token, not your password)

Every push to `main` runs: test → build/push image (`:<sha>` and `:latest`) → update the image tag in `k8s/deployment.yaml`.

## 4. Kubernetes (Minikube)
```bash
minikube start
# replace YOUR_DOCKERHUB_USERNAME in k8s/deployment.yaml first
kubectl apply -f k8s/
kubectl get pods
kubectl get svc
minikube service zenda-demo --url
```

## 5. GitOps (ArgoCD)
```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
kubectl -n argocd port-forward svc/argocd-server 8443:443   # https://localhost:8443 (user: admin)

# edit repoURL in argocd/application.yaml, then:
kubectl apply -f argocd/application.yaml
```
Auto-sync, prune and self-heal are enabled. If you applied `k8s/` manually earlier, delete those resources first or let ArgoCD adopt them.

## 6. Verify
- `http://<minikube-ip>:30080/` and `/api/health`
- ArgoCD UI shows `Synced` / `Healthy`
- Push a code change → new pods roll out automatically

## Screenshots
See `/screenshots` (numbered 01–12, matching the assessment checklist).

## Security
No secrets in the repo; registry credentials live in GitHub Actions secrets.
