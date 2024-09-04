#!/bin/bash

for ((i=1; i<=$1; i++));
do
    ./write.sh $i &
done
wait
