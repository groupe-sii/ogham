(function() {
	var findSteps = function() {
		var steps = $('.steps');
		var currentNodes = $('.steps-current');
		for(var i=0 ; i<steps.length ; i++) {
			var parent = $(steps[i]);
			var current = parseInt($(currentNodes[i]).text());
			createSteps(parent, current);
		}
	}
	
	var createSteps = function(/*Node*/ parent, /*int*/ current) {
		var steps = parent.find('a');
		parent.append('<div class="progressbar">').addClass('total-steps'+steps.length);
		parent.find('.progressbar').append('<div class="progress">');
		parent.find('.progress').css('width', (current*100/(steps.length-1))+"%");
		parent.find('.progressbar').append(parent.find('p'));
		for(var i=0 ; i<steps.length ; i++) {
			var step = $(steps[i]);
			step.addClass('step step'+i+' '+(current==i ? 'current' : '')+' '+(i==steps.length-1 ? 'last' : ''));
			step.attr("title", step.text());
			step.css("left", (i*100/(steps.length-1))+"%");
		}
	}
	
	$(document).ready(findSteps);
})();