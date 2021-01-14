(function() {
	var convert = function(/*String[]*/sourceLines, /*String*/lines) {
		var hl = [];
		var parts = lines.split(",");
		// example: 1,5,10-25
		for(var i=0 ; i<parts.length ; i++) {
			var part = parts[i];
			if(part.indexOf("-")!=-1) {
				// example: 10-25
				var subpart = part.split("-");
				var start = subpart[0] ? parseInt(subpart[0]) : 1;
				var end = subpart[1] ? parseInt(subpart[1]) : sourceLines.length;
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
	
	var update = function(/*Node*/element, /*Array*/lines) {
		element.html(lines.join("\n"));
	};
	
	var getSourceLines = function(/*Node*/element) {
		var content = element.html();
		return content.split("\n");
	};
	
	var getSourceElement = function(/*Node*/listingBlock) {
		return $(listingBlock).find("pre code");
	}
	
	var parseLinesMetadata = function(/*String*/metadata) {
		var classes = metadata.split(' ');
		var classMap = {};
		for(var i=0 ; i<classes.length ; i++) {
			var cssClass = classes[i];
			var parts = cssClass.split(':');
			if(parts.length==2) {
				classMap[parts[0]] = parts[1];
			}
		}
		return classMap;
	}
	
	window.lineUtils = {
		convert: convert,
		update: update,
		getSourceLines: getSourceLines		
	}
	
	window.sourceUtils = {
		getSourceElement: getSourceElement,
		parseLinesMetadata: parseLinesMetadata
	}
})();

