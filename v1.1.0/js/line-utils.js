(function() {
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
	
	var update = function(/*Node*/element, /*Array*/lines) {
		element.html(lines.join("<br>"));
	};
	
	var getSourceLines = function(/*Node*/element) {
		var content = element.html();
		return content.split("<br>");
	};
	
	window.lineUtils = {
		convert: convert,
		update: update,
		getSourceLines: getSourceLines
	}
})();

