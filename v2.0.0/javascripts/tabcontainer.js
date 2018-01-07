(function() {
	var process = function() {
		var tabContainerStarts = $('.tab-container');
		var tabContainerEnds = $('.tab-container-end');
		if(tabContainerStarts.length!=tabContainerEnds.length) {
			throw new Error("The number of tab-container is different from tab-container-end");
		}
		for(var i=0 ; i<tabContainerStarts.length ; i++) {
			generateTabContainer(i, tabContainerStarts[i], tabContainerEnds[i]);
		}
	}
	
	var generateTabContainer = function(/*int*/group, /*Node*/start, /*Node*/end) {
		var tabs = $(start).nextUntil(end, '.tab');
		var height = 0;
		for(var i=0 ; i<tabs.length ; i++) {
			var tab = tabs[i];
			var tabTitleNode = $(tab).find('p');
			var tabTitle = tabTitleNode.html();
			var contentNodes = $(tab).nextUntil(tabs[i+1] || end);
			var contentHeight = computeHeight($(contentNodes));
			if(contentHeight>height) {
				height = contentHeight;
			}
			$(tab).prepend('<input type="radio" id="tab-'+group+'-'+i+'" name="tab-group-'+group+'" '+(i==0 ? 'checked' : '')+'>');
			$(tab).append('<div class="tab-content">');
			var tabContentNode = $(tab).find('.tab-content');
			$(tabContentNode).append(contentNodes);
			$(tab).append(tabContentNode);
			$(start).append(tab);
			tabTitleNode.replaceWith('<label class="tab-label" for="tab-'+group+'-'+i+'">'+tabTitle+'</label>');
		}
		$(start).css('height', (height+45+42)+'px');			// TODO: height of tab... This is really bad :(
		$(end).remove();
	}
	
	var computeHeight = function(/*Node[]*/nodes) {
		var totalHeight = 0;
		for(var i=0 ; i<nodes.length ; i++) {
			var overflow = $(nodes[i]).css('overflow', 'hidden');
			totalHeight += $(nodes[i]).outerHeight(true);
			$(nodes[i]).css('overflow', overflow);
		}
		return totalHeight;
	}
	
	$(document).ready(process);
})();