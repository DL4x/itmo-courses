#!/bin/bash

function evaluate() {
    result=$(echo $2 $3 | awk '{print $1/$2}')
    echo "Average_Running_Children_of_Parent=$1 is $result"
}

./4.sh
m_sum=0
m_ppid=0
m_count=0
while read -r line;
do
    ppid=$(echo "$line" | awk '{print $3}' | awk -F '=' '{print $2}')
    art=$(echo "$line" | awk '{print $5}' | awk -F '=' '{print $2}')
    if [[ m_ppid -eq ppid ]]; then
        m_count=$(( m_count+1 ))
        m_sum=$(echo $m_sum $art | awk '{print $1+$2}')
    else
        evaluate $m_ppid $m_sum $m_count
        m_count=1
        m_sum=$art
        m_ppid=$ppid
    fi
    echo "$line"
done < file > file_extended
evaluate $m_ppid $m_sum $m_count >> file_extended
