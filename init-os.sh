#!/bin/bash

REMOTE_CRC_HOST=192.168.1.106
OS_NAMESPACE=tutorial-os
CRC_REGISTRY=default-route-openshift-image-registry.apps-crc.testing
GATEWAYS_VS_HOST=spring-boot-rest-client.example.ru

CLIENT_APPLICATION_NAME=spring-boot-rest-client
CLIENT_APPLICATION_PATH=spring-boot-rest-client

SERVER_APPLICATION_NAME=spring-boot-rest-server
SERVER_APPLICATION_PATH=spring-boot-rest-server

echo "Start deployment script for Namespace: " $OS_NAMESPACE

#Init oc
eval $(crc oc-env)

docker login -u kubeadmin -p $(oc whoami -t) $CRC_REGISTRY

# Delete project
oc delete project $OS_NAMESPACE

mvn clean package
docker login -u kubeadmin -p $(oc whoami -t) $CRC_REGISTRY
export CRC_REGISTRY=default-route-openshift-image-registry.apps-crc.testing

# Build Server application and deploy
export SERVER_VERSION=v13
export SERVER_IMAGE=$CRC_REGISTRY/tutorial-os/spring-boot-rest-server:$SERVER_VERSION
docker build -t tutorial-os/spring-boot-rest-server:${SERVER_VERSION} spring-boot-rest-server/.
docker tag tutorial-os/spring-boot-rest-server:${SERVER_VERSION} ${SERVER_IMAGE}
docker push ${SERVER_IMAGE}
# Clear all untagged images
docker rmi $(docker images -f "dangling=true" -q)

#oc delete deployment $OS_NAMESPACE -n $CLIENT_APPLICATION_NAME
# Wait until all resourses of just deleted project are being deleted - need for success creation new project.
# Then create the new one
echo "Wait while old project being deleted..."
oc delete project $OS_NAMESPACE
DEL_RES="$(oc get project.project.openshift.io/$OS_NAMESPACE --show-kind --ignore-not-found)"
while [ ${#DEL_RES} != 0 ];
    do
        echo "Updating";
        sleep 1;
        DEL_RES="$(oc get project.project.openshift.io/$OS_NAMESPACE --show-kind --ignore-not-found)";
        echo "DEBUG. Available symbols" : ${#DEL_RES};
    done && echo "Finish waiting for project deletion.\n";
echo "Create new project"
oc new-project $OS_NAMESPACE

# Deploy Server app
oc apply -f $SERVER_APPLICATION_PATH/server-deployment-os.yaml -n $OS_NAMESPACE

oc annotate route spring-boot-rest-server-1-svc --overwrite haproxy.router.openshift.io/timeout=60s -n tutorial-os

# openshift run - 200:; 400:;
# local docker run - 200:7,5; 400:14;
# docker run -it -p 8081:8081 tutorial-os/spring-boot-rest-server


#curl -v -HHost:spring-boot-rest-client.example.ru -Hcustom-rl-header:val1 http://192.168.1.106:31067/pingServer
#curl -v -HHost:spring-boot-rest-client.example.ru http://192.168.1.106:31067/ping

#curl -v http://spring-boot-rest-server-1-svc-tutorial-os.apps-crc.testing/ping
#curl -v http://localhost:8082/pingServerLoop/300