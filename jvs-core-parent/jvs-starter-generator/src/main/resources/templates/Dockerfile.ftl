FROM registry.cn-hangzhou.aliyuncs.com/glg/sky-agent:8.0.1
MAINTAINER guojing <13555555555@163.com>

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone
ADD ./target/${moduleName}-mgr.jar /app/app.jar
ENV skyname="${moduleName}-mgr"
ENV JAVA_OPTS=""
ENV skyip="localhost:11800"
ENV nacosAddr="jvs-nacos:8848"
ENV namespace="jvs"
ENTRYPOINT ["sh","-c","java -javaagent:/skywalking-agent/agent/skywalking-agent.jar -Dskywalking.agent.service_name=$skyname -Dskywalking.collector.backend_service=$skyip -Dspring.cloud.nacos.discovery.server-addr=$nacosAddr -Dspring.cloud.nacos.discovery.namespace=$namespace  $JAVA_OPTS -jar /app/app.jar"]

