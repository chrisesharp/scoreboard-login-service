ifdef SECRETS
	include $(SECRETS)
endif
PORT = 32785
SSL_PORT = 32444
IMAGE = login:v1.0.0
CHART = chart/login
SECURITY = /opt/ol/wlp/usr/servers/defaultServer/resources/security

GITHUB_AUTH_CALLBACK_URL = http://localhost:32785/GitHubAuth/callback
FRONT_END_SUCCESS_CALLBACK = http://localhost:32785/callback/success
FRONT_END_FAIL_CALLBACK = http://localhost:32785/callback/failure


all: build docker

.PHONY: clean
clean:
	mvn clean

.PHONY: build
build:
	mvn package

.PHONY: docker
docker:
	docker build -t $(IMAGE) .

.PHONY: test
test:
	mvn verify

.PHONY: coverage
coverage:
	mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package
	mvn com.gavinmogan:codacy-maven-plugin:coverage \
		-DcoverageReportFile=target/site/jacoco-ut/jacoco.xml \
		-DprojectToken=$(CODACY_PROJECT_TOKEN) -DapiToken=$(CODACY_API_TOKEN)

.PHONY: run
run:
	docker run --rm \
		-p$(PORT):9080 \
		-p$(SSL_PORT):9443 \
		-eGITHUB_AUTH_CALLBACK_URL=${GITHUB_AUTH_CALLBACK_URL} \
		-eFRONT_END_SUCCESS_CALLBACK=${FRONT_END_SUCCESS_CALLBACK} \
		-eFRONT_END_FAIL_CALLBACK=${FRONT_END_FAIL_CALLBACK} \
		-eGITHUB_APP_ID=${GITHUB_APP_ID} \
		-eGITHUB_APP_SECRET=${GITHUB_APP_SECRET} \
		$(IMAGE)

.PHONY: run-keystore
run-keystore:
	docker run --rm \
		-p$(PORT):9080 \
		-p$(SSL_PORT):9443 \
		-v keystore:$(SECURITY) \
		-eGITHUB_AUTH_CALLBACK_URL=${GITHUB_AUTH_CALLBACK_URL} \
		-eFRONT_END_SUCCESS_CALLBACK=${FRONT_END_SUCCESS_CALLBACK} \
		-eFRONT_END_FAIL_CALLBACK=${FRONT_END_FAIL_CALLBACK} \
		-eGITHUB_APP_ID=${GITHUB_APP_ID} \
		-eGITHUB_APP_SECRET=${GITHUB_APP_SECRET} \
		$(IMAGE) 

.PHONY: install
install:
	./inject-secrets.sh
	helm dependency build $(CHART)
	helm upgrade --wait --install login $(CHART)
