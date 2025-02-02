<img src="https://spec.edmcouncil.org/fibo/htmlpages/master/latest/img/logo.66a988fe.png" width="150" align="right"/>

# FIBO Viewer

FIBO Viewer is an open-source project that is hosted by EDM Council. The project started in May 2019. FIBO Viewer is a Java application that is specifically designed to access both the FIBO structure and its content in the easiest possible way. FIBO Viewer servers both as a web application and REST API.


## FIBO website
FIBO Viewer is integrated with FIBO website. It resolves the FIBO IRIs. See e.g.:

* https://spec.edmcouncil.org/fibo/ontology/BE/LegalEntities/LegalPersons/LegalEntity


# How to run FIBO Viewer

To run the FIBO Viewer locally: 

* Download the file named "fibo\_viewer\_relase.zip" from the [latest release](https://github.com/edmcouncil/fibo-viewer/releases). 
* Unzip the file. 
* In the command prompt of your operating system run the following command in the folder with the last release: 

```
java -jar app-v-LAST_VERSION_NUMBER.war
```
e.g.,

```
java -jar app-v-0.1.0.war
```

# Contributing
Please read [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.


# Development

To run integration tests, use the following command:

```shell
mvn -P integration-tests verify
```


# License
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)


# Release notes

Please read [CHANGELOG.md](CHANGELOG.md) for details.
