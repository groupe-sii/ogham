(function() {
	var highlightLines = function() {
		$('.listingblock').each(function(i, element) {
			var metadata = sourceUtils.parseLinesMetadata(element.className);
			['info', 'warn', 'error', 'diff-add', 'diff-remove', 'irrelevant'].forEach(function(type) {
				highlightElementLines(element, metadata[type+'-lines'], type);
			});
			// default
			highlightElementLines(element, metadata['highlight-lines'], "");
		});
	};
	
	var highlightElementLines = function(/*Node*/element, /*String*/lines, /*String*/type) {
		if(lines) {
			var sourceElement = sourceUtils.getSourceElement(element);
			var sourceLines = lineUtils.getSourceLines(sourceElement);
			var lines = lineUtils.convert(sourceLines, lines);
			for(var i=0 ; i<lines.length ; i++) {
				var line = lines[i] - 1;
				sourceLines[line] = decorate(sourceLines[line], type);
			}
			lineUtils.update(sourceElement, sourceLines);
		}
	};
	
	var decorate = function(/*String*/line, /*String*/type) {
		return '<span class="highlight-line'+(type ? '-'+type : '')+'"><span class="wrapped-line">'+(line || "")+'</span></span>';
	};
	
	$(document).ready(highlightLines);
})();
