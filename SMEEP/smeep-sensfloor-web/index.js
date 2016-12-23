/**
 * file			index.js
 * @author     	Thomas Derleth
 * @copyright  	2016 Thomas Derleth
 * @version    	1.0
**/

// required modules
var express = require('express');  
var app = express();  
var server = require('http').createServer(app);
var io = require('socket.io')(server);
var jsonfile = require('jsonfile');
var request = require('request');
var schedule = require('node-schedule');

// cron jobs
var wakeUpCron;
var bedTimeCron;

// global
var settings_config_file = __dirname + '/settings.json';
var settings_config;
var openhab_base_url = "http://localhost:8080/rest/items/";
var base_item_name = "Light_Bedroom";
var default_func = function(){};

// set view engine, using jade-templating
app.set('view engine', 'jade');

// use bower components - Bootstrap / JQuery
app.use(express.static(__dirname + '/bower_components'));  

jsonfile.readFile(settings_config_file, function(err, temp) {

	// routing
	settings_config = temp;
	app.get('/', function(req, res,next) {  
		if(settings_config.toggle_evening == null)
			res.render('tutorial_one');
		else
			res.render('settings');
	});
	app.get('/tutorial_one', function(req, res,next) { res.render('tutorial_one');});
	app.get('/tutorial_two', function(req, res,next) { res.render('tutorial_two');});
	app.get('/settings', function(req, res,next) { res.render('settings', {data:settings_config}); });
	app.get('/tutorial_end', function(req, res,next) { res.render('tutorial_end');});
	app.get('/io', function(req, res,next) { res.render('io');});
		
	// schedule configured cron jobs
	scheduleBedtime();
	scheduleWakeup();

	// on connection keep client
	var status_hue;
	io.on('connection', function(client) {  

		// INTERACTION WITH SENSFLOOR AND HUE LIGHT

		// read status of hue
		request(openhab_base_url+base_item_name+"_S/state" , function(error, response, body){
			if(!error && response.statusCode == 200){
				status_hue = body;
				client.emit('get_status_hue', status_hue );
			}
		});
	
		// switch status of hue
		client.on("set_status_hue", function(data){		
			post(openhab_base_url+base_item_name+"_S" , data);
		});
		
		// reset reference of sensfloor
		client.on("start_reference", function(data){
			console.log("starting timer for reference point");			
			setTimeout(function() {
				console.log("Emitting reference call");
				client.broadcast.emit('set_reference'); 
			}, 10000);
		});
		
		// switch hue light color if reference was updated			
		client.on("get_reference", function(data){
			data = JSON.parse(data);
			settings_config.reference_tile.x = data.x;
			settings_config.reference_tile.y = data.y;
			jsonfile.writeFile(settings_config_file, settings_config, function (err) {});
			lightReference();
		});			
		
		// react on sensfloor event
		client.on('event_sensfloor', function(data){
			// initialising time parameters
			current_hour = new Date().getHours();
			current_minutes = new Date().getMinutes();
			bedtime = settings_config.bedtime.split(":");
			wakeuptime = settings_config.wakeuptime.split(":");
			absoluteBedtime = bedtime[0] + bedtime[1]; 
			absoluteWakeuptime = wakeuptime[0] + wakeuptime[1];
			absoluteCurrent =  String(current_hour) + String(current_minutes);	

			// debug logs
			console.log("recieved sensfloor event, requested state: " + data);
			console.log("configured bedtime: " + bedtime);
			console.log("configured wakeuptime: " + wakeuptime);
			
			// guard clause, if its day carpet should not be active
			if(absoluteCurrent >= absoluteWakeuptime && absoluteCurrent < absoluteBedtime){
				console.log("Not switching light on since its daytime");
				return;
			}
			
			// interaction with hue light based on current state and configuration
			data = JSON.parse(data);			
			if(data.status == "on" && settings_config.toggle_night == true){	
				console.log("switching light ON");
				lightBrightness("50");
				lightSwitch("ON");
			}
			else if(settings_config.toggle_night == true){
				setTimeout(function() {
					console.log("switching light OFF");
					lightSwitch("OFF");
				}, 3000);
			}
		});	

		client.emit('settings_config', settings_config );
		client.on('io_toggle_all', function (data) {
			console.log("toggle all");
			if(data == true){
				settings_config.toggle_evening = true;
				settings_config.toggle_morning = true;
				settings_config.toggle_night = true;
			}
			else{
				settings_config.toggle_evening = false;
				settings_config.toggle_morning = false;
				settings_config.toggle_night = false;
			}
			jsonfile.writeFile(settings_config_file, settings_config, function (err) {});
			client.emit('settings_config', settings_config ); 
	
		});

		// EVENTS TO UPDATE CONFIGURATION

		client.on('io_toggle_evening', function (data) {
			if(data == true){settings_config.toggle_evening = true;}
			else{settings_config.toggle_evening = false;}
			jsonfile.writeFile(settings_config_file, settings_config, function (err) {});
			client.emit('settings_config', settings_config ); 
	
		});
 
		client.on('io_toggle_morning', function (data) {
			if(data == true){settings_config.toggle_morning = true;}
			else{settings_config.toggle_morning = false;}
			jsonfile.writeFile(settings_config_file, settings_config, function (err) {});
			client.emit('settings_config', settings_config ); 
		
		});
		
		client.on('io_toggle_night', function (data) {
			if(data == true){settings_config.toggle_night = true;}
			else{settings_config.toggle_night = false;}
			jsonfile.writeFile(settings_config_file, settings_config, function (err) {});
			client.emit('settings_config', settings_config ); 
		
		});

		client.on('bedtime', function (data) {
			settings_config.bedtime = data;
			jsonfile.writeFile(settings_config_file, settings_config, function (err) {});
			client.emit('settings_config', settings_config ); 
			scheduleBedtime();
		});

		client.on('wakeuptime', function (data) {
			settings_config.wakeuptime = data;
			jsonfile.writeFile(settings_config_file, settings_config, function (err) {});
			client.emit('settings_config', settings_config ); 
			scheduleWakeup();
		});
	});
});

// CRON HELPER FUNCTIONS

/**
 * Schedule the cron job for wakuptime
 */
function scheduleWakeup(){
	wakeuptime = settings_config.wakeuptime.split(":");
	wakeupTimeSchedule = Number(wakeuptime[1])+ ' '+Number(wakeuptime[0])+' * * *';
	wakeUpCron = resetCron(wakeUpCron, wakeupTimeSchedule, function(){
		// only switch on light if its off
		url = openhab_base_url+base_item_name+"_S/state";
		request(url, function(error, response, body){
			if(!error && response.statusCode == 200 && body=="OFF"){
				lightMorning(1, 100, 10);
			}
		});
	});
}

/**
 * Schedulee the cron job for bedtime
 */
function scheduleBedtime(){
	//resetting cron
	bedtime = settings_config.bedtime.split(":");
	bedTimeSchedule = Number(bedtime[1])+ ' '+Number(bedtime[0])+' * * *';
	bedTimeCron = resetCron(bedTimeCron, bedTimeSchedule, function(){
		url = openhab_base_url+base_item_name+"_S/state";
		request(url, function(error, response, body){
			// only switch off light if its on
			if(!error && response.statusCode == 200 && body=="ON"){
				lightEvening(100, 1, 10);
			}
		});

	});
}

/**
 * Resets current running cron jobs
 * @param {Object} cronJob The cron job to reset  
 * @param {String} cronSchedule The timer string for the cron job
 * @param {String} cronFunction The function to execute
 * @return {Object} Newly started cronjob 
 */
function resetCron(cronJob, cronSchedule, cronFunction){
	// canceling
	if(cronJob != null){
		cronJob.cancel();
	}
	// rescheduling of cronjob
	cronJob = schedule.scheduleJob(cronSchedule, function(){
		cronFunction();
	});
	return cronJob
}


// HUE HELPER FUNCTIONS

/**
 * Dims hue light with a given delay
 * @param {Number} baseBrightness Start value for dimming (1-100)
 * @param {Number} targetBrightness Target value for dimming (1-100)
 * @param {Number} delay Time in seconds how long dimming should take
 */
function lightEvening(baseBrightness, targetBrightness, delay){
	// calculating parameters
	diff = baseBrightness - targetBrightness;
	step = diff / delay;
	// adjusting light
	var counter = 1;
	var interval = setInterval(function(){
		currentBrightness = baseBrightness - step*counter;
		lightBrightness(currentBrightness.toString());
		counter++;
		// killing intervall
		if(currentBrightness <= targetBrightness){
			// switching light off
			setTimeout(function() {
				lightSwitch("OFF");
			}, 5000);
			clearInterval(interval);
		}
	}, 1000);
}

/**
 * Lights hue with a given delay
 * @param {Number} baseBrightness Start value for lighting (1-100)
 * @param {Number} targetBrightness Target value for lighting (1-100)
 * @param {Number} delay Time in seconds how long dimming should take
 */
function lightMorning(baseBrightness, targetBrightness, delay){
	console.log("Function call: morning");
	// calculating parameters
	diff = targetBrightness - baseBrightness;
	step = diff / delay;
	// init light
	lightBrightness(baseBrightness.toString());
	lightSwitch("ON");
	// adjusting light
	var counter = 1;
	var interval = setInterval(function(){
		currentBrightness = baseBrightness + step*counter;
		lightBrightness(currentBrightness.toString());
		console.log(currentBrightness);
		counter++;
		// killing intervall
		if(currentBrightness >= targetBrightness){
			console.log("clearing interval");
			clearInterval(interval);
		}
	}, 1000);
}

/**
 * Switch hue to green and back down
 */
function lightReference(){
	callback = function(error, response, body){
		setTimeout(function() {
			lightColor("90,100,100");
		}, 5000);
		setTimeout(function() {
			lightSwitch("OFF");
		}, 5500);
	};
	lightSwitch("ON");
	lightColor("145,100,100", callback);
}

/**
 * Calls hue switch item
 * @param {String} value Value to post to hue (ON/OFF)
 * @param {Function} callback Callback for response
 */
function lightSwitch(value, callback){
	// switch light on
	url = openhab_base_url + base_item_name + "_S";
	post(url, value, callback);
}

/**
 * Calls hue color item
 * @param {String} value Value to post to hue (1-255,1-100,1-100)
 * @param {Function} callback Callback for response
 */
function lightColor(value, callback){
	url = openhab_base_url + base_item_name + "_C";
	post(url, value, callback);
}

/**
 * Calls hue brightness item
 * @param {String} value Value to post to hue (1-100)
 * @param {Function} callback Callback for response
 */
function lightBrightness(value, callback){
	url = openhab_base_url + base_item_name + "_B";
	post(url, value, callback);
}

/**
 * Calls hue temperature item
 * @param {String} value Value to post to hue (1-100)
 * @param {Function} callback Callback for response
 */
function lightTemperature(value, callback){
	url = openhab_base_url + base_item_name + "_T";
	post(url, value, callback);
}

/**
 * Execute POST http-request
 * @param {String} url URL for request
 * @param {String} body Request body
 * @param {Function} callback Callback for response
 */
function post(url, body, callback){
	callback = callback || default_func;
	request({
    	url: url,
    	method: "POST",
    	body: body,
		}, callback);
}

server.listen(3000);
