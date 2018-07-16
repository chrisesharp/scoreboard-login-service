
# Scoreboard Authentication Service

## Using the service

When running, point your browser to http://localhost:32785/ to authenticate with 
GitHub and follow the instructions.

## Secrets Needed

The service need the following secrets injected, in order to work:
* GITHUB_APP_ID
* GITHUB_APP_SECRET

These need to be put in a file called `secrets.txt` in the form:
```
GITHUB_APP_ID=xxxxxx
GITHUB_APP_SECRET=xxxxxx
```

The `Makefile` includes this if present, to set environment variables for the local build, or injects them as kubernetes secrets for helm deploys.

## Build

Perform a Maven and Docker build with:
```
make
```

## Test

```
make test
```

## Run

Run locally under Docker with:
```
make run-keystore
```
This will mount the keystore volume you will need to have created using:
https://github.com/chrisesharp/shared-keystore 

Run under Kubernetes in Docker for Mac with Helm installed:
```
make install
```

## Clean Up

Clean up build artifacts with:
```
make clean
```

