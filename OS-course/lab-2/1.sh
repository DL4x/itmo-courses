#!/bin/bash

ps ax -u $USER -o pid,command | sed 1d | awk '{print $1":"$2}' > file
sed -i "1i$(wc -l < file)" file
