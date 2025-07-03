#!/bin/bash

ps ax -u $USER -o pid,stat | grep "Z" | awk '{print $1}'
