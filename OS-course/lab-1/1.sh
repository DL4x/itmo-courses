#!/bin/bash

[[ $1 -ge $2 ]] && max1=$1 || max1=$2
[[ $3 -ge  $max1 ]] && echo "$3" || echo "$max1"
