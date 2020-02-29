(function() {
	var process = function() {
		var headings = $('h1[id],h2[id],h3[id],h4[id],h5[id],h6[id]');
		for (var i=0 ; i<headings.length ; i++) {
			var anchor = getAnchor(headings[i]);
			if (anchor) {
				$(headings[i]).attr('id', $(anchor).attr('href').replace(/^#/, ''));
			}
		}
	}
	
	var getAnchor = function(/*Node*/ heading) {
		return $('a.anchor[href]', heading)[0];
	}
	
	$(document).ready(process);
})();