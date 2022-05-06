# Uses an image that has linux/x86_64 alpine and glib installed
FROM lfiesta/alp-glib

# Sets the enviroment variables for JAVA_HOME and PATH
ENV JAVA_HOME /usr/java
ENV PATH  $PATH:$JAVA_HOME/bin

# Creates an enviroment variables for the java version 
ENV JAVA_VERSION jdk-8u131-linux-x64
ENV JAVA_FOLDER jdk1.8.0_131
# Installs java version 8.131
RUN  wget -c --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.tar.gz && \
    tar zxvf $JAVA_VERSION.tar.gz && \
    rm $JAVA_VERSION.tar.gz && \
    mv $JAVA_FOLDER /usr/java

# Creates an enviroment variable for maven and adds maven to the PATH
ENV MAVEN_VERSION 3.8.5
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH $MAVEN_HOME/bin:$PATH
# Installs maven version 3.8.5, installs git, and clones the Workbench repo
RUN wget https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    tar zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    rm apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    mv apache-maven-$MAVEN_VERSION /usr/lib/mvn && \
    apk add git && \
    git clone https://github.com/UWB-Biocomputing/WorkBench.git
