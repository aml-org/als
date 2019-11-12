FROM artifacts.msap.io/mulesoft/core-paas-base-image-ubuntu:v3.0.200 as base

# hadolint ignore=DL3002
USER root

RUN apt-get update                      \
    && apt-get install -y --no-install-recommends \
            bash                        \
            docker.io                   \
            gcc                         \
            git                         \
            golang                      \
            jq                          \
            libffi-dev                  \
            make                        \
            openssh-client              \
            openssl                     \
            libssl-dev                  \
            python-pip                  \
            python-setuptools           \
            python-wheel                \
            python                      \
            python-dev                  \
            python3                     \
            python3-dev                 \
            python3-pip                 \
            python3-venv                \
            tar                         \
            unzip                       \
            wget                        \
    && apt-get clean                    \
    && rm -rf /var/lib/apt/lists/*

ARG USER_HOME_DIR="/root"

ENV SCALA_VERSION 2.12.10
ENV SBT_VERSION 1.3.3

# Copy the CPC binary from the core-paas-client docker image.
FROM artifacts.msap.io/mulesoft/core-paas-client:v7.3.0 as cpc-base


# Update the repository sources list and install dependencies
RUN apt-get update

RUN apt-get install -y software-properties-common unzip htop rsync openssh-client jq git

# install Java
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

ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
ENV JAVA_TOOL_OPTIONS -Dfile.encoding=UTF8

# Final user and home config
RUN useradd --create-home --shell /bin/bash jenkins
USER jenkins
WORKDIR /home/jenkins