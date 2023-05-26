FROM docker.io/ubuntu:23.04
# https://github.com/kncept-oauth/simple-oidc
# DEBUGGING: docker build -f .devcontainer/ubuntu.Dockerfile -t ubuntu-dev . && docker run -it ubuntu-dev bash

# consider lscr.io/linuxserver/code-server:latest

RUN apt-get update
RUN apt-get -y install sudo wget curl vim git

# Locale injector - if needed
# RUN \
    # echo LANGUAGE=en_US.UTF-8 >> /etc/environment && \
    # echo LC_ALL=en_US.UTF-8 >> /etc/environment && \
    # echo LANG=en_US.UTF-8 >> /etc/environment && \
    # echo LC_CTYPE=en_US.UTF-8 >> /etc/environment


# Java Openjdk install
ARG OPENJDK_VERSION=17
RUN apt install openjdk-${OPENJDK_VERSION}-jdk openjdk-${OPENJDK_VERSION}-jre


# User
RUN usermod -aG sudo ubuntu
RUN echo "ubuntu:ubuntu" | chpasswd
USER ubuntu
WORKDIR /home/ubuntu

# install NVM and node tools
ARG NODE_VERSION=18
RUN \
    curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.3/install.sh | bash
RUN bash -c 'source .nvm/nvm.sh && npm install -g ts-node'
# known location for the current node version bin tools
RUN ln -s /home/ubuntu/.nvm/versions/node/v${NODE_VERSION}* /home/ubuntu/.local_node


