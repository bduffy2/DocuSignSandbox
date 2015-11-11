package com.stillwaterinsurance.docusign.controller;

import java.io.File;
import java.io.IOException;

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
	
	@RequestMapping(value={"/send"}, method=RequestMethod.POST)
	public ResponseEntity<String> sendDoc(final ModelAndView mav, final HttpSession session,
			@RequestParam(value="document") final String document,
			@RequestParam(value="name") final String name,
			@RequestParam(value="email") final String email) {
		
		HttpStatus status = HttpStatus.OK;
		String response = "";
		
		final String s = session.getServletContext().getRealPath("/");
		final File file = new File(s + document);
		
		try {
			response = signatureService.requestSignatureDocument(file, name, email);
			response.replaceAll("\n", "<br>");
		} catch(IOException e) {
			LOGGER.error("An I/O exception occurred while sending PDF request", e);
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response = e.getMessage();
		}
		
		return new ResponseEntity<String>(response, status);
	}
	
	@RequestMapping(value={"/send/template"}, method=RequestMethod.POST)
	public ResponseEntity<String> sendTemplate(final ModelAndView mav, final HttpSession session,
			@RequestParam(value="templateId") final String templateId,
			@RequestParam(value="role[]", required=false) String[] roles,
			@RequestParam(value="name[]") final String[] names,
			@RequestParam(value="email[]") final String[] emails) {
		
		HttpStatus status = HttpStatus.OK;
		String response = "";
		
		try {
			//TODO - send to all people not just [0]
			if(roles == null || roles.length == 0) {
				roles = new String[]{"Insured"};
			}
			response = signatureService.requestSignatureTemplate(templateId, roles[0], names[0], emails[0]);
			response.replaceAll("\n", "<br>");
		} catch(IOException e) {
			LOGGER.error("An I/O exception occurred while sending PDF request", e);
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response = e.getMessage();
		}
		
		return new ResponseEntity<String>(response, status);
	}
	
}

