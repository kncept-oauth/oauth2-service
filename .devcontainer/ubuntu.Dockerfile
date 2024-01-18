FROM docker.io/ubuntu:22.04
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
RUN apt install -y openjdk-${OPENJDK_VERSION}-jdk openjdk-${OPENJDK_VERSION}-jre


# user 'ubuntu' exists in 23 but not in 22
RUN useradd -m -s /bin/bash ubuntu
# User
RUN usermod -aG sudo ubuntu
RUN echo "ubuntu:ubuntu" | chpasswd
USER ubuntu
WORKDIR /home/ubuntu

# install NVM and node tools
ARG NODE_VERSION=18
ARG NVM_SH_VERSION=v0.39.7
RUN \
    curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/${NVM_SH_VERSION}/install.sh | bash
RUN bash -c 'source .nvm/nvm.sh && npm install -g ts-node'


