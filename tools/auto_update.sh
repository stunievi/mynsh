file="/Users/bin/work/update.zip"
folder="/Users/bin/work/update_temp"
front="/root/hz2/front"
webapp="/root/tomcat/apache-tomcat-8.5.34/webapps"

echo "检查更新文件中......\n"

#判断文件是否存在, 如果存在, 则建立工作目录
if [ ! -f "$file" ]; then
    echo "更新文件不存在, 本次更新略过"
    exit -1
fi

#建立工作目录
if [ -d "$folder" ]; then
    rm -rf "$folder"
fi
mkdir "$folder"

#解压缩
echo "正在解压缩更新包......\n"
unzip -d "$folder" "$file"

#更新webapp
echo "正在复制服务端到tomcat......\n"
mv "$folder/hz-back.war" "$webapp"
echo "正在复制客户端到tomcat......\n"
rm -rf "$front"
mv  "$folder/www" "$front"

#重启tomcat
echo "正在重启tomcat......\n"
"$webapp/../bin/shutdown.sh"
"$webapp/../bin/startup.sh"

#清理文件
echo "正在清理文件......\n"
rm -rf "$folder"
rm -f "$file"

echo "更新完毕\n"

