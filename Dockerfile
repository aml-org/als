FROM eclipse-temurin:17-focal

# hadolint ignore=DL3002
USER root

ARG USER_HOME_DIR="/root"

ENV SCALA_VERSION 2.12.13
ENV SBT_VERSION 1.7.1


# Update the repository sources list and install dependencies
RUN apt-get update

RUN apt-get install -y software-properties-common unzip htop rsync openssh-client jq git

# install Java
RUN java -version

RUN mkdir -p /usr/share/man/man1 && \
    apt-get update -y

RUN apt-get install unzip -y && \
    apt-get autoremove -y && \
    apt-get install git -y

# Install Scala
## Piping curl directly in tar
RUN \
  apt-get install curl --assume-yes && \
  curl -fsL http://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /root/ && \
  echo >> /root/.bashrc && \
  echo 'export PATH=~/scala-$SCALA_VERSION/bin:$PATH' >> /root/.bashrc

# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://scala.jfrog.io/artifactory/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  sbt -Dsbt.rootdir=true sbtVersion

VOLUME "$USER_HOME_DIR/.sbt"

# Install nodejs
RUN \
  curl -sL https://deb.nodesource.com/setup_16.x | bash -

RUN \
  apt-get install nodejs --assume-yes

RUN export NODE_OPTIONS=--max_old_space_size=6000

RUN npm i -g npm@8

ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
ENV JAVA_TOOL_OPTIONS -Dfile.encoding=UTF8

# Final user and home config
RUN useradd --create-home --shell /bin/bash jenkins
USER jenkins
WORKDIR /home/jenkins
