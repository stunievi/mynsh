Docker rmi -f $(docker images -a|grep none|awk '{print $3}')

