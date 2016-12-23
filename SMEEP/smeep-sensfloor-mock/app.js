/**
 * file			app.js
 * @author     	Thomas Derleth
 * @copyright  	2016 Thomas Derleth (free to use)
 * @version    	1.0
**/

var express = require('express');  
var app = express();  
var server = require('http').createServer(app);  
var io = require('socket.io')(server);

// load environment-variables
var env = require('node-env-file');

// set view engine, using jade-templating
app.set('view engine', 'jade');


// Load ENV variables form /.env
env(__dirname + '/.env');

// global object containing ENV
var env_vars = {
	server:process.env.SERVER, 
	width_from:process.env.WIDTH_FROM, 
	width_to:process.env.WIDTH_TO, 
	height_from:process.env.HEIGHT_FROM,
	height_to:process.env.HEIGHT_TO,
};

// use bower components - Bootstrap / JQuery
app.use(express.static(__dirname + '/bower_components'));  

// Routing - Render views/index.jade and passign ENV to view
app.get('/', function(req, res,next) {  
	res.render('index', {
	  env_vars:env_vars
	});
});


// Contructor for Tiles
function Tile(x, y, c) {
    this.x = x;
    this.y = y;
    this.c = c;
}

// Initialise array containing the current position
var position = new Array(0,0);

// On connection keeep client
io.on('connection', function(client) {  

	/**
	* event 		position	
	* description	Server sends current postion to connected client.
	*				Client updates tiles (colorizing each)
	**/
    client.emit('position', position ); 

	/**
	* event 		move	
	* description	Client sends movement on keydown with updated postion
	*				Server updates position and emits new position-event
	**/
	client.on('move', function (data) {
	    position = data;
		console.log(position);
		client.emit('position', position ); 
	});
	  
	// run this code every 500 ms
	setInterval(function(){
		
		var arrayList = [];
		
		// build arrayList with tiles 
		for(y = env_vars.height_from; y < env_vars.height_to ; y++){	
			
			for (x = env_vars.width_from ; x < env_vars.width_to  ; x++ ){
				
				if(x == position[0] && y == position[1])
					weight = 100;
				else
					weight = 0;
					
				var current_tile = new Tile( parseFloat(x)+0.01, parseFloat(y)+0.01 , weight );
				
				arrayList.push(current_tile);	
				
			}
		}
				
		var cog_tile = new Tile( parseFloat(position[0])+0.01 , parseFloat(position[1])+0.01, 100 );
		
		// modelling data by carpet-model	
		var data = {
			cog:cog_tile, 
			points: arrayList
		};

		// format json, preparing for response
		resp_json = JSON.stringify(data);
		
		console.log(resp_json);
		
		/**
		* event 		cluster	
		* description	Server sends JSON to all connected clients
		**/		
	    client.broadcast.emit('cluster', resp_json); 
	    
	    // again update carpet on mock-clients
	    client.emit('position', position ); 	
	        
	}, 500);

});


server.listen(8000);