
(function ($, swGlobal) {
	"use strict";
	
	var _DS = {};
	
	var _CONTEXT = "/DocuSign";
	var LOADING_MODAL_COUNT = 0;
	
	$(document).ready(function() {
		
	});
	
	_DS.sendDoc = function() {
		$('#responseOutput').hide();
		
		var email = $('#sendToEmail').val();
		
		if(validateEmail(email)) {
			$.ajax({
				type: 'POST',
				url: _CONTEXT + "/docs/sendDocument",
				data: {
					document: '/resources/pdfs/Hello World.pdf',
					email: email
				},
				beforeSend: _DS.showLoadingModal,
				complete: _DS.hideLoadingModal
			}).done(function(response){
				response = response.replace(/&/g, '&amp;')
					.replace(/>/g, '&gt;')
					.replace(/</g, '&lt;')
					.replace(/\n/g, '<br>');
				$('#responseOutput').html('<pre>' + response + '</pre>');
				$('#responseOutput').show();
			}).fail(function(response){
				console.log(response);
				$('#responseOutput').html('An error occurred. <br><br>' + JSON.stringify(response, null, '\t'));
				$('#responseOutput').show();
			});
		}
	};

	_DS.sendTemplate = function() {
		$('#responseOutput').hide();
		
		if(validateEmail($('#ohqInsuredEmail').val())) {
			ajaxPostHelper("/docs/sendTemplate", $('#ohqForm').serialize());
		}
	};
	
	_DS.sendCompositeTemplate = function() {
		$('#responseOutput').hide();
		
		if(validateEmail($('#compTemplateInsuredEmail').val())) {
			ajaxPostHelper("/docs/sendCompositeTemplate", $('#compTemplateForm').serialize());
		}
	};
	
	_DS.sendSupplApp = function() {
		$('#responseOutput').hide();
		
		if(validateEmail($('#documentInsuredEmail').val())) {
			ajaxPostHelper('/docs/sendSupplApp', $('#documentForm').serialize())
		}
	};
	
	function ajaxPostHelper(url, data) {
		$.ajax({
			type: 'POST',
			url: _CONTEXT + url,
			data: data,
			beforeSend: _DS.showLoadingModal,
			complete: _DS.hideLoadingModal
		}).done(function(response){
			response = response.replace(/&/g, '&amp;')
				.replace(/>/g, '&gt;')
				.replace(/</g, '&lt;')
				.replace(/\n/g, '<br>');
			$('#responseOutput').html('<pre>' + response + '</pre>');
			$('#responseOutput').show();
		}).fail(function(response){
			console.log(response);
			$('#responseOutput').html('An error occurred. <br><br>' + JSON.stringify(response, null, '\t'));
			$('#responseOutput').show();
		});
	}


	function validateEmail(email) {
		if (email !== '' && /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
			return true;
		}
		else {
			alert("email address is not valid");
			return false;	
		}
	}
	
	//show please wait curtain
	_DS.showLoadingModal = function(optionalText) {
		if(optionalText) {
			$('#loadingModalText').html(optionalText);
		}
		
		if (LOADING_MODAL_COUNT === 0)
		{
			$("#pageLoadingSpinner").spin(swGlobal.spinOptions);
		}
		
		LOADING_MODAL_COUNT++;
		
		$('#pageLoadingModal').reveal({
			closeOnBackgroundClick: false,
			animation: "none"
		});
	};

	//hide please wait curtain
	_DS.hideLoadingModal = function() {
		if (LOADING_MODAL_COUNT > 0)
		{
			LOADING_MODAL_COUNT--;
		}
		
		// Prevent the "hide" until all "shows" are hidden
		if (LOADING_MODAL_COUNT === 0)
		{
			$('#pageLoadingModal').trigger('reveal:close');
		}
		
		$("body").css("cursor", "auto");
	};


	
	//Make this module globally accessible
	if(!window.DsGlobal) {
		window.DsGlobal = _DS;
	}
	
}(window.jQuery, window.SwGlobal));
