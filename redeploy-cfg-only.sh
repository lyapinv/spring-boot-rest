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
OS_NAMESPACE2=tutorial2
SERVER_APPLICATION_NAME=spring-boot-rest-server
SERVER_APPLICATION_PATH=spring-boot-rest-server

# Deploy clients intermediate container
oc apply -f network-tools-deployment.yaml -n $OS_NAMESPACE

# Ingress config for Client app
oc apply -f ingress-cfg.yaml -n $OS_NAMESPACE

# Egress config for Client app
oc apply -f egress-cfg.yaml -n $OS_NAMESPACE2