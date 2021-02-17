#
# Copyright 2019-2020 Micro Focus or one of its affiliates.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Docker registry
ARG DOCKER_HUB_PUBLIC=docker.io

# Reference the official Elasticsearch 7 image
FROM docker.elastic.co/elasticsearch/elasticsearch-oss:7.7.0 AS es7-image

#
# The actual image definition
#
FROM ${DOCKER_HUB_PUBLIC}/cafapi/opensuse-jre11:3

RUN zypper -n refresh && \
    zypper -n update

ENV ELASTIC_CONTAINER=true

COPY --from=es7-image /etc/pki/ca-trust /etc/pki/ca-trust

# Install packages
RUN zypper -n install which hostname netcat-openbsd unzip wget
RUN zypper -n clean --all

RUN mkdir -p /usr/share/elasticsearch
# Create group, user and set access rights to the elasticsearch base dir
RUN groupadd -g 1000 elasticsearch && \
    useradd -u 1000 -g 1000 -G 0 -d /usr/share/elasticsearch elasticsearch && \
    chmod 0775 /usr/share/elasticsearch && \
    chgrp 0 /usr/share/elasticsearch

WORKDIR /usr/share/elasticsearch

COPY --from=es7-image --chown=1000:0 /usr/share/elasticsearch/ /usr/share/elasticsearch/
RUN ES_JAVA_OPTS="$(echo ${https_proxy} | sed -E 's/^.*:\/\/(.*):([0-9]*).*/-Dhttps.proxyHost=\1 -Dhttps.proxyPort=\2/')" \
    /usr/share/elasticsearch/bin/elasticsearch-plugin install analysis-icu

# Configure logging
COPY log4j2.properties /usr/share/elasticsearch/config
COPY /maven/*.jar /usr/share/elasticsearch/lib/

ENV PATH=/usr/share/elasticsearch/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin

COPY --from=es7-image --chown=1000:0 /usr/local/bin/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chgrp 0 /usr/local/bin/docker-entrypoint.sh && \
    chmod g=u /etc/passwd && \
    chmod 0775 /usr/local/bin/docker-entrypoint.sh

RUN rm -rf /usr/share/elasticsearch/jdk

COPY scripts/ /opt/elasticsearch/scripts/
RUN chmod +x /opt/elasticsearch/scripts/*

# Startup Elasticsearch and expose ports
EXPOSE 9200 9300
CMD /opt/elasticsearch/scripts/start.sh
