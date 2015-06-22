$(window).load(function() {
	$('div.source pre, pre code').each(function(i, e) {
		e.innerHTML = e.innerHTML.replace(/\n/g, "<br />").replace(/\t/g, "&nbsp;&nbsp;");
	});
	$("#toc-bar").affix({});
})

