FROM websphere-liberty:microProfile
MAINTAINER chrisesharp@gmail.com
COPY /target/liberty/wlp/usr/servers/defaultServer /config/
COPY /target/liberty/wlp/usr/shared/resources /config/resources/
COPY /src/main/liberty/config/jvm.options /config/jvm.options
COPY startup.sh /opt/startup.sh
RUN /opt/startup.sh
RUN installUtility install --acceptLicense defaultServer
#RUN /opt/ibm/wlp/usr/extension/liberty_dc/bin/config_liberty_dc.sh -silent /opt/ibm/wlp/usr/extension/liberty_dc/bin/silent_config_liberty_dc.txt
# Upgrade to production license if URL to JAR provided
#ARG LICENSE_JAR_URL
#RUN \ 
#  if [ $LICENSE_JAR_URL ]; then \
#    wget $LICENSE_JAR_URL -O /tmp/license.jar \
#    && java -jar /tmp/license.jar -acceptLicense /opt/ibm \
#    && rm /tmp/license.jar; \
#  fi

