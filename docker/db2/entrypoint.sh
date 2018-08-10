#!/bin/bash
if [ ! -d "/home/db2inst1" ]; then
    ./createInstance
    su - db2inst1 -c "db2start"
    su - db2inst1 -c "db2 CREATE db test USING CODESET UTF-8 TERRITORY CN"
else
    su - db2inst1 -c "db2start"
fi
nohup /usr/sbin/sshd -D 2>&1 > /dev/null &
while true; do sleep 1000; done
