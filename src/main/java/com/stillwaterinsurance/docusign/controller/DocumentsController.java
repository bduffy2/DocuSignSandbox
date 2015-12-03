package com.stillwaterinsurance.docusign.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.stillwaterinsurance.docusign.service.DocuSignService;

@Controller
@RequestMapping("/docs")
public class DocumentsController {
	
	private static final Logger LOGGER = Logger.getLogger(DocumentsController.class);

	@Autowired
	private DocuSignService signatureService;
	
	private static final Map<String, String> DOC_TEMPLATES;
	
	@RequestMapping(value={"/sendDocument"}, method=RequestMethod.POST)
	public ResponseEntity<String> sendDoc(final ModelAndView mav, final HttpSession session,
			@RequestParam(value="document") final String document,
			@RequestParam(value="email") final String email) {
		
		HttpStatus status = HttpStatus.OK;
		String response = "";
		
		final String s = session.getServletContext().getRealPath("/");
		final File file = new File(s + document);
		
		try {
			response = signatureService.requestSignatureDocument(file, randomName(), email);
			response.replaceAll("\n", "<br>");
		} catch(IOException e) {
			LOGGER.error("An I/O exception occurred while sending PDF request", e);
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response = e.getMessage();
		}
		
		return new ResponseEntity<String>(response, status);
	}
	
	@RequestMapping(value={"/sendSupplApp"}, method=RequestMethod.POST)
	public ResponseEntity<String> sendSupplApp(final ModelAndView mav, final HttpSession session,
			@RequestParam(value="document") final String document,
			@RequestParam(value="insEmail") final String insEmail) {
		
		HttpStatus status = HttpStatus.OK;
		String response = "";
		
		final String s = session.getServletContext().getRealPath("/");
		final File file = new File(s + document);
		
		try {
			response = signatureService.requestSignatureSupplApp(file, randomName(), insEmail);
			response.replaceAll("\n", "<br>");
		} catch(IOException e) {
			LOGGER.error("An I/O exception occurred while sending PDF request", e);
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response = e.getMessage();
		}
		
		return new ResponseEntity<String>(response, status);
	}
	
	@RequestMapping(value={"/sendTemplate"}, method=RequestMethod.POST)
	public ResponseEntity<String> sendTemplate(final ModelAndView mav, final HttpSession session,
			@RequestParam(value="templateId") final String templateId,
			@RequestParam(value="insEmail") final String insEmail,
			@RequestParam(value="agtEmail", required=false) final String agtEmail) {
		
		HttpStatus status = HttpStatus.OK;
		String response;
		
		List<String> roles = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		List<String> emails = new ArrayList<String>();
		
		roles.add("Insured");
		names.add(randomName());
		emails.add(insEmail);
		
		if(agtEmail != null && !agtEmail.trim().isEmpty()) {
			roles.add("Producer");
			names.add(randomName());
			emails.add(agtEmail);
		}
		
		try {
			response = signatureService.requestSignatureTemplate(templateId, roles, names, emails);
			response.replaceAll("\n", "<br>");
		} catch(IOException e) {
			LOGGER.error("An I/O exception occurred while sending PDF request", e);
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response = e.getMessage();
		}
		
		return new ResponseEntity<String>(response, status);
	}
	
	@RequestMapping(value={"/sendCompositeTemplate"}, method=RequestMethod.POST)
	public ResponseEntity<String> sendCompTemplate(final ModelAndView mav, final HttpSession session,
			@RequestParam(value="document") final String document,
			@RequestParam(value="insEmail") final String insEmail,
			@RequestParam(value="agtEmail", required=false) final String agtEmail) {
		
		HttpStatus status = HttpStatus.OK;
		String response;
		
		List<String> roles = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		List<String> emails = new ArrayList<String>();
		
		roles.add("Insured");
		names.add(randomName());
		emails.add(insEmail);
		
		if(agtEmail != null && !agtEmail.trim().isEmpty()) {
			roles.add("Producer");
			names.add(randomName());
			emails.add(agtEmail);
		}
		
		final String s = session.getServletContext().getRealPath("/");
		final File file = new File(s + document);
		
		String templateId = DOC_TEMPLATES.containsKey(document) ? 
				DOC_TEMPLATES.get(document) : "44E2C622-6BC7-457B-ACA0-E01F758258ED";
		
		try {
			response = signatureService.requestSignatureCompositeTemplate(file, templateId, roles, names, emails);
			response.replaceAll("\n", "<br>");
		} catch(IOException e) {
			LOGGER.error("An I/O exception occurred while sending PDF request", e);
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response = e.getMessage();
		}
		
		return new ResponseEntity<String>(response, status);
	}
	
	private String randomName() {
		final String[] FIRST_NAMES = new String[]{"John", "James", "Robert", "Mary", "Lisa"};
		final String[] LAST_NAMES = new String[]{"Smith", "Jones", "Williams", "Taylor", "Brown"};
		Random random = new Random();
		
		return FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + 
				" " + LAST_NAMES[random.nextInt(LAST_NAMES.length)];
	}
	
	static {
		Map<String, String >map = new HashMap<String, String>();
		map.put("/resources/pdfs/Suppl_App_H3.pdf", "44E2C622-6BC7-457B-ACA0-E01F758258ED");
		map.put("/resources/pdfs/Suppl_App_H4.pdf", "44E2C622-6BC7-457B-ACA0-E01F758258ED");
		map.put("/resources/pdfs/acord_H4.pdf", "1EDD0786-6313-4789-81BF-CA60CECA41CC");
		DOC_TEMPLATES = Collections.unmodifiableMap(map);
	}
	
}

