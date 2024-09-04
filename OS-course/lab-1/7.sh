#!/bin/bash

pattern="[[:alnum:]._-]+"
sudo grep -shoE "$pattern@$pattern.$pattern" /etc/* | uniq | awk -v ORS=', ' '{print $1}' | sed 's/, $//' > emails.lst
