<!DOCTYPE html>
<!--[if IE 8]><html class="no-js lt-ie9" lang="en" ><![endif]-->
<!--[if gt IE 8]><!--><html class="no-js" lang="en" ><!--<![endif]-->

<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width">
	<title>Stillwater DocuSign</title>
	
	<!--style-->
	<link rel="stylesheet" href="${contextStatic}/foundation/stylesheets/foundation.min.css?t=${launchTimestamp}">
	<link rel="stylesheet" href="${contextStatic}/common/css/foundation-common.css?t=${launchTimestamp}">
	
	<!--Javascript-->
	<script type="text/javascript" src="${contextStatic}/foundation/javascripts/modernizr.foundation.js?t=${launchTimestamp}"></script>
	<script type="text/javascript" src="${contextStatic}/foundation/javascripts/foundation.min.js?t=${launchTimestamp}"></script>
	<script type="text/javascript" src="${contextStatic}/foundation/javascripts/app.js?t=${launchTimestamp}"></script>
	<script type="text/javascript" src="${contextStatic}/common/js/foundation-common.js?t=${launchTimestamp}"></script>
	<script type="text/javascript" src="${contextStatic}/common/js/spin.js?t=${launchTimestamp}"></script>
	<script type="text/javascript" src="${contextStatic}/common/js/jquery.spin.js?t=${launchTimestamp}"></script>
	<script type="text/javascript" src="${contextStatic}/common/js/sw-global.js?t=${launchTimestamp}"></script>
	
	<script type="text/javascript" src="${context}/resources/js/docusign.js?t=${launchTimestamp}"></script>
</head>

<body>
	<div class="row" style="width:80%">
		<h1>DocuSign - Stillwater Integration</h1>
		
		<hr/>
		
		<h3>Template</h3>
		<h5>Older Home Questionnaire</h5>
		<form class="userInputForm" id="ohqForm">
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Insured Name</label>
				</div>
				<div class="four columns end">
					<input type="text" id="ohqInsuredName" name="name[]" placeholder="optional" />
				</div>
			</div>
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Insured Email</label>
				</div>
				<div class="four columns end">
					<input type="text" id="ohqInsuredEmail" name="email[]" />
				</div>
			</div>
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Producer Name</label>
				</div>
				<div class="four columns end">
					<input type="text" id="ohqProducerName" name="name[]" placeholder="optional" />
				</div>
			</div>
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Producer Email</label>
				</div>
				<div class="four columns end">
					<input type="text" id="ohqProducerEmail" name="email[]" placeholder="optional" />
				</div>
			</div>
			<div class="row collapse">
				<div class="eleven offset-by-one columns">
					<input type="button" class="button" value="Send" onclick="DsGlobal.sendTemplate()" />
				</div>
			</div>
			
			<input type="hidden" name="templateId" value="AC626C30-21BF-4BF7-9774-46B5EEEEF93D" />
		</form>
		
		<hr/>
		
		<h3>Document</h3>
		<div class="userInputForm">
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Name</label>
				</div>
				<div class="four columns end">
					<input type="text" id="sendToName" placeholder="optional" />
				</div>
			</div>
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Email</label>
				</div>
				<div class="four columns end">
					<input type="text" id="sendToEmail" name="email"/>
				</div>
			</div>
			<div class="row collapse">
				<div class="eleven offset-by-one columns">
					<input type="button" class="button" value="Send" onclick="DsGlobal.sendDoc()" />
					<a href="${context}/resources/pdfs/Hello World.pdf" target="_blank">This document</a> will be sent for signature
				</div>
			</div>
		</div>
		
		<hr/>
		
		TODO:
		<ul>
			<li>Send multiple docs in single "envelope"</li>
			<li>Upload custom docs?</li>
			<li>Real docs that we will be sending from FIRST</li>
			<li>Templates</li>
		</ul>
		
		<div class="panel display-none" id="responseOutput"></div>
		
	</div>
	
	<!-- Modals - leave at end of body -->
	<div id="pageLoadingModal" class="reveal-modal coverScreen">
		<div id="pageLoadingSpinner" class="loading-spinner"></div>
		<div class="text" id="loadingModalText">
		Loading, please wait...
		</div>
	</div>
	
</body>

</html>