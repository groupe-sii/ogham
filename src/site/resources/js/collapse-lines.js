(function() {
	window.collapseLines = function() {
		$('span.collapse').each(function(i, element) {
			collapseElementLines(element, element.getAttribute("data-lines"));
		});
		$('span.collapsed').each(function(i, element) {
			$(element).click(function() {
				$(element).removeClass("closed");
			});
		});
	};
	
	window.collapseElementLines = function(/*Node*/element, /*String*/lines) {
		if(lines) {
			var lines = lineUtils.convert(lines);
			var sourceElement = $(element).parent().next(".source").find("pre");
			var sourceLines = lineUtils.getSourceLines(sourceElement);
			lines.sort(function(a, b) { return a-b; });
			var start;
			var previous;
			for(var i=0 ; i<lines.length ; i++) {
				var line = lines[i];
				if(!previous) {
					start = line-1;
				}
				if(previous && line!=previous+1) {
					var end = previous-1;
					// group is ended => wrap all lines between start and end
					sourceLines[start] = decorateStart(sourceLines[start]);
					sourceLines[end] = decorateEnd(sourceLines[end]);
					start = line-1;
				}
				// update previous to new line
				previous = line;
			}
			lineUtils.update(sourceElement, sourceLines);
		}
	};
	
	var decorateStart = function(/*String*/line) {
		return '<span class="collapsed closed" title="click to show hidden code"><span class="collapsed-text">...</span><span class="collapsed-lines">'+line;
	};
	
	var decorateEnd = function(/*String*/line) {
		return '</span></span>';
	};
})();

$(window).load(collapseLines);