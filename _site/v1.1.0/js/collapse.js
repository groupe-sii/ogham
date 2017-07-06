(function() {
	window.highlightLines = function() {
		$('span.highlight').each(function(i, element) {
			['info', 'warn', 'error', 'diff-add', 'diff-remove', 'irrelevant'].forEach(function(type) {
				highlightElementLines(element, element.getAttribute("data-"+type+"-lines"), type);
			});
			// default
			highlightElementLines(element, element.getAttribute("data-lines"), "");
		});
	};
	
	window.highlightElementLines = function(/*Node*/element, /*String*/lines, /*String*/type) {
		if(lines) {
			var lines = convert(lines);
			var sourceElement = $(element).parent().next(".source").find("pre");
			var sourceLines = getSourceLines(sourceElement);
			for(var i=0 ; i<lines.length ; i++) {
				var line = lines[i] - 1;
				sourceLines[line] = decorate(sourceLines[line], type);
			}
			update(sourceElement, sourceLines);
		}
	};
	
	var convert = function(/*String*/lines) {
		var hl = [];
		var parts = lines.split(",");
		// example: 1,5,10-25
		for(var i=0 ; i<parts.length ; i++) {
			var part = parts[i];
			if(part.indexOf("-")!=-1) {
				// example: 10-25
				var subpart = part.split("-");
				var start = parseInt(subpart[0]);
				var end = parseInt(subpart[1]);
				for(var j=start ; j<=end ; j++) {
					hl.push(j);
				}
			} else {
				// simple number
				hl.push(parseInt(part));
			}
		}
		return hl;
	};
	
	var decorate = function(/*String*/line, /*String*/type) {
		return '<span class="highlight-line'+(type ? '-'+type : '')+'"><span class="wrapped-line">'+line+'</span></span>';
	};
	
	var update = function(/*Node*/element, /*Array*/lines) {
		element.html(lines.join("<br>"));
	};
	
	var getSourceLines = function(/*Node*/element) {
		var content = element.html();
		return content.split("<br>");
	};
})();

$(window).load(highlightLines);