# Prototype for the "headache mode" as a mobile Android app
___

This repository contains the source code for an Android app which implements the "headache mode". The idea of "headache mode" is to provide help for migraine and headache patients by providing an app with which the user can rule out any disturbances from his mobile phone related to headache, such as noise or light.
___

This repository was created as part of the "Digital Society School" programme at the University of Applied Sciences Amsterdam.

The goal of this prototype is to show a first proof of concept for a simple app which can be used to showcase the idea of a "headache mode". The "headache mode" is part of the headache diary app and aims to deliver value to the user through simple features such as the headache mode.

The repo contains the Android Studio project of the Android app. As this is only intended as a prototype to show a simple use case of the headache mode, the app was developed for Android API level 23 or higher. Furthermore, the app needs permissions to access e.g. the system settings on an Android phone, to enable features such as turning the notifications off. This is not optimal and needs to be resolved in a (more technical aimed) proof-of-concept.

## Features

### Headache mode

When enabled, the headache mode...
- Dims the brightness of the phone
- Turns off notifications and sounds (and enable "Do not disturb" mode)
- Blocks any incoming calls and send a message (SMS) to the caller, informing him/her that the recipient is not available
- Turns off any connected smart lights (via Philips Hue API)

## Run the project

Install Android Studio, clone & build the project, install it on an Android device of your choice (Android 6.0+ / API level 23+).

When starting the app for the first time, you have to grant multiple permissions to the app.
To use the integration of Philips Hue smart lights, you have to configure the lights via the web API. Usually this means that you have to change the IP address of the Hue bridge in `MainActivity.java`. You can read more about how to integrate Philips Hue in the official [documentation](https://developers.meethue.com/).
