#!/bin/bash

mvn clean package

BASEDIR=$(dirname "$0")
TARGETDIR=~/Work/IdeaProjects/dso/api-gateway-bitbucket/apigw_big-file-download
echo "$BASEDIR"

#cd $BASEDIR
#mkdir -p $TARGETDIR/target
#cp spring-boot-rest-client/target/*.jar $TARGETDIR/target
#cp spring-boot-rest-client/Dockerfile-nexus-ci $TARGETDIR/Dockerfile
#cp -r uploads $TARGETDIR/uploads
#cp -r certs $TARGETDIR/certs
#cd $TARGETDIR
#git add .
#git commit -m "Add binary for client"
#git push origin master


# https://teamcity-cicdl.corp.dev.vtb/buildConfiguration/USBP_ServiceMeshIstio_Build_BuildDockerImageAndPushToNexus?mode=builds#all-projects
# big-file-download-client
# usbp/apigw_big-file-download
# latest

#Имя приложения = Имя которое будет в репозитории докера
#Проект в Bitbucket = usbp/${название репозитория в битбакете}
#Версия приложения = какая-нибудь версия( можно latest)


#cd $BASEDIR
#mkdir -p $TARGETDIR/target
#cp spring-boot-rest-server/target/*.jar $TARGETDIR/target
#cp spring-boot-rest-server/Dockerfile-nexus-ci $TARGETDIR/Dockerfile
#cp -r uploads $TARGETDIR/uploads
#cp -r certs $TARGETDIR/certs
#cd $TARGETDIR
#git add .
#git commit -m "Add binary for server"
#git push origin master


OS_NAMESPACE=dso-api-gateway
CLIENT_APPLICATION_PATH=spring-boot-rest-client
SERVER_APPLICATION_PATH=spring-boot-rest-server
oc login --token=eyJhbGciOiJSUzI1NiIsImtpZCI6IlN2UDZiWk5DcmRGaU1zNE1nR21nN1B4Q290NDBLVDF6d0s0Yy1fQjQwNWsifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkc28tYXBpLWdhdGV3YXkiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlY3JldC5uYW1lIjoiZHNvLWFwaS1nYXRld2F5LWRlcGxveS1zdmMtdG9rZW4tZDY1bjgiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZHNvLWFwaS1nYXRld2F5LWRlcGxveS1zdmMiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJjZTIzOTU0Yy1kZmEyLTRjY2ItYmVmMy03NDVmMzU3ZWFhZWUiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6ZHNvLWFwaS1nYXRld2F5OmRzby1hcGktZ2F0ZXdheS1kZXBsb3ktc3ZjIn0.HaLb1l9Htr5WVUxWy8aPalbd91n5JfQ4LCmUAIX1rLg3zJBBEX59jrUL4X-QpcemL5mbc4yKO9RmyEGS52rq-BuVkOT9CmivXMKRDsnbT6aY_HIIq4dcDyW-0O6urgsKi6_PDJ_zkEonEecDt5j2Z5B-VYo9udF_zBeeymmP9jEHPNqQf1MzmmabSMiLOQzpLgePWpvBxlCfWhsQ8-ieAAPRPfit61sM6WT7DKBrHt0Fu5Upccvh7c25e6F4ZOqVn3QXGvG-YtgDpDwEImL6DvzdhaMMmI6Jw32bM06F3Or5YIsNCUwaM9uArlQXkezdS-z-6a1Dk6HbWy5OW7OPeA --server=https://api.d0-oscp.corp.dev.vtb:6443 --certificate-authority ~/Downloads/api-d0-oscp-corp-dev-vtb.pem
oc apply -f $CLIENT_APPLICATION_PATH/client-deployment.yaml -n $OS_NAMESPACE
oc apply -f $SERVER_APPLICATION_PATH/server-deployment.yaml -n $OS_NAMESPACE
oc apply -f ingress-cfg.yaml -n $OS_NAMESPACE

# curl -v http://big-file-download-client-dso-api-gateway.apps-crc.testing:8081/downloadFile/one_and_half_mb.png/1/1
# curl -v http://big-file-download-client-dso-api-gateway.apps.d0-oscp.corp.dev.vtb/downloadFile/one_and_half_mb.png/1/1