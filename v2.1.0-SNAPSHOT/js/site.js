$(document).ready(function() {
	var currentVersion = $('.projectVersion').text().replace(/Version: (.+)[.] $/, "$1");
	var ul = $("a[title='[version]']").parent().parent();
	$(ul).prev().html('v'+currentVersion+' <b class="caret"></b>');
	$(ul).empty();
	$.get($(".brand").attr("href") + "/versions.json", function(data) {
		data.forEach(function(version) {
			ul.append('<li><a href="'+$(".brand").attr("href")+'/'+version+'/" title="'+version+'">'+version+'</a></li>');
		});
	});
});