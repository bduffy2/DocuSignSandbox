import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.stillwaterinsurance.docusign.service.DocuSignService;

public class TestDocuSignService {

	private static final DocuSignService signatureService = new DocuSignService(
			"BRAN-b5d4c55d-d6f5-40e8-be03-82d6382169aa", "brandon.duffy@stillwater.com", "brandon");
	
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
			String response = signatureService.requestSignatureAcordApp(file, roles, names, emails);
			System.out.println(response);
			assertNotNull(response);
		} catch (IOException e) {
			e.printStackTrace();
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
