(
    sleep 1;
    echo task -c -n 1~16 -g;
    sleep 10;
)|telnet 127.0.0.1 5461

