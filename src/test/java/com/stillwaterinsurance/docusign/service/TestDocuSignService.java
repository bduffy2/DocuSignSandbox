package com.stillwaterinsurance.docusign.service;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.stillwaterinsurance.docusign.service.DocuSignService;
import com.stillwaterinsurance.docusign.vo.*;
import com.stillwaterinsurance.docusign.vo.CompositeTemplate.*;
import com.stillwaterinsurance.docusign.vo.Recipients.Signer;

public class TestDocuSignService {

	private static final DocuSignService signatureService = new DocuSignService();
	
	@Test
	public void testCompTemplateAcord() {
		final File file = new File("src/main/webapp/resources/pdfs/acord_H4.pdf");
		
		List<String> roles = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		List<String> emails = new ArrayList<String>();
		
		roles.add("Insured");
		names.add("Jack Trice");
		emails.add("brandon.duffy@stillwater.com");
		
		try {
			String response = signatureService.requestSignatureAcordAppTemplate(file, roles, names, emails);
			System.out.println(response);
			assertNotNull(response);
			assertTrue(response.contains("envelopeId"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCompTemplate() {
		final File file = new File("src/main/webapp/resources/pdfs/Suppl_App_H4.pdf");
		final String templateId = "44E2C622-6BC7-457B-ACA0-E01F758258ED";
		
		List<String> roles = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		List<String> emails = new ArrayList<String>();
		
		roles.add("Insured");
		names.add("Jack Trice");
		emails.add("brandon.duffy@stillwater.com");
		
		try {
			String response = signatureService.requestSignatureCompositeTemplate(file, templateId, 
					roles, names, emails);
			System.out.println(response);
			assertNotNull(response);
			assertTrue(response.contains("envelopeId"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testRequestSignatureSupplAppH4() {
		final File file = new File("src/main/webapp/resources/pdfs/Suppl_App_H4.pdf");
		
		try {
			String response = signatureService.requestSignatureSupplApp(file, "Jack Trice", 
					"brandon.duffy@stillwater.com");
			System.out.println(response);
			assertNotNull(response);
			assertTrue(response.contains("envelopeId"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testRequestSignatureSupplAppH3() {
		final File file = new File("src/main/webapp/resources/pdfs/Suppl_App_H3.pdf");
		
		try {
			String response = signatureService.requestSignatureSupplApp(file, "Jack Trice", 
					"brandon.duffy@stillwater.com");
			System.out.println(response);
			assertNotNull(response);
			assertTrue(response.contains("envelopeId"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testRequestSignatureAcordApp() {
		final File file = new File("src/main/webapp/resources/pdfs/acord_H4.pdf");
		
		try {
			String response = signatureService.requestSignatureAcordApp(file, "Jack Trice", 
					"brandon.duffy@stillwater.com");
			System.out.println(response);
			assertNotNull(response);
			assertTrue(response.contains("envelopeId"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	@Test
	public void testRequestSignatureAcord() {
		
		final Document acordApp = new Document("1", "ACORD Application/Underwriting Confirmation");
		final Document[] documents = {acordApp};
		
		final Signer[] signers = {new Signer("1", "Jack Trice", "brandon.duffy@stillwater.com", null, 
				DocuSignService.createAcordTabs(documents[0].documentId))};
		
		final Envelope envelope = new Envelope("Stillwater Insurance - Please sign", "sent", 
				documents, new Recipients(signers));
		
		DocumentVO document = new DocumentVO(acordApp.documentId, 
				new File("src/main/webapp/resources/pdfs/AcordAndSuppl.pdf"), acordApp.name);
		
		try {
			final String response = signatureService.requestSignature(envelope, Arrays.asList(document));
			System.out.println(response);
			assertNotNull(response);
			assertTrue(response.contains("envelopeId"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testRequestSignatureAcordOlderHomeBundle() {
		
		final String insuredName = "Jack Trice";
		final String insuredEmail = "brandon.duffy@stillwater.com";
		
		// Older Home
		final ServerTemplate[] serverTemplates1 = {new OlderHomeServerTemplate("1")};
		final Signer insured = new Signer(insuredName, insuredEmail, "Insured", null);
		final InlineTemplate[] inlineTemplates1 = {new InlineTemplate("1", insured)};
		final CompositeTemplate olderHomeTemplate = new CompositeTemplate(serverTemplates1, inlineTemplates1, null);
		
		// Acord App
		final Document acordApp = new Document("1", "ACORD Application/Underwriting Confirmation");
		final Document[] documents = {acordApp};
		
		final Signer signer2 = new Signer(insuredName, insuredEmail, null, 
				DocuSignService.createAcordTabs(documents[0].documentId));
		final InlineTemplate[] inlineTemplates2 = {new InlineTemplate("1", signer2)};
		final CompositeTemplate acordTemplate = new CompositeTemplate(null, inlineTemplates2, documents[0]);
		
		final CompositeTemplate[] compositeTemplates = {olderHomeTemplate, acordTemplate};
		
		final Envelope envelope = new Envelope("Stillwater Insurance - Please sign", "sent", compositeTemplates);
		
		DocumentVO document = new DocumentVO(acordApp.documentId, 
				new File("src/main/webapp/resources/pdfs/AcordAndSuppl.pdf"), acordApp.name);
		
		try {
			final String response = signatureService.requestSignature(envelope, Arrays.asList(document));
			assertNotNull(response);
			System.out.println(response);
			assertTrue(response.contains("envelopeId"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testRequestSignatureTemplate() {
		List<String> roles = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		List<String> emails = new ArrayList<String>();
		
		roles.add("Insured");
		names.add("Billy Bob");
		emails.add("brandon.duffy@stillwater.com");
		
		try {
			final String response = signatureService.requestSignatureTemplate("AC626C30-21BF-4BF7-9774-46B5EEEEF93D", 
					roles, names, emails);
			System.out.println(response);
			assertNotNull(response);
			assertTrue(response.contains("envelopeId"));
		} catch(IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
