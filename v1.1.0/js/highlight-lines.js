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
			var lines = lineUtils.convert(lines);
			var sourceElement = $(element).parent().next(".source").find("pre");
			var sourceLines = lineUtils.getSourceLines(sourceElement);
			for(var i=0 ; i<lines.length ; i++) {
				var line = lines[i] - 1;
				sourceLines[line] = decorate(sourceLines[line], type);
			}
			lineUtils.update(sourceElement, sourceLines);
		}
	};
	
	var decorate = function(/*String*/line, /*String*/type) {
		return '<span class="highlight-line'+(type ? '-'+type : '')+'"><span class="wrapped-line">'+line+'</span></span>';
	};
})();

$(window).load(highlightLines);