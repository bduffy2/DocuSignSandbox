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
	public ResponseEntity<String> index(final ModelAndView mav, final HttpSession session,
			@RequestParam(value="document") final String document,
			@RequestParam(value="name") final String name,
			@RequestParam(value="email") final String email) {
		
		HttpStatus status = HttpStatus.OK;
		String response = "";
		
		final String s = session.getServletContext().getRealPath("/");
		final File file = new File(s + document);
		
		try {
			response = signatureService.sendPdfRequest(file, name, email);
			response.replaceAll("\n", "<br>");
		} catch(IOException e) {
			LOGGER.error("An I/O exception occurred while sending PDF request", e);
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response = e.getMessage();
		}
		
		return new ResponseEntity<String>(response, status);
	}
	
}

