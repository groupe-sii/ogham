(function() {
	
	var addVersions = function() {
		var currentVersion = $('.projectVersion').text().replace(/Version: (.+)[.] $/, "$1");
		var ul = $("a[title='[version]']").parent().parent();
		$('.dropdown-toggle', ul).html('v' + currentVersion +' <b class="caret"></b>');
		$('.dropdown-menu', ul).empty();
		$.get($(".navbar-brand").attr("href") + "/versions.json", function(data) {
			data.forEach(function(version) {
				$('.dropdown-menu', ul).append('<li><a class="dropdown-item" href="'+$(".brand").attr("href")+'/'+version+'/" title="'+version+'">'+version+'</a></li>');
			});
		});
	}
	
	var fixAnchorScrolling = function() {
		var targetedAnchor = document.location.hash;
		if(targetedAnchor && $(targetedAnchor).length) {
			$(targetedAnchor)[0].scrollIntoView({block: "start", inline: "nearest", behavior: "smooth"});
		}
	}
	

	$(document).ready(function() {
		addVersions();
		// must be the last one
//		$(document).ready(function() {
//			fixAnchorScrolling();
//		});
	});
})();
