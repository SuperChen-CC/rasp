FROM ubuntu:latest

WORKDIR /data/install

COPY ./bin /data/install/

RUN tar -zxvf OpenJDK8U-jdk_x64_linux_hotspot_8u392b08.tar.gz -C /data/install/

ENV JAVA_HOME /data/install/jdk8u392-b08
ENV PATH $PATH:$JAVA_HOME/bin

CMD ["bash"]
