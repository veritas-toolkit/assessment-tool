# System Maintenance Manual
![logo](./.img/logo.png)
## Development

* Web Framework: Vue 2.6.14
* Server: Python 3.6+, Java 8, Spring Boot 2.4.10
* Database: sqlite 3.34.0

## Deployment

### Requirements

* __OS:__ CentOS, Ubuntu, or other Linux
* __CPU / RAM:__ 2 CPU & 2G RAM at least, 4 CPU & 4G RAM __recommended__
* __JRE:__ JRE(Java Runtime Environment) 8 is required.
* __Python:__ Python 3.6+

### Install on Docker (Recommended)

Prerequisites: Docker is required. See [the official installation documentation](https://docs.docker.com/get-docker/).

Get the lasted docker image of veritas-assessment-tool.
```bash
docker pull veritastool/veritas-assessment-tool
```

Run the docker 

This is the simplest setup; just run Veritas Assessment Tool.

```bash
docker run -d \
	-p 8001:8001 \
	--name veritas-assessment-tool \
	veritastool/veritas-assessment-tool:latest
```



We recommend creating volumes for the following directories:

* `/opt/veritas/file`: data files, such as database, uploaded json files and generated images
* `/opt/veritas/config`: config files.
* `/opt/veritas/log`: contains Veritas Assessment Tool logs about access, web process logs

```bash
docker run -d \
	-p 8001:8001 \
	--name veritas-assessment-tool \
	-v $PWD/file:/opt/veritas/file \
	-v $PWD/log:/opt/veritas/log \
	-v $PWD/config:/opt/veritas/config \
	-c 4 \
	-m 4g \
	--stop-timeout 3600 \
	veritastool/veritas-assessment-tool:latest
```
 

### Install on OS

1. Download the veritas assessment tool release package(assessment-tool.zip).
2. Unzip `unzip veritas-assessment-tool.zip`
3. Install python lib
4. Start
```bash
cd veritas-assessment-tool
bin/install.sh
bin/start.sh
```

### Backup files

There are several ways to store data used by applications that run in Docker containers.

If running docker as the simplest method.
Docker manage the storage of your files by writing the files to disk on the host system using its own internal volume management.
This is the default and is easy and fairly transparent to the user. 
The downside is that the files may be hard to locate for tools and applications that run directly on the host system, i.e. outside containers.
```bash
mkdir data
docker cp veritas-assessment-tool:/opt/veritas/file data/file
docker cp veritas-assessment-tool:/opt/veritas/config data/config
docker cp veritas-assessment-tool:/opt/veritas/log data/log
```

If running docker as the recommend method.

Create a data directory on the host system (outside the container) and mount this to a directory visible from inside the container.
This places the database files in a known location on the host system, and makes it easy for tools and applications on the host system to access the files.
The downside is that the user needs to make sure that the directory exists, and that e.g. directory permissions and other security mechanisms on the host system are set up correctly.

```bash
mkdir YOUR_DOCKER_DIR
tar -zcvf vat.tar.gz file/ config/
```

## Admin

### Accessing the Application

Currently, the default user and password is `admin/123456`.

You should change the default password after first login.

You can create some normal users as needed.

## FAQ

### Why use python

The application is a web server, witch implemented by Java & Spring Boot Framework. We use python to generate the plot
