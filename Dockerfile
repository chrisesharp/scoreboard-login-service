FROM open-liberty:microProfile1
MAINTAINER chrisesharp@gmail.com
COPY /target/liberty/wlp/usr/servers/defaultServer /config/
COPY /target/liberty/wlp/usr/shared/resources /config/resources/
COPY /src/main/liberty/config/jvm.options /config/jvm.options
COPY startup.sh /opt/startup.sh
CMD ["/opt/startup.sh"]
