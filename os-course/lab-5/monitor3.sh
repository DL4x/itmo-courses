#!/bin/bash

for ((i=0; i<30; i++))
do
    ./newmem.sh 5400000 &
    sleep 1
done
