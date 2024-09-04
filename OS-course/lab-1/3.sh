#!/bin/bash

echo "Choose option:
  Option 1. Editor nano
  Option 2. Editor vi
  Option 3. Browser links
  Option 4. Quit"
while true; do
	read COMMAND
	case $COMMAND in
		"1")
			nano
			;;
		"2")
			vi
			;;
		"3")
			links
			;;
		"4")
			break
			;; 
		*)
			echo "Unexpected option"
			;;
	esac
done
