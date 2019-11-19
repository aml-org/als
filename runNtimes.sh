#! /bin/bash
for((A=0;A<$1;A++))
do
  echo "time $A"
  sbt -mem 4096 serverJVM/test
done
