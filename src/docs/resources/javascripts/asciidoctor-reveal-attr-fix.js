(function() {	
	var setAttributes = function() {
		$('[class*="attr:"]').each(function(i, element) {
			var attributes = parseAttrMetadata(element.className);
			for (var attrName in attributes) {
				applyAttr($(element), attrName, attributes[attrName]);
			}
		});
	};
	
	var parseAttrMetadata = function(/*String*/metadata) {
		var classes = metadata.split(' ');
		var attributeMap = {};
		for(var i=0 ; i<classes.length ; i++) {
			var cssClass = classes[i];
			var parts = cssClass.split('=');
			if(cssClass.indexOf('attr:')!=-1 && parts.length==2) {
				attributeMap[parts[0].substr(5)] = parts[1];
			}
		}
		return attributeMap;
	}
	
	var applyAttr = function(/*Node*/element, /*String*/attrName, /*String*/value, /*Map*/attributes) {
		if (extensions[attrName]) {
			extensions[attrName](element, attrName, value, attributes);
		} else {
			element.attr(attrName, value);
		}
	}
	
	var autoAnimate = function(/*Node*/element, /*String*/attrName, /*String*/value) {
		if (element.hasClass('listingblock')) {
			$('.content', element).attr('data-auto-animate', '');
			$('pre', element).attr('data-id', value);
			$('code', element).attr('data-line-numbers', '');
		}
	}
	
	var extensions = {
		'data-auto-animate': autoAnimate
	}

	$(document).ready(setAttributes);
})();
