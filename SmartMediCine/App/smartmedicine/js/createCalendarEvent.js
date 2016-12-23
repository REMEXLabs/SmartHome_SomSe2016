/**
 * @author Waldemar Jaufmann
 */
$(function(){
	
	$( document ).ready(function() {
		var siteDestination = localStorage.getItem('SiteDestination');
		var medikament = localStorage.getItem('Medikament');
		var krankheit = localStorage.getItem('Krankheit');
	   
		if(siteDestination=="MainInformation"){
			var inputMedikament = document.getElementById('txtMedikament');
		    inputMedikament.value=medikament;
			var inputKrankheit = document.getElementById('txtKrankheit');
			inputKrankheit.value = krankheit;  
		}
		
		if(siteDestination=="Date"){
			var intervall = localStorage.getItem('Intervall');
			var einnahmeZeitpunkt = localStorage.getItem('EinnahmeZeitpunkt');
			
			if(einnahmeZeitpunkt!=null){
				document.getElementById("txtEinnahmeZeitpunkt").value=einnahmeZeitpunkt;
			}
	 	   
			if(intervall!=null){
				var inputIntervall = document.getElementById('txtIntervall');
		  	  	inputIntervall.value=intervall; 
			}
		}
		
		if(siteDestination=="LightSelection"){
			var lightSelection = localStorage.getItem('selectedLight');
			
			
			if(lightSelection!=null){
				if(lightSelection=="red"){
					$("#aRed").click();  
				}
				if(lightSelection=="blue"){
					$("#aBlue").click();  
				}
				if(lightSelection=="green"){
					$("#aGreen").click();  
				}
			}

		}
		
		
	});
	
	
	$("#btnAddMainInformation").click(function(){
		var inputKrankheit = document.getElementById('txtKrankheit');
		var valueInputKrankeit = inputKrankheit.value;  
		var inputMedikament = document.getElementById('txtMedikament');
	    var valueInputMedikament = inputMedikament.value;

	    if(valueInputMedikament!="" && valueInputKrankeit!=""){
		    localStorage.setItem('Medikament', valueInputMedikament);
		    localStorage.setItem('Krankheit', valueInputKrankeit);
		    localStorage.setItem('SiteDestination', "Date");
		    $('#anzfeld').load('addMedicine_2.html');
	    } else{
	    	
	    	if(valueInputMedikament=="" && valueInputKrankeit==""){

		  		  $("#pAlertInfo").empty();
				  $("#pAlertInfo").append("<font size=4>Das Feld für das Medikament und die Krankheit darf nicht leer sein</font>");
				  $('#btnOpenModal').click();
	    	} else {
	    		if(valueInputMedikament=="")
		    	{
		  		  $("#pAlertInfo").empty();
				  $("#pAlertInfo").append("<font size=4>Das Feld für das Medikament darf nicht leer sein</font>");
				  $('#btnOpenModal').click();
		    	}
		    	if(valueInputKrankeit==""){
			  	  $("#pAlertInfo").empty();
				  $("#pAlertInfo").append("<font size=4>Das Feld für das Krankheit darf nicht leer sein</font>");
				  $('#btnOpenModal').click();
		    	}
	    	}
	    	
	    }
    });
	
	$("#btnAddDate").click(function(){
		
		var valueEinnahmeZeitpunkt = document.getElementById("txtEinnahmeZeitpunkt").value;
  	  	var selDuration = document.getElementById("selDuration");
  	  	var valueDuration = selDuration.options[selDuration.selectedIndex].id;
  	  	var inputIntervall = document.getElementById('txtIntervall');
  	  	var valueIntervall = inputIntervall.value;
  	  	
  	    if(valueEinnahmeZeitpunkt!=""){
  	    	localStorage.setItem('EinnahmeZeitpunkt', valueEinnahmeZeitpunkt);
  		    localStorage.setItem('Dauer', valueDuration);
  	 	    localStorage.setItem('Intervall', valueIntervall);
  	 	   
  	 	   localStorage.setItem('SiteDestination', "LightSelection");
  	 	   $('#anzfeld').load('addMedicine_3.html');
  	    } else {
		  	  $("#pAlertInfo").empty();
			  $("#pAlertInfo").append("<font size=4>Bitte Datum eingeben</font>");
			  $('#btnOpenModal').click();
  	    }
	   
    });	
	
	$("#btnBackDate").click(function(){
		 localStorage.setItem('SiteDestination', "MainInformation");
		 
		var valueEinnahmeZeitpunkt = document.getElementById("txtEinnahmeZeitpunkt").value;
	  	var selDuration = document.getElementById("selDuration");
	  	var valueDuration = selDuration.options[selDuration.selectedIndex].id;
	  	var inputIntervall = document.getElementById('txtIntervall');
	  	var valueIntervall = inputIntervall.value;
	  	  	
		localStorage.setItem('EinnahmeZeitpunkt', valueEinnahmeZeitpunkt);
		localStorage.setItem('Dauer', valueDuration);
	 	localStorage.setItem('Intervall', valueIntervall);
		$('#anzfeld').load('addMedicine.html');
	});
	
	$("#btnBackLightSelection").click(function(){
		localStorage.setItem('SiteDestination', "Date");
		$('#anzfeld').load('addMedicine_2.html');
	});
	
	$("#btnCloseModal").click(function(){

		var alertType = localStorage.getItem('alertType');
		
		if(alertType=="gespeichert"){
			$('#anzfeld').load('index.html');
		}

	});
	
	$("#aViolette").click(function(){  
		localStorage.setItem('selectedLight', "violette");
	
	    document.getElementById("aBlue").style.opacity = 0.3;
	    document.getElementById("aGreen").style.opacity = 0.3;
	    document.getElementById("aRed").style.opacity = 0.3;
	    
	    
	    
		$("#aViolette").empty();
		$("#aViolette").append( "<img class='myImage' src='img/violette_3.png' width='150' height='150'/>" );
		
		$("#aRed").empty();
		$("#aRed").append( "<img onmouseover='bigImg(this)' name='red' onmouseout='normalImg(this)' class='myImage' src='img/red_3.png' width='150' height='150'/>" );
		
		
		$("#aBlue").empty();
		$("#aBlue").append( "<img id='aBlue' name='red' onmouseover='blueHover(this)' onmouseout='blueNormal(this)' class='myImage' src='img/blue_3.png' width='150' height='150'/>" );
		
		$("#aGreen").empty();
		$("#aGreen").append("<a id='aGreen'><img id='aGreen' onmouseover='greenHover(this)' onmouseout='greenNormal(this)' class='myImage' src='img/green_3.png' width='150' height='150'/>");
		
		$("#tdRed").empty();
		$("#tdBlue").empty();
		$("#tdGreen").empty();
		$("#tdViolette").empty();
		$("#tdViolette").append("<font size='4'><b>Magenta</b></font>");
		
		localStorage.setItem('ColorId', "3");
	    document.getElementById("aViolette").style.opacity = 1;
	});
	
	$("#aRed").click(function(){  
		localStorage.setItem('selectedLight', "red");
		
	    document.getElementById("aBlue").style.opacity = 0.3;
	    document.getElementById("aGreen").style.opacity = 0.3;
	    document.getElementById("aViolette").style.opacity = 0.3;
	    
		$("#aViolette").empty();
		$("#aViolette").append( "<img onmouseover='violetteHover(this)' id='aViolette' onmouseout='violetteNormal(this)' class='myImage' src='img/violette_3.png' width='150' height='150'/>" );
	    
		$("#aRed").empty();
		$("#aRed").append( "<img class='myImage' src='img/red_3.png' width='150' height='150'/>" );
		
		$("#aBlue").empty();
		$("#aBlue").append( "<img id='aBlue' name='red' onmouseover='blueHover(this)' onmouseout='blueNormal(this)' class='myImage' src='img/blue_3.png' width='150' height='150'/>" );
		
		$("#aGreen").empty();
		$("#aGreen").append("<a id='aGreen'><img id='aGreen' onmouseover='greenHover(this)' onmouseout='greenNormal(this)' class='myImage' src='img/green_3.png' width='150' height='150'/>");
		
		$("#tdRed").empty();
		$("#tdBlue").empty();
		$("#tdGreen").empty();
		$("#tdViolette").empty();
		$("#tdRed").append("<font size='4'><b>Rot</b></font>");
		
		localStorage.setItem('ColorId', "11");
	    document.getElementById("aRed").style.opacity = 1;
	});
	
	$("#aBlue").click(function(){
		localStorage.setItem('selectedLight', "blue");
		
	    document.getElementById("aRed").style.opacity = 0.3;    
	    document.getElementById("aGreen").style.opacity = 0.3;
	    document.getElementById("aViolette").style.opacity = 0.3;
	    
		$("#aViolette").empty();
		$("#aViolette").append( "<img onmouseover='violetteHover(this)' id='aViolette' onmouseout='violetteNormal(this)' class='myImage' src='img/violette_3.png' width='150' height='150'/>" );
	    
		$("#aRed").empty();
		$("#aRed").append( "<img onmouseover='bigImg(this)' onmouseout='normalImg(this)' class='myImage' src='img/red_3.png' width='150' height='150'/>" );
		
		$("#aBlue").empty();
		$("#aBlue").append( "<img class='myImage' src='img/blue_3.png' width='150' height='150'/>" );
		
		$("#aGreen").empty();
		$("#aGreen").append("<a id='aGreen'><img id='aGreen' onmouseover='greenHover(this)' onmouseout='greenNormal(this)' class='myImage' src='img/green_3.png' width='150' height='150'/>");
		
		$("#tdRed").empty();
		$("#tdBlue").empty();
		$("#tdGreen").empty();
		$("#tdViolette").empty();
		$("#tdBlue").append("<font size='4'><b>Blau</b></font>");
		
		localStorage.setItem('ColorId', "9");
	    document.getElementById("aBlue").style.opacity = 1;
	});
	
	$("#aGreen").click(function(){
		localStorage.setItem('selectedLight', "green");
		
	    document.getElementById("aRed").style.opacity = 0.3;    
	    document.getElementById("aBlue").style.opacity = 0.3;
	    document.getElementById("aViolette").style.opacity = 0.3;
	    
		$("#aViolette").empty();
		$("#aViolette").append( "<img onmouseover='violetteHover(this)' id='aViolette' onmouseout='violetteNormal(this)' class='myImage' src='img/violette_3.png' width='150' height='150'/>" );
	    
		$("#aRed").empty();
		$("#aRed").append( "<img onmouseover='bigImg(this)' name='red' onmouseout='normalImg(this)' class='myImage' src='img/red_3.png' width='150' height='150'/>" );
		
		$("#aBlue").empty();
		$("#aBlue").append( "<img id='aBlue' name='red' onmouseover='blueHover(this)' onmouseout='blueNormal(this)' class='myImage' src='img/blue_3.png' width='150' height='150'/>" );
	
		$("#aGreen").empty();
		$("#aGreen").append("<a id='aGreen'><img id='aGreen' class='myImage' src='img/green_3.png' width='150' height='150'/>");
		
		$("#tdRed").empty();
		$("#tdBlue").empty();
		$("#tdGreen").empty();
		$("#tdViolette").empty();
		$("#tdGreen").append("<font size='4'><b>Grün</b></font>");
		
		localStorage.setItem('ColorId', "10");
	    document.getElementById("aGreen").style.opacity = 1;
	});
});