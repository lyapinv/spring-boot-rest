#!/bin/bash

CRC_REGISTRY=default-route-openshift-image-registry.apps-crc.testing
eval $(crc oc-env)
docker login -u kubeadmin -p $(oc whoami -t) $CRC_REGISTRY

REMOTE_CRC_HOST=192.168.1.106
OS_NAMESPACE=tutorial
CRC_REGISTRY=default-route-openshift-image-registry.apps-crc.testing
GATEWAYS_VS_HOST=spring-boot-rest-client.example.ru
CLIENT_APPLICATION_NAME=spring-boot-rest-client
CLIENT_APPLICATION_PATH=spring-boot-rest-client
export CLIENT_VERSION=apV2
export CLIENT_IMAGE=$CRC_REGISTRY/tutorial/spring-boot-rest-client:$CLIENT_VERSION
docker build -t tutorial/spring-boot-rest-client:${CLIENT_VERSION} spring-boot-rest-client/.
docker tag tutorial/spring-boot-rest-client:${CLIENT_VERSION} ${CLIENT_IMAGE}
docker push ${CLIENT_IMAGE}
OS_NAMESPACE2=tutorial2
SERVER_APPLICATION_NAME=spring-boot-rest-server
SERVER_APPLICATION_PATH=spring-boot-rest-server
export SERVER_VERSION=apV2
export SERVER_IMAGE=$CRC_REGISTRY/$OS_NAMESPACE2/spring-boot-rest-server:$SERVER_VERSION
docker build -t $OS_NAMESPACE2/spring-boot-rest-server:${SERVER_VERSION} spring-boot-rest-server/.
docker tag $OS_NAMESPACE2/spring-boot-rest-server:${SERVER_VERSION} ${SERVER_IMAGE}
docker push ${SERVER_IMAGE}
docker rmi $(docker images -f "dangling=true" -q)

# curl -v http://spring-boot-rest-client-tutorial.apps-crc.testing/ping