kill -9 `lsof -t -i:3101`
kill -9 `lsof -t -i:3102`
kill -9 `lsof -t -i:3103`
kill -9 `lsof -t -i:3104`
kill -9 `lsof -t -i:3105`
kill -9 `lsof -t -i:3106`

nohup java -jar -Dspring.config.location=/conf/hy2001.yml hz-back.jar >> /dev/null 2>&1 &
nohup java -jar -Dspring.config.location=/conf/hy2002.yml hz-back.jar >> /dev/null 2>&1 &
nohup java -jar -Dspring.config.location=/conf/hy2003.yml hz-back.jar >> /dev/null 2>&1 &
nohup java -jar -Dspring.config.location=/conf/hy2004.yml hz-back.jar >> /dev/null 2>&1 &
nohup java -jar -Dspring.config.location=/conf/hy2005.yml hz-back.jar >> /dev/null 2>&1 &
nohup java -jar -Dspring.config.location=/conf/hy2006.yml hz-back.jar >> /dev/null 2>&1 &

echo success
