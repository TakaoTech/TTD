Test GH Workflow locally with Act https://github.com/nektos/act
Note: download full image.

Create `enviroments.secrets` on path `.github/workflows`

```
DOCKER_HUB_TOKEN=<dockerhub token>
SERVER_GITHUB_TOKEN=<gh token need for testing>

KEYSTORE_BASE64_SECRET=<keystore base 64>
KEYSTORE_PASSWORD=
KEY_ALIAS=
KEY_PASSWORD=

GOOGLE_LOGIN_JSON_NAME=
GOOGLE_LOGIN_JSON_BASE64=

FIREBASE_DISTRIBUTION_BASE64=
FIREBASE_JSON_BASE64=
```

can generate `KEYSTORE_BASE64_SECRET` with

```
openssl base64 < <keystore path> | tr -d '\n' | tee <keystore name file>.base64.txt
```

can generate `GOOGLE_LOGIN_JSON_BASE64` with

```
base64 <keystore path> | tr -d '\n'
```

Create `environments.variables` on path `.github/workflows`

```
DOCKER_HUB_USERNAME=<dockerhub username>
ENDPOINT_URL=<app endpoint>
```

```
act --secret-file .github/workflows/enviroments.secrets --var-file .github/workflows/environments.variables -e .github/workflows/event.json
```