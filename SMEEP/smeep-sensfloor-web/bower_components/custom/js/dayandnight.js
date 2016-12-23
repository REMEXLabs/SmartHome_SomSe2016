var today = new Date().getHours();
if (today >= 21 && today <= 7) {
	$('head').append('<link rel="stylesheet" type="text/css" href="/custom/css/dark.css">');
}