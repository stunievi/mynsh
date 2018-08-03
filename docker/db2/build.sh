rm -rf data
mkdir data
Docker rmi -f $(docker images -a|grep none|awk '{print $3}')
docker build . --rm -t cubi
docker run -it --privileged=true cubi
