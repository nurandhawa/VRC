FROM java:openjdk-8-jdk

# Gradle setup
WORKDIR /usr/bin
RUN curl -sLO https://services.gradle.org/distributions/gradle-2.9-all.zip && \
unzip gradle-2.9-all.zip && \
ln -s gradle-2.9 gradle && \
rm gradle-2.9-all.zip

ENV GRADLE_HOME /usr/bin/gradle
ENV PATH $PATH:$GRADLE_HOME/bin
ENV TESTING im_just_here_so_that_the_value_is_not_null

# Node installation
# This uses the method shown in the official Node Dockerfile
# https://github.com/nodejs/docker-node/blob/466e418a117f33c1cd550414ae1b39423319a265/6.2/Dockerfile
RUN set -ex \
    && for key in \
        7937DFD2AB06298B2293C3187D33FF9D0246406D \
        114F43EE0176B71C7BC219DD50A3051F888C628D \
    ; do \
        gpg --keyserver ha.pool.sks-keyservers.net --recv-keys "$key"; \
    done

ENV NODE_VERSION 0.12.7
ENV NPM_VERSION 2.14.1

RUN curl -SLO "https://nodejs.org/dist/v$NODE_VERSION/node-v$NODE_VERSION-linux-x64.tar.gz" \
    && curl -SLO "https://nodejs.org/dist/v$NODE_VERSION/SHASUMS256.txt.asc" \
    && gpg --verify SHASUMS256.txt.asc \
    && grep " node-v$NODE_VERSION-linux-x64.tar.gz\$" SHASUMS256.txt.asc | sha256sum -c - \
    && tar -xzf "node-v$NODE_VERSION-linux-x64.tar.gz" -C /usr/local --strip-components=1 \
    && rm "node-v$NODE_VERSION-linux-x64.tar.gz" SHASUMS256.txt.asc \
    && npm install -g npm@"$NPM_VERSION" \
    && npm cache clear

# Grunt installation
RUN npm install -g grunt-cli
