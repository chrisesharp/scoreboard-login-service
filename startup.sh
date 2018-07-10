#!/bin/bash

export CONTAINER_NAME=auth

SERVER_PATH=/opt/ol/wlp/output/defaultServer
echo "Building keystore/truststore from cert.pem"
echo "-creating dir"
mkdir -p ${SERVER_PATH}/resources/security
echo "-cd dir"
cd ${SERVER_PATH}/resources/
echo "-importing jvm truststore to server truststore"
keytool -importkeystore -srckeystore $JAVA_HOME/lib/security/cacerts -destkeystore security/truststore.jks -srcstorepass changeit -deststorepass truststore

if [ -f /etc/cert/cert.pem ]; then
  echo "-converting pem to pkcs12"
  openssl pkcs12 -passin pass:keystore -passout pass:keystore -export -out cert.pkcs12 -in /etc/cert/cert.pem
  echo "-importing pem to truststore.jks"
  keytool -import -v -trustcacerts -alias default -file /etc/cert/cert.pem -storepass truststore -keypass keystore -noprompt -keystore security/truststore.jks
  echo "-creating dummy key.jks"
  keytool -genkey -storepass testOnlyKeystore -keypass wefwef -keyalg RSA -alias endeca -keystore security/key.jks -dname CN=rsssl,OU=unknown,O=unknown,L=unknown,ST=unknown,C=CA
  echo "-emptying key.jks"
  keytool -delete -storepass testOnlyKeystore -alias endeca -keystore security/key.jks
  echo "-importing pkcs12 to key.jks"
  keytool -v -importkeystore -srcalias 1 -alias 1 -destalias default -noprompt -srcstorepass keystore -deststorepass testOnlyKeystore -srckeypass keystore -destkeypass testOnlyKeystore -srckeystore cert.pkcs12 -srcstoretype PKCS12 -destkeystore security/key.jks -deststoretype JKS
  cd ${SERVER_PATH}
  echo "done"
fi

exec /opt/ol/wlp/bin/server run defaultServer

