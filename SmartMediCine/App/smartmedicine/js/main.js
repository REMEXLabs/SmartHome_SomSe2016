$(function(){ 
	/*$('#featuresbearbeiten').click(function(){	
		   $('#anzfeld').load('editItem.html');
	});
	
    $('.nav li a').click(function(){
        $('.nav li a').removeClass('active');
        $(this).addClass('active'); 
    });
    
     $('.btn btn-primary btn-lg').click(function(){
        $('.btn btn-primary btn-lg').removeClass('active');
        $(this).addClass('active'); 
    });*/


    $('#btnanlegen').click(function(){		
        $('#anzfeld').load('addMedicine.html');
    });

	$('#btnbindingupload').click(function(){		
		$('#anzfeld').load('editItem.html');
    });

	
    $('#btnbearbeiten').click(function(){	
        $('#anzfeld').load('editItemOverview.html');
    });
    $('#btnSimulation').click(function(){	
        $('#anzfeld').load('testSimulation.html');
    });	 


    $('#btnentfernen').click(function(){	
	   $('#anzfeld').load('deleteMedicine.html');
    });
    
    $('#btnhelp').click(function(){	
 	   $('#anzfeld').load('help.html');
     });
    
    $('#btnhome').click(function(){	
	   $('#anzfeld').load('home.html');
    });
    
    $('#btnqrcodescan').click(function(){	
	   $('#anzfeld').load('qrcodescan.html');
    });
       
    $('#featuresbearbeiten').click(function(){	
	   $('#anzfeld').load('editItem.html');
    });
    
    $('#itemsspeichern').click(function(){	
	   $('#anzfeld').load('editItemOverview.html');
    });
    
    $('#abbrechen').click(function(){	
 	   $('#anzfeld').load('addFeature.html.html');
     });
    
    $('#btnItemsAnlegenAbbrechen').click(function(){	
  	   $('#anzfeld').load('addProduct.html');
      }); 
});

