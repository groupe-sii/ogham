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
			$(tab).prepend('<input type="radio" id="tab-'+group+'-'+i+'" name="tab-group-'+group+'" '+(i==0 ? 'checked' : '')+'>');
			$(tab).append('<div class="tab-content">');
			var tabContentNode = $(tab).find('.tab-content');
			$(tabContentNode).append(contentNodes);
			$(tab).append(tabContentNode);
			$(start).append(tab);
			tabTitleNode.replaceWith('<label class="tab-label" for="tab-'+group+'-'+i+'">'+tabTitle+'</label>');
			var contentHeight = computeHeight($(tabContentNode));
			if(contentHeight>height) {
				height = contentHeight;
			}
		}
		var tabBarHeight = 0;
		for(var i=0 ; i<tabs.length ; i++) {
			var tabTitleNode = $(tabs[i]);
			var tabHeight = computeHeight(tabTitleNode);
			if (tabHeight > tabBarHeight) {
				tabBarHeight = tabHeight;
			}
		}
		$(start).css('height', (height+tabBarHeight+computeContainerHeight(start)+10)+'px');		// add 10px for possible vertical scrollbar that may appear
		$(end).remove();
	}
	
	var computeHeight = function(/*Node[]*/nodes) {
		var totalHeight = 0;
		for(var i=0 ; i<nodes.length ; i++) {
			$(nodes[i]).css('position', 'relative');
			totalHeight += $(nodes[i]).outerHeight(true);
			$(nodes[i]).css('position', '');
		}
		return totalHeight;
	}
	
	var computeContainerHeight = function(/*Node*/ container) {
		var children = $('> *', container);
		for (var i=0 ; i<children.length ; i++) {
			$(children[i]).css('display', 'none');
		}
		var height = $(container).outerHeight();
		for (var i=0 ; i<children.length ; i++) {
			$(children[i]).css('display', '');
		}
		return height;
	}
	
	$(document).ready(process);
})();