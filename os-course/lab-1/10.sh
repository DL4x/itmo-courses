#!/bin/bash

man bash | tr -s ' ' '\n' | grep -x ".\{4,\}" | sort | uniq -c | sort -r | awk '{print $2, $1}' | sed -n '1,3p'
