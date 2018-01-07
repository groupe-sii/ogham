
$(window).load(function() {
	$('[class^=progress]').each(function(i, e) {
		var content = $(e).text();
		$(e).text("");
		$(e).append('<span class="text">'+content+'</span>');
		var percent = e.className.replace("progress:", "") + "%";
		$(e).append('<div class="progress-bar"><span class="progress" style="width: '+percent+'">'+percent+'</span></div>');
	});
});