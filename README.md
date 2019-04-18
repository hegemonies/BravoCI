# BravoCI
Perfect Continuous Integration System

Our CI perfect working on ubuntu 18.04.2 with jdk11 + gradle 5.2.1 and mongodb 4.0.8 and docker 18.09.2

# How to start the program
```
$ gradle build
$ gradle bootRun
```

# Supported languages
1. Java (gradle)

# What needed to testing
The project under test should be located in the public github repository, which should contain the configuration of the target system and a list of steps to run the tests.
Example:
```
{
    "config": {
      "compiler": "gradle",
      "targetFile": ".jar"
    },
    "steps": [
      {
        "title": "Step1",
        "cmd": "gradle build"
      },
      {
        "title": "Step2",
        "cmd": "gradle run"
      }
    ]
}
```