# Workbench Docker Containers

## Alp-Glib

The dockerfile in the `alp-glib` contains the code to create a docker image that uses and installs linux/x86_64 alpine OS, and glib.

## Java-mvn-git

The dockerfile in the `java-mvn-git` folder contains the code to create a docker image that uses and installs java version 8.131, mvn version 3.8.5, git, and clones Workbench.

## Tools

* `Docker:` To create and run images you will need to [install docker](https://docs.docker.com/get-docker/).


## Creating Docker Images

Open the terminal to location of a dockerfile
```md
~/Desktop/WorkBench/Dockerfiles/java-mvn-git> docker build -t (docker username)/java-mvn-git .
```
That will build an image locally called (docker username)/java-mvn-git

## Running a Docker Image

05/05/2022 At time of writing I am currently hosting a docker image on my [personal github](https://github.com/users/viaduct12/packages/container/package/workbench-container). We will be using this image as an example on running a docker image.

* Using the terminal we will run this command:
```md
docker pull ghcr.io/viaduct12/workbench-container:latest
```
* Running the command your terminal will output:

```md
~ > docker pull ghcr.io/viaduct12/workbench-container:latest
latest: Pulling from viaduct12/workbench-container
Digest: sha256:71390cc3bd65d065b5575536de309be68b7d9e1e36edfed5b1c64582c77da5e3
Status: Downloaded newer image for ghcr.io/viaduct12/workbench-container:latest
ghcr.io/viaduct12/workbench-container:latest
```
* The id of the image is necessary. The command to get the id is `docker image ls`
```md
~ > docker image ls
REPOSITORY                              TAG       IMAGE ID       CREATED       SIZE
ghcr.io/viaduct12/workbench-container   latest    d0919924e5ae   4 hours ago   424MB
```
* To run the image the command is `docker run -i -t d0919924e5ae`. If your OS is not linux/amd64 you will receive a WARNING and the terminal will change to `/ #`
```md
4:17:51: law in ~ > docker run -i -t d0919924e5ae
WARNING: The requested image's platform (linux/amd64) does not match the detected host platform (linux/arm64/v8) and no specific platform was requested
/ # 
```

After running `docker run` you'll be able to use terminal commands and because Workbench has already been cloned. You can follow the necessary steps from [workbench_getting_started](../../docs/workbench_getting_started.md) to compile the application

## Extracting the JAR file and Resources

* After building the JAR file, the container id is necessary and the command to get the id is `docker container ls`
```md
~/Desktop> docker container ls
CONTAINER ID   IMAGE          COMMAND     CREATED          STATUS          PORTS     NAMES
c547ea86f492   d0919924e5ae   "/bin/sh"   24 minutes ago   Up 24 minutes             exciting_golick
```

* With the container id, the next command to extract the necessary components are `docker cp c547ea86f492:/WorkBench/WorkbenchProject/target (folder name)`. The folder created from extraction will be located in the directory where the command has been exectued. In the following example the folder will appear in the Desktop.
```md
~/Desktop > docker cp c547ea86f492:/WorkBench/WorkbenchProject/target (folder name)
~/Desktop > ls
(folder name)
```

## Adding Docker Images to Github (under construction)
References:
* [https://www.youtube.com/watch?v=qoMg86QA5P4&ab_channel=BeachcastsProgrammingVideos](https://www.youtube.com/watch?v=qoMg86QA5P4&ab_channel=BeachcastsProgrammingVideos)
* [https://docs.github.com/en/actions/publishing-packages/publishing-docker-images](https://docs.github.com/en/actions/publishing-packages/publishing-docker-images)
* [unauthenticated](https://github.community/t/github-container-registry-ghcr-io-packages-not-appearing-in-webinterface/130077)