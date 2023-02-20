# AltoDemo
### Run the project locally
Android Studio is notorious for start up issues with projects. It is encouraged that you reach out to Aaron if issues outside of the below are encountered on Apple or Intel macbook devices. While application initialization was tested on both, the follow issues is the only that occurred with project several tests, an Issue the gradle Execution, Build, and Deployment Java version.
1. Download the latest Android Studio Release, Electric Eel
2. After building, the project should run but depending on your android studios’ default configuration, you may be required to update the Gradle JDK due to the usage of Jetpack Compose. The error is as follows.
    ```
    The following error indicates “update required”
        A problem occurred configuring root project ‘Alto’Demo’.
            Could not resolve all files for configuration ':classpath'.
            Could not resolve com.android.tools.build:gradle:7.4.0.
        Required by:
            project :  com.android.application:com.android.application.gradle.plugin:7.4.0
            project :  com.android.library:com.android.library.gradle.plugin:7.4.0
    ```
3. The Gradle JDK can be updated by navigating into Android Studio > Preferences > Build, Execution, Deployment > Build Tools > Gradle > under general settings > Select Gradle JDK
4. Select from the list Amazon Corretto version 11.0.15 (This may require download)
5. Run project on any emulator supporting API 26

### Devices Used for testing
It is encouranged you project is reviewed on two devices, consider testing on a Pixel 2 device or any other device mentioned below.

Devices used for testing are what are immediately available in application development. As "Alto" is considered a luxury service, it is likely consumer users will have higher end devices. The dynamic layout design allows for closer composition to the guide, but risks colliding text and fields with smaller devices. The latest device tested is a Pixel 5, which was released in 2020.

- Pixel 4 API 33 Emulator (This application is designed for tall screens such as this)
- Pixel 2 API 27 Emulator (This is a shorter screen, and really pushes the bounds of the Layout)
    - Note that any device with a shorter screen may render content illegible. The dynamic nature of the layout height as designed is responsible for this. Potentially alternatives for layout behavior could be explored.
- Pixel 5 API 33
- Pixel 3 API 33 Emulator

It may be worth mentioning that I do not have a samsung available to test with.

### Development Decisions
These are tracked in a Google Doc originally for tracking open questions with Alto during development. I am tracking here for additional visibility.

- Provided assets are pngs, and extremely Small. Development with Vectors is highly encouraged as they scale with high fidelity with device. Some assets were replaced with material icons or plain texts to the best of the available tools.
- The Mission item indicator was adjusted for accessibility. This includes adding a transparent exterior and replacing indicated color for a darker color.
- Missing Font weight for PX Grotesk results in some text fields not appearing similar to their guide counter parts. This is especially clear with the time text in the "Your Trip" sections at the start and end of the list.
- Initially out of scope, I added an edit note feature to play with compose. Please edit drop off notes, including completely deleting a note! I provided an arbitrary max char limit of 70 to maintain layout integrity on the Pixel 2 device. :)
- Loading states are mocked using for delays with coroutines.
- Material Design indicates that the Disabled states for buttons should be a solid color, instead of an outline. This is a discussion with product and design.
- Compose has powerful theming tools for typography that were not used as the initial set up time is burdensome. The extensive usage of font theming with Text can and would require to be consolidated in a production setting.

### Bug and De-scoped Features
- The navigation for change vibe may halt unexpectedly. It occurred in initial navigation development but I have been unable to reproduce since.
- Error state for failing to load the JSON, with forced failures was in my initial design, but I de-scoped this in favor of testing project start up and repository.

### Final notes
I had a lot of fun putting this together. I hope you enjoy. :) 

