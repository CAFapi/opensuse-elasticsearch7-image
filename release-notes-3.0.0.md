!not-ready-for-release!

#### Version Number
${version-number}

#### New Features
- **SCMOD-9841**: Updated to Elasticsearch version [7.7.0](https://www.elastic.co/guide/en/elasticsearch/reference/current/release-notes-7.7.0.html)

#### Known Issues
- None

#### Breaking Changes
- **SCMOD-8516**: Extend the security hardening of Java base images by disabling TLS algorithms mentioned [here](https://github.com/CAFapi/opensuse-java8-images/blob/develop/src/main/docker/disableWeakTlsAlgorithms.patch)
- **SCMOD-9780**: Updated to Java 11
