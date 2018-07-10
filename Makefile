PORT = 32785
SSL_PORT = 32444
IMAGE = login:v1.0.0
CHART = chart/login

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

.PHONY: verify
verify:
	mvn verify

.PHONY: coverage
coverage:
	mvn com.gavinmogan:codacy-maven-plugin:coverage \
		-DcoverageReportFile=target/site/jacoco/jacoco.xml \
		-DprojectToken=$(CODACY_PROJECT_TOKEN) -DapiToken=$(CODACY_API_TOKEN)

.PHONY: run
run:
	docker run --rm -p$(PORT):9080 -p$(SSL_PORT):9443 $(IMAGE)
#	docker run --rm \
		-p$(PORT):9080 \
		-p$(SSL_PORT):9443 \
		-eGITHUB_APP_ID=${GITHUB_APP_ID} \
		-eGITHUB_APP_SECRET=${GITHUB_APP_SECRET} \
		-eAUTH_URL=${AUTH_URL} $(IMAGE) 
	
.PHONY: install
install:
	helm dependency build $(CHART)
	helm upgrade --wait --install login $(CHART)
