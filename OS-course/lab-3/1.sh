#!/bin/bash

mkdir ~/test && echo "catalog test was created successfully" >> ~/report && touch ~/test/"$(date +%F_%T)"
ping -c 1 www.net_nikogo.ru || echo "$(date +%F_%T) ping error" >> ~/report
