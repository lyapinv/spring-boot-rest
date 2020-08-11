#!/bin/bash

REMOTE_CRC_HOST=192.168.1.106
OS_NAMESPACE=tutorial
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

# Build Client application and deploy
export CLIENT_VERSION=v14
export CLIENT_IMAGE=$CRC_REGISTRY/tutorial/spring-boot-rest-client:$CLIENT_VERSION
docker build -t tutorial/spring-boot-rest-client:${CLIENT_VERSION} spring-boot-rest-client/.
docker tag tutorial/spring-boot-rest-client:${CLIENT_VERSION} ${CLIENT_IMAGE}
docker push ${CLIENT_IMAGE}

# Build Server application and deploy
export SERVER_VERSION=v14
export SERVER_IMAGE=$CRC_REGISTRY/tutorial/spring-boot-rest-server:$SERVER_VERSION
docker build -t tutorial/spring-boot-rest-server:${SERVER_VERSION} spring-boot-rest-server/.
docker tag tutorial/spring-boot-rest-server:${SERVER_VERSION} ${SERVER_IMAGE}
docker push ${SERVER_IMAGE}

#mvn clean package
#docker build -t tutorial/spring-boot-rest-client:5 spring-boot-rest-client/.
#docker tag tutorial/spring-boot-rest-client:5 default-route-openshift-image-registry.apps-crc.testing/tutorial/spring-boot-rest-client:5
#docker push default-route-openshift-image-registry.apps-crc.testing/tutorial/spring-boot-rest-client:5


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

# Deploy Client app
oc apply -f $CLIENT_APPLICATION_PATH/client-deployment.yaml -n $OS_NAMESPACE
# Deploy Server app
oc apply -f $SERVER_APPLICATION_PATH/server-deployment.yaml -n $OS_NAMESPACE

# Prepare rate limiter
oc apply -f rate-limit-config.yaml
oc apply -f EnvoyFilter-cfg.yaml
# TODO - как проверить что конфигурация применилась верно ? Нужна например какая то выгрузка конфигов Sidecar...



# Ingress config for Client app
oc apply -f ingress-cfg.yaml -n $OS_NAMESPACE
export INGRESS_HOST=$(crc ip)
export INGRESS_PORT=$(oc -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
export SECURE_INGRESS_PORT=$(oc -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="https")].port}')

export WS_SERVER_URI="http://"$INGRESS_HOST:$INGRESS_PORT"/ping"
echo "Ingress configured. Call WS with the next URI: curl -v -HHost:"$GATEWAYS_VS_HOST $WS_SERVER_URI -Hcustom-rl-header:val1
echo "For remote CRC use: curl -v -HHost:"$GATEWAYS_VS_HOST " -Hcustom-rl-header:val1 " "http://"$REMOTE_CRC_HOST:$INGRESS_PORT"/ping"


#----------
#istioctl proxy-config clusters $(oc get pods -n tutorial|grep spring-boot-rest-server|awk '{ print $1 }'|head -1) --fqdn spring-boot-rest-server-svc.tutorial.svc.cluster.local
#istioctl proxy-config route $(oc get pods -n tutorial|grep spring-boot-rest-server|awk '{ print $1 }'|head -1) -o json
#istioctl dashboard envoy $(oc get pods -n tutorial|grep spring-boot-rest-client|awk '{ print $1 }'|head -1) -n tutorial
#istioctl dashboard envoy $(oc get pods -n istio-system|grep istio-ingressgateway|awk '{ print $1 }'|head -1) -n istio-system

#curl -v -HHost:spring-boot-rest-client.example.ru -Hcustom-rl-header:val1 http://192.168.1.106:31067/pingServer
#curl -v -HHost:spring-boot-rest-client.example.ru http://192.168.1.106:31067/ping