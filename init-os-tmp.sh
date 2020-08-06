#!/bin/bash

mvn clean package

export MY_IP=$(ifconfig | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p')
echo "MY_IP="$MY_IP

export WS_VERSION=v17r
export WS_PROJECT=spring-boot-jaxws
echo "WS_VERSION=" $WS_VERSION
echo "WS_PROJECT=" $WS_PROJECT
docker container stop $(docker container ls -q --filter name=${WS_PROJECT})

docker build -t tutorial-os/${WS_PROJECT}:${WS_VERSION} ${WS_PROJECT}/.
export JMX_PORT=1097
export MJO="
-Dcom.sun.management.jmxremote
-Djava.rmi.server.hostname=$MY_IP
-Dcom.sun.management.jmxremote.port=$JMX_PORT
-Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.local.only=false "
WS_CID=$(docker run -d --name ${WS_PROJECT} -p 9999:9999 -p $JMX_PORT:$JMX_PORT -e JAVA_OPTS="$MJO" tutorial-os/${WS_PROJECT}:${WS_VERSION})
echo "WebService container started. WS_CID="$WS_CID

export SERVER_VERSION=v17r
export SERVER_PROJECT=spring-boot-rest-server
echo "SERVER_VERSION=" $SERVER_VERSION
echo "SERVER_PROJECT=" $SERVER_PROJECT
docker build -t tutorial-os/${SERVER_PROJECT}:${SERVER_VERSION} ${SERVER_PROJECT}/.
export JMX_PORT=1098
export MJO="
-Dcom.sun.management.jmxremote
-Djava.rmi.server.hostname=$MY_IP
-Dcom.sun.management.jmxremote.port=$JMX_PORT
-Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.local.only=false "
SRV_CID=$(docker run -d -p 8081:8081 -p $JMX_PORT:$JMX_PORT -e JAVA_OPTS="$MJO" tutorial-os/${SERVER_PROJECT}:${SERVER_VERSION} --name ${SERVER_PROJECT})
echo "Server container started. SRV_CID="$SRV_CID

export CLIENT_VERSION=v17r
export CLIENT_PROJECT=spring-boot-rest-client
echo "CLIENT_VERSION=" $CLIENT_VERSION
echo "CLIENT_PROJECT=" $CLIENT_PROJECT
docker build -t tutorial-os/${CLIENT_PROJECT}:${CLIENT_VERSION} ${CLIENT_PROJECT}/.
export JMX_PORT=1099
export MJO="
-Dcom.sun.management.jmxremote
-Djava.rmi.server.hostname=$MY_IP
-Dcom.sun.management.jmxremote.port=$JMX_PORT
-Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.local.only=false "
CL_CID=$(docker run -d -p 8082:8082 -p $JMX_PORT:$JMX_PORT -e JAVA_OPTS="$MJO" tutorial-os/${CLIENT_PROJECT}:${CLIENT_VERSION} --name ${CLIENT_PROJECT})
echo "Client container started. CL_CID="$CL_CID

# Clear all untagged images
docker rmi $(docker images -f "dangling=true" -q)

echo "jmx connection path: service:jmx:rmi:///jndi/rmi://$MY_IP:$JMX_PORT/jmxrmi"

#echo "to remove containers run: \n
#docker rm -f $WS_CID
#docker rm -f $CL_CID
#docker rm -f $SRV_CID"

read  -n 1 -p "Press any button to remove all created containers:" mainmenuinput
docker rm -f $WS_CID
docker rm -f $CL_CID
docker rm -f $SRV_CID

#sleep 5;
#curl -v http://$MY_IP:8082/pingServerLoopRxWs/1

#docker container prune

#docker container stop $(docker container ls -q --filter name=${WS_PROJECT})


#java -jar spring-boot-jaxws-1.0-SNAPSHOT.jar --name spring-boot-jaxws

#docker exec -it d954e4d09b56 /bin/sh

#curl -v -HHost:spring-boot-rest-client.example.ru -Hcustom-rl-header:val1 http://192.168.1.106:31067/pingServer
#curl -v -HHost:spring-boot-rest-client.example.ru http://192.168.1.106:31067/ping
#curl -v http://localhost:8082/pingServerLoopRxMock/50
#curl -v http://127.0.0.1:8082/pingServerLoopRxWs/1

#http://locahost:9999/ws/hello?wsdl

#curl -v http://spring-boot-rest-server-1-svc-tutorial-os.apps-crc.testing/ping
#curl -v http://localhost:8082/pingServerLoop/300

# service:jmx:rmi:///jndi/rmi://192.168.1.84:1098/jmxrmi