#!/usr/bin/env bash

if[ -z "$AMF_VERSION" ]
then
  echo "AMF_VERSION EMPTY"
  exit $status
fi

if[ -z "$VALIDATOR_VERSION" ]
then
  echo "VALIDATOR_VERSION EMPTY"
  exit $status
fi

echo replacing AMF_VERSION: $AMF_VERSION
sed -i -e "s/amf=.*/amf=$AMF_VERSION/" dependencies.properties
echo replacing VALIDATOR_VERSION: $VALIDATOR_VERSION
sed -i -e "s/amf.custom-validator-scalajs=.*/amf.custom-validator-scalajs=$VALIDATOR_VERSION/" dependencies.properties
exit $status