
$(document).ready(function() {
	$('[class^=progress]').each(function(i, e) {
		var content = $(e).text();
		$(e).text("");
		$(e).append('<span class="text">'+content+'</span>');
		var percent = e.className.replace("progress:", "") + "%";
		$(e).append('<div class="progress"><div class="progress-bar" role="progressbar" style="width: '+percent+'" aria-valuenow="'+percent+'" aria-valuemin="0" aria-valuemax="100">'+percent+'</div></div>');
	});
});