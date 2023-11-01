### 1.注意事项

1.1 操作系统建议Centos7.6，内存大小16G+

1.2 内存大小请调整.env文件，默认内存大小16G。不建议版本升级直接修改.env文件,可能导致新版本功能无法使用

1.3 部署完成后出现404页面，请重启重启所有docker-compose yml

### 2.环境准备

2.1 安装docker 和docker-compose （如已经安装可以跳过）







```
curl -C- -O --retry 3 https://download.bctools.cn/jvs-docker && chmod a+x jvs-docker && ./jvs-docker -I
```


### 3.部署项目

3.1 目录授权
```
 cd docker-compose && chmod -R 777 data && chmod -R 644 data/mysqlcnf
```
3.2 替换服务器ip地址

请将${ip}修改为本地服务器IP

```
sed -i -e 's/jvs-minio/${ip}/g'  ./mysql/nacos.sql  
sed -i -e 's/file.preview.bctools.cn/${ip}/g'  ./mysql/nacos.sql
sed -i -e 's/documentIpAddress/${ip}/g'  ./mysql/nacos.sql  
sed -i -e 's/documentIpAddress/${ip}/g'  ./data/jvs-knowledge-plugins/config.js 
sed -i -e 's/documentIpAddress/${ip}/g'  ./data/jvs-teamwork-plugins/config.js 
```
例: 


sed -i -e 's/jvs-minio/114.114.114.114/g' ./mysql/nacos.sql  
sed -i -e 's/file.preview.bctools.cn/114.114.114.114/g' ./mysql/nacos.sql  
sed -i -e 's/documentIpAddress/114.114.114.114/g' ./mysql/nacos.sql  
sed -i -e 's/documentIpAddress/114.114.114.114/g' ./data/jvs-knowledge-plugins/config.js  
sed -i -e 's/documentIpAddress/114.114.114.114/g'  ./data/jvs-teamwork-plugins/config.js


3.3 部署数据库


```
docker-compose -f docker-compose-db.yml pull
docker-compose -f docker-compose-db.yml up jvs-mysql
```

初始化数据库的时候比较慢和主机性能相关，出现如图
![输入图片说明](https://gitee.com/software-minister/jvs-docker-compose/raw/2.1.6/img/image%E5%88%9D%E5%A7%8B%E5%8C%96%E6%95%B0%E6%8D%AE%E5%BA%93.png)

效果就可以 Ctrl+C 退出当前操作；继续下面的操作


```
docker-compose -f docker-compose-db.yml  up -d
docker-compose -f docker-compose-db.yml  up -d createbuckets
```
查看日志是否报错   
`docker-compose -f docker-compose-db.yml logs -f`


3.4 查看nacos


程序依赖nacos 请保证能正常访问nacos后再执行应用部署

```
curl -X GET   'http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=jvs-gateway&group=jvs&tenant=jvs'
```


###  4.hosts 配置  
Windows hosts 路径：
```
C:\Windows\System32\drivers\etc  
```
配置本地hosts（127.0.0.1替换为正确的IP（下列服务启动在哪里就配置哪里的ip））：
```
127.0.0.1 jvs-xxl-job-admin
127.0.0.1 jvs-nacos
127.0.0.1 jvs-mongo
127.0.0.1 jvs-redis
127.0.0.1 jvs-mysql
127.0.0.1 jvs-minio
127.0.0.1 elasticsearch
127.0.0.1 rabbitmq
127.0.0.1 jvs-auth
127.0.0.1 gateway
127.0.0.1 jvs-auth-mgr
```

###  5访问地址  
5.1 账号密码  
帐号：admin 密码: 123456

5.2 访问后台（含开发套件）
​​http://localhost:8088​​

5.3 nacos地址
​​http://localhost:8848/nacos​​

帐号：nacos 密码: nacos


