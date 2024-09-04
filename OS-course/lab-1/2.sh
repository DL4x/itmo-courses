#!/bin/bash

result=""
while true; do
	read STR
	if [[ "$STR" == "q" ]]; then
		break
	else
		result+="$STR"	
	fi
done
echo "$result"
