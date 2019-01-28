FROM sgrio/java-oracle:jdk_8

ARG USER_HOME_DIR="/root"

ENV SCALA_VERSION 2.12.2
ENV SBT_VERSION 0.13.16
ENV DEBIAN_FRONTEND noninteractive

# Update the repository sources list and install dependencies
RUN apt-get update

RUN apt-get install -y software-properties-common unzip htop rsync openssh-client jq

# install Java
USER root
RUN mkdir -p /usr/share/man/man1 && \
    apt-get update -y && \
    apt-get install -y openjdk-8-jdk

RUN apt-get install unzip -y && \
    apt-get autoremove -y

# Install Scala
## Piping curl directly in tar
RUN \
  apt-get install curl --assume-yes && \
  curl -fsL http://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /root/ && \
  echo >> /root/.bashrc && \
  echo 'export PATH=~/scala-$SCALA_VERSION/bin:$PATH' >> /root/.bashrc

# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb http://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  sbt sbtVersion

VOLUME "$USER_HOME_DIR/.sbt"

# Install nodejs
RUN \
  curl -sL https://deb.nodesource.com/setup_8.x | bash -

RUN \
  apt-get install nodejs --assume-yes
