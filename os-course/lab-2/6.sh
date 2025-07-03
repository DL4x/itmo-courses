#!/bin/bash

m_pid=0
m_max=0
pids=$(ls /proc | grep -E "^[[:digit:]]+$")
for pid in $pids
do
    size=$(grep -sE "VmSize" "/proc/$pid/status" | awk '{print $2}')
	[[ $size -gt $m_max ]] && m_pid=$pid && m_max=$size 
done
echo "$m_pid $m_max kB"
