FROM java:openjdk-8-jdk

WORKDIR /usr/bin
RUN curl -sLO https://services.gradle.org/distributions/gradle-2.9-all.zip && \
unzip gradle-2.9-all.zip && \
ln -s gradle-2.9 gradle && \
rm gradle-2.9-all.zip

ENV GRADLE_HOME /usr/bin/gradle
ENV PATH $PATH:$GRADLE_HOME/bin
