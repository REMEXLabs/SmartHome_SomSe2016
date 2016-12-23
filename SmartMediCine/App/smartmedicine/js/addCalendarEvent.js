/**
 * @author Waldemar Jaufmann
 * @author Thomas Seewald
 */
$(function(){ 
	var manufacturer_selection = "";
	var product_type_selection = "";
	var product_id=0;
	var product_name="";
	var features_number=0;
	
	var product_details_id = 'product_details_id';
	
	var host = window.location.origin;
	
	$("#btnbindingupload").unbind('click').click(function () {
		$('#anzfeld').load('addItem.html');
	});
	
	$('#btnNichtGefunden').unbind('click').click(function(){		
		$('#anzfeld').load('addProductManually.html');
    });
	$('#manufacturer').ready(function() {
	    $("#producttype_title").attr("style", "visibility: hidden");
    	$("#producttype").attr("style", "visibility: hidden");
	    $("#product_title").attr("style", "visibility: hidden");
    	$("#product").attr("style", "visibility: hidden");
		load_Manufacturer();
		load_Manufacturer_Selection();
		load_Product_Type_Selection();		
		load_Product_Selection();
		load_room();
	});
	
	/*Loading the rooms for the next step*/
	function load_room() {
		  $.ajax({
			    dataType: 'json',
			    success: function(data) {
			    	var arrRoom = [];
			
			    	arrRoom.push("<option id='0'>Please select room...</option>");
			    	for(var i=0;i<data.room.length;i++){
			    		arrRoom.push("<option id="+data.room[i].room_ID+">"+data.room[i].room_Name+"</option>");
			    	}
				  	  localStorage.setItem("rooms", arrRoom);
				   },
			    url: host+'/itemconfigurator/OIS/rest/room'
			});
		};
		
	function load_Manufacturer_Selection() {
		$( "#manufacturer" ).unbind('change').change(function () {
		      $( "#manufacturer option:selected" ).each(function() {
		    	  manufacturer_selection = $( this ).text() + " ";
			      window.manufacturer_id =""+$( this ).attr('id');
		    	  
			      $("#product").attr("style", "visibility: hidden");
				  $("#product_title").attr("style", "visibility: hidden"); 
				  $("#btnbindingupload").attr('disabled',true);
			      
		    	  if(window.manufacturer_id != "0"){
				      $("#producttype_title").attr("style", "visibility: visible");
			    	  $("#producttype").attr("style", "visibility: visible");
			    	  load_Product_Type_By_Manufacturer_ID(window.manufacturer_id);
		    	  }
		    	  else{
		    		  $("#btnbindingupload").attr('disabled',true);
				      $("#producttype_title").attr("style", "visibility: hidden");
			    	  $("#producttype").attr("style", "visibility: hidden");
		    	  }
		    });
		  }).change();
	};
		
	function load_Manufacturer() {
		$.ajax({
		    dataType: 'json',
		    success: function(data) {
		    	var options = $('#manufacturer');
		    	$("select[id='manufacturer'] option").remove();
		    	$("<option id='0'>Choose the manufacturer...</option><br>").appendTo("select[id='manufacturer']");
		    	for(var i=0;i<data.manufacturer.length;i++){
		    		options.append($("<option id='"+data.manufacturer[i].manufacturer_ID+"'>"+data.manufacturer[i].manufacturer_Name+"</option><br>"));
		    	}	        
			   },
		    url: host+'/itemconfigurator/OIS/rest/manufacturer'
		});
	};
	
	function load_Product_Type_By_Manufacturer_ID(manufacturer_id) {
		  $.ajax({
			    dataType: 'json',
			    success: function(data) {
			    	$cname = $("select[id='producttype']");
			    	$("select[id='producttype'] option").remove();
			    	$("<option id='0'>Choose the product type...</option><br>").appendTo($cname);
			    	for(var i=0;i<data.product_types.length;i++){
			    		$("<option id='"+data.product_types[i].product_Type_ID+"'>"+data.product_types[i].product_Type_Name+"</option><br>").appendTo($cname);
			    	}	        
				   },
			    url: host+'/itemconfigurator/OIS/rest/product_type/Manufacturer_ID='+manufacturer_id
			});
		  
	};
	function load_Product_Type_Selection() {
		$( "#producttype" ).unbind('change').change(function () {
		      $( "#producttype option:selected" ).each(function() {
		    	  product_type_selection = $( this ).text() + " ";
		    	  product_type_id = $( this ).attr('id');
		    	  
		    	  if(product_type_id != "0"){
				      $("#product_title").attr("style", "visibility: visible");
			    	  $("#product").attr("style", "visibility: visible");
				      load_Product_by_Product_ID(product_type_id)
		    	  }
		    	  else{
		    		  $("#btnbindingupload").attr('disabled',true);
				      $("#product_title").attr("style", "visibility: hidden");
			    	  $("#product").attr("style", "visibility: hidden");
		    	  }
		    });
		  }).change();
	};
	
	function load_Product_by_Product_ID(product_id) {
		  $.ajax({
			    dataType: 'json',
			    success: function(data) {
			    	$product = $("select[id='product']");
			    	$("select[id='product'] option").remove();
			    	$("<option id='0'>Choose the product...</option><br>").appendTo($product);
			    	for(var i=0;i<data.product_details.length;i++){
			    		window.product_Detail_ID=data.product_details[i].product_Detail_ID;
			    		$("<option id='"+data.product_details[i].product_Detail_ID+"'>"+data.product_details[i].product_Name+"</option><br>").appendTo($product);
			    	}
				   },
			    url: host+'/itemconfigurator/OIS/rest/product/Product_Type_ID='+product_id+'/Manufacturer_ID='+window.manufacturer_id
			});
		  
	};
	
	function load_Product_Selection() {
		$( "#product" ).unbind('change').change(function () {
		      $( "#product option:selected" ).each(function() {
		      window.product_id = $( this ).attr('id');
		      window.product_name =  $( this ).val();
		      
		      localStorage.setItem('product_name',  window.product_name);
		  	  localStorage.setItem(product_details_id, $( this ).attr('id'));
		      if(window.product_id != "0"){
		    	  $("#btnbindingupload").attr('disabled',false);
		      } else {
		    	  $("#btnbindingupload").attr('disabled',true);
		      }
		    });
		  }).change();	
	};
});