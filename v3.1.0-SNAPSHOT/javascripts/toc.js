(function() {
	var offset = 100;
	
	var init = function() {
		var toc = $("#toc");
		if (toc && toc.length) {
			// All list items
			var menuItems = toc.find("a");
			// Anchors corresponding to menu items
			var scrollItems = menuItems.map(function(){
				var item = $($(this).attr("href"));
				if (item.length) { return item; }
			});
			
			// Bind to scroll
			$(window).scroll(function(){
				// Get container scroll position
				var fromTop = $(this).scrollTop() + offset;
				
				// Get id of current scroll item
				var cur = scrollItems.map(function(){
					if ($(this).offset().top < fromTop)
						return this;
				});
				// Get the id of the current element
				cur = cur[cur.length-1];
				var id = cur && cur.length ? cur[0].id : "";
				
				// Set/remove active class
				menuItems.parent().removeClass("reading");
				menuItems.filter("[href='#"+id+"']").parent().addClass("reading");
				
				// Open/close sections
				menuItems.parents('.sectlevel3,.sectlevel2').removeClass("opened");
				menuItems.filter("[href='#"+id+"']").parents('.sectlevel3,.sectlevel2').addClass("opened");
			});
		}
	}
    
    $(document).ready(init);
})();