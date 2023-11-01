FROM registry.cn-hangzhou.aliyuncs.com/glg/sky-agent:8.8.0
MAINTAINER guojing <13594163317@163.com>

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone
ADD ./target/jvs-auth.jar /app/app.jar
ENV skyname="jvs-auth"
ENV JAVA_OPTS=""
ENV skyip="localhost:11800"
ENV authentication=""
ENV nacosAddr="cloud-nacos:8848"
ENV namespace=""
ENTRYPOINT ["sh","-c","java -javaagent:/skywalking-agent/skywalking-agent.jar  -Dskywalking.agent.service_name=$skyname -Dskywalking.agent.authentication=$authentication -Dskywalking.collector.backend_service=$skyip -Dspring.cloud.nacos.discovery.server-addr=$nacosAddr -Dspring.cloud.nacos.discovery.namespace=$namespace  $JAVA_OPTS -jar /app/app.jar"]

