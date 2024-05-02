

Test GH Workflow locally with Act https://github.com/nektos/act
Note: download full image.

The env file need contains:

```
DOCKER_HUB_USERNAME=<dockerhub username>
DOCKER_HUB_TOKEN=<dockerhub token>

ENDPOINT_URL=<app endpoint>
SERVER_GITHUB_TOKEN=<gh token need for testing>
```

For me in local need remove action `check_yaml_consistency` for permission error on yaml checking

```
act --env-file .github/workflows/environments.env
```