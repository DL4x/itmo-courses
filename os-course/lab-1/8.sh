#!/bin/bash

cat /etc/passwd | awk -F ':' '$3 ~ /^-?[0-9]+$/ {print $1, $3}' | sort -nk2
