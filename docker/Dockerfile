FROM centos:7 AS build

ENV GOSU_VERSION=1.14
RUN gpg --keyserver hkps://keys.openpgp.org --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4 \
    && curl -o /usr/local/bin/gosu -SL "https://github.com/tianon/gosu/releases/download/${GOSU_VERSION}/gosu-amd64" \
    && curl -o /usr/local/bin/gosu.asc -SL "https://github.com/tianon/gosu/releases/download/${GOSU_VERSION}/gosu-amd64.asc" \
    && gpg --verify /usr/local/bin/gosu.asc \
    && rm /usr/local/bin/gosu.asc \
    && rm -r /root/.gnupg/ \
    && chmod +x /usr/local/bin/gosu \
    && gosu nobody true

RUN cd /tmp &&\
    yum groupinstall -y "Development Tools" &&\
    curl -O https://www.sqlite.org/2021/sqlite-autoconf-3360000.tar.gz &&\
    tar -zxvf sqlite-autoconf-3360000.tar.gz &&\
    mv sqlite-autoconf-3360000 sqlite &&\
    cd sqlite &&\
    ./configure &&\
    make -j 2 &&\
    chmod +x sqlite3 &&\
    echo "Compile sqlite success."

COPY target/veritas-assessment-tool.tar.gz /tmp/

RUN mkdir -p /opt/veritas/ &&\
    tar -zxvf /tmp/veritas-assessment-tool.tar.gz -C /opt/veritas/

COPY docker/*.sh /opt/veritas/bin/

RUN chmod u+x /opt/veritas/bin/*.sh


FROM centos:7

#ENV JAVA_VERSION="jdk8u292-b10" \
ENV LANG='en_US.UTF-8' \
    LANGUAGE='en_US:en' \
    LC_ALL='en_US.UTF-8' \
    PYTHON_VERSION=3.8.13

RUN groupadd -g 1000 veritas &&\
    useradd -d /home/veritas -u 1000 -g veritas -G root veritas &&\
    #yum install -y python3 java-1.8.0-openjdk &&\
    yum install -y java-1.8.0-openjdk &&\
    cd /tmp &&\
    yum install -y make gcc openssl-devel bzip2-devel libffi-devel zlib-devel wget &&\
    wget https://www.python.org/ftp/python/${PYTHON_VERSION}/Python-${PYTHON_VERSION}.tgz &&\
    tar xzf Python-${PYTHON_VERSION}.tgz &&\
    cd Python-${PYTHON_VERSION} &&\
    ./configure --enable-optimizations &&\
    make install &&\
    cd /tmp &&\
    rm -rf Python-${PYTHON_VERSION}.tgz Python-${PYTHON_VERSION} && \
    find /usr/local -depth \
            \( \
                \( -type d -a \( -name test -o -name tests -o -name idle_test \) \) \
                -o \( -type f -a \( -name '*.pyc' -o -name '*.pyo' -o -name 'libpython*.a' \) \) \
                -o \( -type f -a -name 'wininst-*.exe' \) \
            \) -exec rm -rf '{}' + \
    ; \
    yum autoremove -y make gcc openssl-devel bzip2-devel libffi-devel zlib-devel wget &&\
    yum clean all

# copy file
COPY --from=build --chown=veritas:veritas /opt/veritas /opt/veritas
COPY --from=build /usr/local/bin/gosu /usr/local/bin/gosu
COPY --from=build /tmp/sqlite/sqlite3 /usr/local/bin/sqlite3

# install application
WORKDIR /opt/veritas
RUN cp -r /opt/veritas/config /opt/veritas/.default_config &&\
    bin/install.sh &&\
    echo "Success"
    #chmod +x /usr/local/bin/gosu &&\

# for running
WORKDIR /opt/veritas
EXPOSE 8001
VOLUME ["/opt/veritas/config", "/opt/veritas/log", "/opt/veritas/file"]
ENTRYPOINT ["bin/docker-entrypoint.sh"]