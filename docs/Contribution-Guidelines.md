## Development and contributing

### Internal development process
Development is performed by 2-weeks sprints (We use kind of Scrum framework)
We use [Git flow](http://nvie.com/posts/a-successful-git-branching-model/) for development. 

### Definition of Ready
The task is ready to be put into a sprint when all following conditions are met:
* All dependencies are clear and how to work with them are clarified
* Use-case is defined in the task
* Acceptance criteria are defined

### Definition of Done
The Task could be accepted only when following requirements are met:
* Code is reviewed (and approved) by another developer
* API documentation in Swagger UI corresponds to acceptance criteria
* At least one automated test for every Use-case exists

### Contributing
Any person are free to join us by implementing some parts of code or fixing some bugs and making merge requests for them.
The conditions listed in the Definition of Done are required to be fulfilled in any case.

### Technical conditions for the implementations

#### Testing API with Postman json collections
 
 
#### Code styling


#### Java


### How to Release

Release is being done using the [release scripts](https://github.com/borisskert/release-scripts) ( (C) by [Boris Skert](https://github.com/borisskert) ) located under scripts/release-scripts.
For detailed info see [README for release-scripts](../scripts/release-scripts/README.md).

#### Steps to make a release

**Release is made from local copy! Ensure that you have enough rights to push to master and develop branches**
```bash
$ git submodule update --init --remote
$ scripts/release-scripts/release.sh <release-version> <next-develop-version>
``` 
Example
```bash
$ scripts/release-scripts/release.sh 1.0 1.1
```