<b><h3>Mobile-Automation Framework :</h3></b>

Mobile-Automation framework is an open-source framework for writing automated tests for 
android client.

This framework is designed for the automation expert in such way that it will provide
all the tools and infrastructure that is needed in order for him to build automated tests for android platform.



<b><h3>The Mobile-Automation framework is comprised of the following components:</h3></b>

- AdbController - exposes interfaces for the adb provided by Google , via USB or WIFI connection
- Mobile-Client - this will be the main interface that will exposed for the user in order for him to write tests
- tcpServer - an apk that will bridge the client's commands to robotium server component
- RobotiumServer - an apk that will execute the client's commands on the AUT (application under test)



<b><h4>Prerequisits :</h4></b> 
download and install android sdk and the eclipse plugin. 
install maven.
verify the AUT's package and main activity .


<b><h3>How To Work with the Framework :</h3></b>
- Download the entire mobile automation repository
- Import all the projects into eclipse , and compile all the maven projects.
(you might need to drop the tcp-communication jar into the robotium-server and tcpServer libs)
- Change the manifest of the robotium-server application in such way that the targetPackage attribute of the instrumentation elements will contain the main activity full class of the AUT.
- Install the apks on the device.
- Run manually the tcpServer apk.
- Run the mobile client code you wrote .
