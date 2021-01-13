(function() {
	
	var isStickyTitle = function(/*Node*/ el) {
		return [/*'h1', 'h2', */'h3', 'h4', 'h5', 'h6'].includes(el.nodeName.toLowerCase());
	}
	
	var stacked;
	var ontop;
	var update = function() {
		var offset = $('.main-body').offset() ? $('.main-body').offset().top+10 : 10;
		var elems = document.elementsFromPoint(window.innerWidth/2, offset).filter(isStickyTitle);
		$(stacked).removeClass('stacked');
		$(ontop).removeClass('on-top');
		$(elems).addClass('stacked');
		if (elems.length) {
			$(elems[0]).addClass('on-top');
			ontop = elems[0];
		} else {
			ontop = null;
		}
		stacked = elems;
	}
	
	$(window).scroll(update);
	$(document).ready(update);
})();