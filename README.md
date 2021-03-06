``` 
____  _____ ____ _   _ ____  ___ _______   __    ____   ___  __  __         _    _   _    _    _  __   ____________ 
/ ___|| ____/ ___| | | |  _ \|_ _|_   _\ \ / /   | __ ) / _ \|  \/  |       / \  | \ | |  / \  | | \ \ / /__  / ____|
\___ \|  _|| |   | | | | |_) || |  | |  \ V /____|  _ \| | | | |\/| |_____ / _ \ |  \| | / _ \ | |  \ V /  / /|  _|  
___) | |__| |___| |_| |  _ < | |  | |   | |_____| |_) | |_| | |  | |_____/ ___ \| |\  |/ ___ \| |___| |  / /_| |___
|____/|_____\____|\___/|_| \_\___| |_|   |_|     |____/ \___/|_|  |_|    /_/   \_\_| \_/_/   \_\_____|_| /____|_____|
```

三方组建依赖分析，依赖于nebula graph做深度索引

## Introduction
系统在19年年底完成demo编写，在log4j2安全应急中发现三方组建、安全资产侧的多方面缺陷，春节期间把demo重新做部分完善。
目前主要针对java-maven做分析，三种方式
- jar
- pom.xml
- maven plugin

数据存储
- nebula

## Install
### kafka
- https://kafka.apache.org/downloads
- create topic
```shell
./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic security_sca_pom
```
- start kafka and zk
```shell
./bin/zookeeper-server-start.sh config/zookeeper.properties
./bin/kafka-server-start.sh config/server.properties
./bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic security_sca_pom --from-beginning
./bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic security_sca_jar_pom --from-beginning
```

### nebula 
- 可视化使用:nebula-graph-studio-v3
- nebula docker:https://github.com/vesoft-inc/nebula-docker-compose.git

### mvn plugin
```shell
cd security-bom-analyze-maven-plugin
mvn install clear
```

## architecture
### service
![](docs/service.png)
### nebula sql
- graph/maven.sql

![](docs/graph.png)

## achievement
查看jar包依赖junit.junit\animal-sniffer-annotations\jsr305
![](docs/result.png)
查看Git对应的依赖
![](docs/git_result.png)

## Thanks
- [SnowFlake](https://github.com/beyondfengyu/SnowFlake/blob/master/SnowFlake.java)  
- [Maven](https://maven.apache.org/plugins/index.html)
- [Nebula Graph Database](https://docs.nebula-graph.com.cn/2.5.1/3.ngql-guide/1.nGQL-overview/1.overview/)