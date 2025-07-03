#!/bin/bash

cat /var/log/*.log | wc -l

# Насколько практичнее испольновать find, так как у утилиты cat нет рекурсивного обхода?
# find /var/log -name "*.log" -depth 1 -exec wc -l {} \; | awk '{k += $1} END {print k}'
