#!/bin/bash

mvn clean package

export WS_VERSION=v17r
export WS_PROJECT=spring-boot-jaxws
echo "WS_VERSION=" $WS_VERSION
echo "WS_PROJECT=" $WS_PROJECT

docker container stop $(docker container ls -q --filter name=${WS_PROJECT})

docker build -t tutorial-os/${WS_PROJECT}:${WS_VERSION} spring-boot-jaxws/.
CID=$(docker run -d -p 9999:9999 tutorial-os/spring-boot-jaxws:v17r --name spring-boot-jaxws)
echo "CID=" $CID
# Clear all untagged images
docker rmi $(docker images -f "dangling=true" -q)

#docker stop $CID
#docker rm $CID

#java -jar spring-boot-jaxws-1.0-SNAPSHOT.jar --name spring-boot-jaxws

#docker exec -it d954e4d09b56 /bin/sh

#curl -v -HHost:spring-boot-rest-client.example.ru -Hcustom-rl-header:val1 http://192.168.1.106:31067/pingServer
#curl -v -HHost:spring-boot-rest-client.example.ru http://192.168.1.106:31067/ping
#curl -v http://localhost:8082/pingServerLoopRxMock/50
#curl -v http://localhost:8082/pingServerLoopRxWs/1

#curl -v http://spring-boot-rest-server-1-svc-tutorial-os.apps-crc.testing/ping
#curl -v http://localhost:8082/pingServerLoop/300