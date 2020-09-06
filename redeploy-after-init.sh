#!/bin/bash

#REMOTE_CRC_HOST=192.168.1.106
#OS_NAMESPACE=tutorial
#CRC_REGISTRY=default-route-openshift-image-registry.apps-crc.testing
#GATEWAYS_VS_HOST=spring-boot-rest-client.example.ru
#CLIENT_APPLICATION_NAME=spring-boot-rest-client
#CLIENT_APPLICATION_PATH=spring-boot-rest-client
#export CLIENT_VERSION=apV2
#export CLIENT_IMAGE=$CRC_REGISTRY/tutorial/spring-boot-rest-client:$CLIENT_VERSION
#docker build -t tutorial/spring-boot-rest-client:${CLIENT_VERSION} spring-boot-rest-client/.
#docker tag tutorial/spring-boot-rest-client:${CLIENT_VERSION} ${CLIENT_IMAGE}
#docker push ${CLIENT_IMAGE}
#OS_NAMESPACE2=tutorial2
#SERVER_APPLICATION_NAME=spring-boot-rest-server
#SERVER_APPLICATION_PATH=spring-boot-rest-server
#export SERVER_VERSION=apV2
#export SERVER_IMAGE=$CRC_REGISTRY/$OS_NAMESPACE2/spring-boot-rest-server:$SERVER_VERSION
#docker build -t $OS_NAMESPACE2/spring-boot-rest-server:${SERVER_VERSION} spring-boot-rest-server/.
#docker tag $OS_NAMESPACE2/spring-boot-rest-server:${SERVER_VERSION} ${SERVER_IMAGE}
#docker push ${SERVER_IMAGE}
#docker rmi $(docker images -f "dangling=true" -q)

REMOTE_CRC_HOST=192.168.1.106
OS_NAMESPACE=tutorial
CRC_REGISTRY=default-route-openshift-image-registry.apps-crc.testing
GATEWAYS_VS_HOST=spring-boot-rest-client.example.ru

CLIENT_APPLICATION_NAME=spring-boot-rest-client
CLIENT_APPLICATION_PATH=spring-boot-rest-client
echo "Start deployment script for Namespace: " $OS_NAMESPACE

#Init oc
eval $(crc oc-env)

docker login -u kubeadmin -p $(oc whoami -t) $CRC_REGISTRY

# Delete project
oc delete project $OS_NAMESPACE

#mvn clean package
docker login -u kubeadmin -p $(oc whoami -t) $CRC_REGISTRY
export CRC_REGISTRY=default-route-openshift-image-registry.apps-crc.testing

## Build Client application and deploy
#export CLIENT_VERSION=apV1
#export CLIENT_IMAGE=$CRC_REGISTRY/tutorial/spring-boot-rest-client:$CLIENT_VERSION
#docker build -t tutorial/spring-boot-rest-client:${CLIENT_VERSION} spring-boot-rest-client/.
#docker tag tutorial/spring-boot-rest-client:${CLIENT_VERSION} ${CLIENT_IMAGE}
#docker push ${CLIENT_IMAGE}

OS_NAMESPACE2=tutorial2
SERVER_APPLICATION_NAME=spring-boot-rest-server
SERVER_APPLICATION_PATH=spring-boot-rest-server
echo "Start deployment script for Namespace: " $OS_NAMESPACE2

## Build Server application and deploy
#export SERVER_VERSION=apV1
#export SERVER_IMAGE=$CRC_REGISTRY/$OS_NAMESPACE2/spring-boot-rest-server:$SERVER_VERSION
#docker build -t $OS_NAMESPACE2/spring-boot-rest-server:${SERVER_VERSION} spring-boot-rest-server/.
#docker tag $OS_NAMESPACE2/spring-boot-rest-server:${SERVER_VERSION} ${SERVER_IMAGE}
#docker push ${SERVER_IMAGE}

# Clear all untagged images
docker rmi $(docker images -f "dangling=true" -q)

#oc delete deployment $OS_NAMESPACE -n $CLIENT_APPLICATION_NAME
# Wait until all resourses of just deleted project are being deleted - need for success creation new project.
# Then create the new one
oc delete project $OS_NAMESPACE
oc delete project $OS_NAMESPACE2

echo "Wait while old project being deleted..."
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
############################
echo "Wait while old project being deleted..."
DEL_RES="$(oc get project.project.openshift.io/$OS_NAMESPACE2 --show-kind --ignore-not-found)"
while [ ${#DEL_RES} != 0 ];
    do
        echo "Updating";
        sleep 1;
        DEL_RES="$(oc get project.project.openshift.io/$OS_NAMESPACE2 --show-kind --ignore-not-found)";
        echo "DEBUG. Available symbols" : ${#DEL_RES};
    done && echo "Finish waiting for project deletion.\n";
echo "Create new project"
oc new-project $OS_NAMESPACE2


# Deploy Client app
oc apply -f $CLIENT_APPLICATION_PATH/client-deployment.yaml -n $OS_NAMESPACE
# Deploy Server app
oc apply -f $SERVER_APPLICATION_PATH/server-deployment.yaml -n $OS_NAMESPACE2

# Ingress config for Client app
oc apply -f ingress-cfg.yaml -n $OS_NAMESPACE
export INGRESS_HOST=$(crc ip)
export INGRESS_PORT=$(oc -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
export SECURE_INGRESS_PORT=$(oc -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="https")].port}')

export WS_SERVER_URI="http://"$INGRESS_HOST:$INGRESS_PORT"/ping"
echo "Ingress configured. Call WS with the next URI: curl -v -HHost:"$GATEWAYS_VS_HOST $WS_SERVER_URI -Hcustom-rl-header:val1
echo "For remote CRC use: curl -v -HHost:"$GATEWAYS_VS_HOST " -Hcustom-rl-header:val1 " "http://"$REMOTE_CRC_HOST:$INGRESS_PORT"/ping"

# Egress config for Client app
oc apply -f egress-cfg.yaml -n $OS_NAMESPACE2

#---
export LOCAL_NS=tutorial-local
export LOCAL_SRV_VERSION=v17r
export LOCAL_SRV_PROJECT=spring-boot-rest-external
docker build -t ${LOCAL_NS}/${LOCAL_SRV_PROJECT}:${LOCAL_SRV_VERSION} ${LOCAL_SRV_PROJECT}/.
export CONTROLLER_PORT=8080
export JMX_PORT=1099
export MJO="
-Dcom.sun.management.jmxremote
-Djava.rmi.server.hostname=$MY_IP
-Dcom.sun.management.jmxremote.port=$JMX_PORT
-Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.local.only=false "
docker rm -f $(docker container ls -q -a --filter name=${LOCAL_SRV_PROJECT})
CONTAINER_CID=$(docker run -d --name ${LOCAL_SRV_PROJECT} -p ${CONTROLLER_PORT}:${CONTROLLER_PORT} -p $JMX_PORT:$JMX_PORT -e JAVA_OPTS="$MJO" ${LOCAL_NS}/${LOCAL_SRV_PROJECT}:${LOCAL_SRV_VERSION})
echo "WebService container started. CONTAINER_CID="$CONTAINER_CID


#----------
#istioctl proxy-config clusters $(oc get pods -n tutorial|grep spring-boot-rest-server|awk '{ print $1 }'|head -1) --fqdn spring-boot-rest-server-svc.tutorial.svc.cluster.local
#istioctl proxy-config route $(oc get pods -n tutorial|grep spring-boot-rest-server|awk '{ print $1 }'|head -1) -o json
#istioctl dashboard envoy $(oc get pods -n tutorial|grep spring-boot-rest-client|awk '{ print $1 }'|head -1) -n tutorial
#istioctl dashboard envoy $(oc get pods -n istio-system|grep istio-ingressgateway|awk '{ print $1 }'|head -1) -n istio-system

#curl -v -HHost:spring-boot-rest-client.example.ru -Hcustom-rl-header:val1 http://192.168.1.106:31067/pingServer
#curl -v -HHost:spring-boot-rest-client.example.ru http://192.168.1.106:31067/ping