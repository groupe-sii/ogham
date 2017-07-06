$(window).load(function() {
	$('div.source pre, pre code').each(function(i, e) {
		e.innerHTML = e.innerHTML.replace(/\n/g, "<br />").replace(/\t/g, "&nbsp;&nbsp;");
	});
	$('[data-spy="scroll"]').each(function () {
		$(this).scrollspy('refresh');
	});

	var currentVersion = $('.projectVersion').text().replace(/Version: (.+)[.] $/, "$1");
	var nav = $('.nav.pull-right');
	nav.append('<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">v'+currentVersion+' <b class="caret"></b></a><ul class="dropdown-menu versions"></ul></li>');
	var ul = nav.find('.dropdown-menu.versions');
	$.get($(".brand").attr("href") + "/versions.json", function(data) {
		data.forEach(function(version) {
			ul.append('<li><a href="'+$(".brand").attr("href")+'/'+version+'/" title="'+version+'">'+version+'</a></li>');
		});
	});
})

