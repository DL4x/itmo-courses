#!/bin/bash

for ((i=0; i<25000; i++));
do
    read line
    echo "$(($line * 2))" >> file$1
done < file$1
