#!/bin/bash
SECRET=githuboauth
SECRETFILE=secrets.txt

if [ -f ${SECRETFILE} ]; then
  kubectl get secret $SECRET |grep $SECRET 2>&1 >/dev/null
  rc=$?
  
  if [ $rc -eq 0 ]; then
    echo "Deleting existing secret ${SECRET}"
    kubectl delete secret $SECRET
  fi
  KUBECMD="kubectl create secret generic ${SECRET}"
  for LINE in $(cat ${SECRETFILE})
  do
    KUBECMD=$KUBECMD" --from-literal=${LINE}"
  done
  $(echo $KUBECMD)
  exit 0
else
  echo "You need to create a local ${SECRETFILE} file."
  exit 1
fi