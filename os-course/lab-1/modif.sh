#!/bin/bash

cat students.csv | awk -F ", " -v name=$1 -v sum=0 -v count=0 'name==$2 {sum+=$3; count++} END {print sum / count}'
