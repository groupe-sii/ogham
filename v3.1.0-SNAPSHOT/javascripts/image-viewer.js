(function() {
	var init = function() {
		$('body').append(`<div class="image-viewer">
							<div class="viewer-header">
								<span class="close-button">x</span>
							</div>
							<div class="viewer-content">
								<div class="viewer-content-wrapper"></div>
							</div>
						</div>`);
		$('body').append('<div class="image-viewer-overlay"></div>');

		$('.image-viewer .close-button').click(close);
		$('.image-viewer-overlay').click(close);
		$(document).keyup(function(e) {
			if (e.key === "Escape") {
				close();
			}
		});
		
		$('.imageblock').click(function() {
			$('.image-viewer .viewer-content-wrapper').empty().append($(this).html());
			$('body').addClass('image-viewer-opened');
		});
	}
	
	var close = function() {
		$('body').removeClass('image-viewer-opened');
	}
	var open = function() {
		$('body').addClass('image-viewer-opened');
	}
	
	$(document).ready(init);
})();