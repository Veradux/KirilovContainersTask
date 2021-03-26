## Table of contents
* [Kirilov Containers Task](#kirilov-containers-task)
* [Application Description](#application-description)
* [Demployment](#deployment)
* [Major-Dependencies](#major-dependencies)
* [To be improved](#to-be-improved)

# Kirilov Containers Task
A task used for evaluating the skills of the author (me) in mobile development.

## Application Description
An Android application developed with Kotlin, used for tracking waste containers by their locations and filling level. Provides an interactible map to display containers and the ability to edit them. Includes Bluetooth Low Energy connectivity for nearby Low Energy devices.

## Deployment
* Download Android Sdk.
* Add environment variable with value set to the directory of ...Android\Sdk\platform-tools
* Connect phone to PC.
* Enable USB Debugging on phone.
* In the directory of the sent .apk, run in terminal `adb install KirilovContainersTask.apk`

## Major Dependencies
* `hilt-android`
* `mapbox-android-sdk`
* `kotlinx-coroutines`
* `klaxon`

## To be improved
Aspects of the application which could be done better outside of the time constraints for the test.
* Abstraction in fragments in the form of an architecture.
* Permissions management.
* Error handling, in cases such as services not providing the requested data.
* Testing, both unit and UI. 
* More specifically follow specifications for the BLE implementation, which requests automatic scanning. The current implementation requires manual scanning through the tap of a button, but despite that it includes BLE device pairing.
