# SensFloor Carpet Mock

Sensfloor Carpet Mock is a node.js application developed in the context of the module **Smart Home Lab** in the summer semester 2016 at **Stuttgart Media University**. The module was part of the degree program **Computer Science and Media**.

The project group Smeep decided to include the [SensFloor by FutureShape](http://www.future-shape.com/index.php/en/technologies/23/sensfloor-large-area-sensor-system) , a [Philips Hue](http://www2.meethue.com/de-DE) and [openHab](http://www.openhab.org/) in combination with a Web Interface to control and configure both devices. 

This node.js application simulates the behavior of the [SensFloor Carpet](http://www.future-shape.com/index.php/en/technologies/23/sensfloor-large-area-sensor-system). It´s based on socket.io and provides data to it´s connected clients in the same way the original carpet does. 

In order to use this mock you can browser to ```localhost:8000``` and use the arrow keys to navigate over your carpet.

### Installation
In order to use the application you need to:

1. Clone the repository
2. Adjusts the host in **.env**
3. Start the app with ```node app.js```


### Team
The concept and implementation have been prepared by:

- Thomas Derleth
- Jörg Einfeldt
- Merle Hiort
- Marc Stauffer

### License
This project is licensed under the terms of the MIT license.
