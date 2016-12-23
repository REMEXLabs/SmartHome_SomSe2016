# SensFloor Web

Sensfloor Web is a node.js application developed in the context of the module **Smart Home Lab** in the summer semester 2016 at **Stuttgart Media University**. The module was part of the degree program **Computer Science and Media**.

The goal of the module was to design and develop interactive smart home solutions to assist elderly persons with certain disabilities.

The project group Smeep decided to include the [SensFloor by FutureShape](http://www.future-shape.com/index.php/en/technologies/23/sensfloor-large-area-sensor-system) , a [Philips Hue](http://www2.meethue.com/de-DE) and [openHab](http://www.openhab.org/) in combination with a Web Interface to control and configure both devices. The node.js application serves as the control node and and is also responsible for the web interface. 

### Installation
In order to use the application you need to:

1. Clone the repository
2. Adjusts the hosts in **views/io.jade** and **views/settings.jade**
3. Adjust the url for openHab in the **index.js**
4. Start the app with ```node index.js```

### Team
The concept and implementation have been prepared by:

- Thomas Derleth
- Jörg Einfeldt
- Merle Hiort
- Marc Stauffer

### License
This project is licensed under the terms of the MIT license
