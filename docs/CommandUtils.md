Test GH Workflow locally with Act https://github.com/nektos/act
Note: download full image.

Create `enviroments.secrets` on path `.github/workflows`

```
DOCKER_HUB_TOKEN=<dockerhub token>
SERVER_GITHUB_TOKEN=<gh token need for testing>
```

Create `environments.variables` on path `.github/workflows`

```
DOCKER_HUB_USERNAME=<dockerhub username>
ENDPOINT_URL=<app endpoint>
```

```
act --secret-file .github/workflows/enviroments.secrets --var-file .github/workflows/environments.variables
```