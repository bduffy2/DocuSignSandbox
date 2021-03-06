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
		<h6>Uses a template that is hosted by DocuSign (Older Home Questionnaire). This is the most straightforward
		way to send a doc if it is static</h6>
		<form class="userInputForm" id="ohqForm">
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Insured Email</label>
				</div>
				<div class="four columns end">
					<input type="text" id="ohqInsuredEmail" name="insEmail" />
				</div>
			</div>
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Producer Email</label>
				</div>
				<div class="four columns end">
					<input type="text" id="ohqProducerEmail" name="agtEmail" placeholder="optional" />
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
		
		<h3>Composite Template (Overlay)</h3>
		<h6>Overlay a template onto a document. The template adds the signature area to the 
		Document via an "anchor" tag</h6>
		<form class="userInputForm" id="compTemplateForm">
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Insured Email</label>
				</div>
				<div class="four columns end">
					<input type="text" id="compTemplateInsuredEmail" name="insEmail" />
				</div>
			</div>
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Producer Email</label>
				</div>
				<div class="four columns end">
					<input type="text" id="compTemplateProducerEmail" name="agtEmail" placeholder="optional" />
				</div>
			</div>
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Document</label>
				</div>
				<div class="four columns end">
					<select name="document">
						<option value="/resources/pdfs/Suppl_App_H3.pdf">
							Supplemental App (UW questions) H3 California
						</option>
						<option value="/resources/pdfs/Suppl_App_H4.pdf">
							Supplemental App (UW questions) H4 California
						</option>
						<option value="/resources/pdfs/acord_H4.pdf">
							Acord H4 CA
						</option>
					</select>
				</div>
			</div>
			<div class="row collapse">
				<div class="eleven offset-by-one columns">
					<input type="button" class="button" value="Send" onclick="DsGlobal.sendCompositeTemplate()" />
					<a href="${context}/resources/pdfs/Suppl_App_H3.pdf" target="_blank">Suppl H3 CA</a>&nbsp;&nbsp;
					<a href="${context}/resources/pdfs/Suppl_App_H4.pdf" target="_blank">Suppl H4 CA</a>&nbsp;&nbsp;
					<a href="${context}/resources/pdfs/acord_H4.pdf" target="_blank">Acord H4 CA</a>
				</div>
			</div>
		</form>
		
		<hr/>
		
		<h3>Document</h3>
		<h6>Send a document for signature (no templates). Tags are added to the doc at the time of 
		sending via anchoring to specific text in the document. This seems to be simpler than 
		dealing with composite templates</h6>
		<form class="userInputForm" id="documentForm">
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Insured Email</label>
				</div>
				<div class="four columns end">
					<input type="text" id="documentInsuredEmail" name="insEmail" />
				</div>
			</div>
			<div class="row collapse">
				<div class="one columns">
					<label class="inline">Document</label>
				</div>
				<div class="four columns end">
					<select name="document">
						<option value="/resources/pdfs/Suppl_App_H3.pdf">
							Supplemental App (UW questions) H3 California
						</option>
						<option value="/resources/pdfs/Suppl_App_H4.pdf">
							Supplemental App (UW questions) H4 California
						</option>
					</select>
				</div>
			</div>
			<div class="row collapse">
				<div class="eleven offset-by-one columns">
					<input type="button" class="button" value="Send" onclick="DsGlobal.sendSupplApp()" />
					<a href="${context}/resources/pdfs/Suppl_App_H3.pdf" target="_blank">H3 CA</a>&nbsp;&nbsp;
					<a href="${context}/resources/pdfs/Suppl_App_H4.pdf" target="_blank">H4 CA</a>
				</div>
			</div>
		</form>
		
		<hr/>
		
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