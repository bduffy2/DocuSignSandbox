package com.stillwaterinsurance.docusign.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stillwaterinsurance.docusign.vo.DocumentVO;
import com.stillwaterinsurance.docusign.vo.Envelope;
import com.stillwaterinsurance.docusign.vo.Tabs;
import com.stillwaterinsurance.docusign.vo.Tabs.DateSignedTab;
import com.stillwaterinsurance.docusign.vo.Tabs.FullNameTab;
import com.stillwaterinsurance.docusign.vo.Tabs.SignHereTab;

@Service
public class DocuSignService {
	
	private static final Logger LOGGER = Logger.getLogger(DocuSignService.class);
	
	@Value("${credentials.integratorKey}")
	private String integratorKey;
	
	@Value("${credentials.username}")
	private String username;
	
	@Value("${credentials.password}")
	private String password;
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public DocuSignService() {}
	
	public DocuSignService(String integratorKey, String username, String password) {
		this.integratorKey = integratorKey;
		this.username = username;
		this.password = password;
	}
	
	
	public String loginRequest() throws IOException {
		StringBuilder result = new StringBuilder();

		String url = "https://demo.docusign.net/restapi/v2/login_information";
		String body = ""; // no request body for the login call

		// create connection object, set request method, add request headers
		HttpURLConnection conn = initializeRequest(url, "GET", body, getAuthHeader());

		// send the request
		result.append("STEP 1:  Sending Login request...\n");
		int status = conn.getResponseCode();
		if (status != 200) // 200 = OK
		{
			result.append("\nError description:  \n" + errorParse(conn, status));
			return result.toString();
		}

		// obtain baseUrl and accountId values from response body
		String response = getResponseBody(conn);
		try{
			result.append("-- Login response --\n\n" + prettyFormat(response, 2) + "\n");
		} catch(Exception e) {
			result.append("-- Login response --\n\n" + response + "\n");
		}
		
		return result.toString();
	}

	/**
	 * Send a document for signature
	 * 
	 * @param pdfFile The PDF file to be signed
	 * @param recipientName recipient (signer) name
	 * @param recipientEmail recipient (signer) email
	 * @throws IOException 
	 */
	public String requestSignatureDocument(final File pdfFile, final String recipientName, 
			final String recipientEmail) throws IOException {
		
		StringBuilder result = new StringBuilder();

		String baseURL = ""; // we will retrieve this through the Login API call
		String envelopeId = ""; // generated from signature request API call
		String url = ""; // end-point for each api call
		String body = ""; // request body
		String response = ""; // response body
		int status; // response status
		HttpURLConnection conn = null; // connection object used for each request

		// ============================================================================
		// STEP 1 - Make the Login API call to retrieve your baseUrl and accountId
		// ============================================================================

		url = "https://demo.docusign.net/restapi/v2/login_information";
		body = ""; // no request body for the login call

		// create connection object, set request method, add request headers
		conn = initializeRequest(url, "GET", body, getAuthHeader());

		// send the request
		result.append("STEP 1:  Sending Login request...\n");
		status = conn.getResponseCode();
		if (status != 200) // 200 = OK
		{
			result.append("\nError description:  \n" + errorParse(conn, status));
			return result.toString();
		}

		// obtain baseUrl and accountId values from response body
		response = getResponseBody(conn);
		baseURL = parseXMLBody(response, "baseUrl");
		parseXMLBody(response, "accountId");
		try{
			result.append("-- Login response --\n\n" + prettyFormat(response, 2) + "\n");
		} catch(Exception e) {
			result.append("-- Login response --\n\n" + response + "\n");
		}

		// ============================================================================
		// STEP 2 - Signature Request on Document API Call
		// ============================================================================

		url = baseURL + "/envelopes"; // append "/envelopes" to baseUrl for signature request call

		// this example uses XML formatted requests, JSON format is also accepted
		// following body will place one signature tab 100 pixels right and 100
		// down from top left corner of document
		body = "<envelopeDefinition xmlns=\"http://www.docusign.com/restapi\">" +  
				"<emailSubject>API Call for adding signature request to document and sending</emailSubject>" + 
				"<status>sent</status>" + 
				// add document(s)
				"<documents>" +
					"<document>" + 
						"<documentId>1</documentId>" + 
						"<name>" + pdfFile.getName() + "</name>" + 
					"</document>" + 
				"</documents>" +
				// add recipient(s)
				"<recipients>" + 
					"<signers>" + 
						"<signer>" + 
							"<recipientId>1</recipientId>" + 
							"<name>" + recipientName + "</name>" +
							"<email>" + recipientEmail + "</email>" +
							"<tabs>" + 
								"<signHereTabs>" + 
									"<signHere>" + 
										"<xPosition>100</xPosition>" + 
										"<yPosition>100</yPosition>" + 
										"<documentId>1</documentId>" + 
										"<pageNumber>1</pageNumber>" + 
									"</signHere>" +
								"</signHereTabs>" + 
							"</tabs>" + 
						"</signer>" + 
					"</signers>" + 
				"</recipients>" +  
			"</envelopeDefinition>";

		// re-use connection object for second request...
		conn = initializeRequest(url, "POST", body, getAuthHeader());

		// read document content into byte array
		InputStream inputStream = new FileInputStream(pdfFile);
		byte[] bytes = new byte[(int) pdfFile.length()];
		inputStream.read(bytes);
		inputStream.close();

		// start constructing the multipart/form-data request...
		String requestBody = "\r\n\r\n--BOUNDARY\r\n" + "Content-Type: application/xml\r\n"
				+ "Content-Disposition: form-data\r\n" + "\r\n" + body + "\r\n\r\n--BOUNDARY\r\n" + // our xml formatted request body
				"Content-Type: application/pdf \r\n" + "Content-Disposition: file; filename=\"" + pdfFile.getName()
				+ "\"; documentid=1\r\n" + "\r\n";
		// we break this up into two string since the PDF doc bytes go here and
		// are not in string format.
		// see further below where we write to the outputstream...
		String reqBody2 = "\r\n" + "--BOUNDARY--\r\n\r\n";

		// write the body of the request...
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		dos.writeBytes(requestBody.toString());
		dos.write(bytes);
		dos.writeBytes(reqBody2.toString());
		dos.flush();
		dos.close();

		result.append("STEP 2:  Sending signature request on document...\n");

		status = conn.getResponseCode(); // triggers the request
		if (status != 201) // 201 = Created
		{
			result.append(errorParse(conn, status));
			return result.toString();
		}

		// display the response body
		response = getResponseBody(conn);
		envelopeId = parseXMLBody(response, "envelopeId");
		try{
			result.append("-- Signature Request response --\n\n" + prettyFormat(response, 2));
		} catch(Exception e) {
			result.append("-- Signature Request response --\n\n" + response);
		}
		result.append(
				"Signature request has been sent to " + recipientEmail + "!\nEnvelopeId is:  " + envelopeId + "\n");
		
		return result.toString();
	}
	
	public String requestSignatureSupplApp(final File pdf, final String recipientName, 
			final String recipientEmail) throws IOException {
		
		final String body = Json.createObjectBuilder()
				.add("emailSubject", "Brandon TEST - Supplemental App")
				.add("status", "sent")
				.add("documents", Json.createArrayBuilder().add(Json.createObjectBuilder()
						.add("documentId", "1")
						.add("name", pdf.getName())))
				.add("recipients", Json.createObjectBuilder()
						.add("signers", Json.createArrayBuilder().add(Json.createObjectBuilder()
								.add("recipientId", "1")
								.add("name", recipientName)
								.add("email", recipientEmail)
								.add("tabs", Json.createObjectBuilder()
										.add("signHereTabs", Json.createArrayBuilder().add(Json.createObjectBuilder()
												.add("documentId", "1")
												.add("anchorString", "INSURED NAME - SIGNATURE")
												.add("anchorXOffset", "10")
												.add("anchorYOffset", "-1")
												.add("anchorUnits", "pixels")
												.add("scaleValue", "0.6")))
										.add("fullNameTabs", Json.createArrayBuilder().add(Json.createObjectBuilder()
												.add("documentId", "1")
												.add("anchorString", "INSURED NAME - PRINT")
												.add("anchorXOffset", "0")
												.add("anchorYOffset", "-11")
												.add("anchorUnits", "pixels")))
										.add("dateSignedTabs", Json.createArrayBuilder().add(Json.createObjectBuilder()
												.add("documentId", "1")
												.add("anchorString", "DATE SIGNED")
												.add("anchorXOffset", "0")
												.add("anchorYOffset", "-11")
												.add("anchorUnits", "pixels")))))))
				.build().toString();
		
		return multipartRequest(body, pdf);
	}
	
	/**
	 * Send a document for signature via template
	 * 
	 * @param templateId valid template id copied from Console
	 * @param roleName valid template role name
	 * @param recipientName recipient (signer) name
	 * @param recipientEmail recipient (signer) email	
	 * @throws IOException
	 */
	public String requestSignatureTemplate(String templateId, List<String> roles, 
			List<String> names, List<String> emails) throws IOException {

		StringBuilder result = new StringBuilder();

		final JsonArrayBuilder templateRoles = Json.createArrayBuilder().add(Json.createObjectBuilder()
				.add("name", names.get(0))
				.add("email", emails.get(0))
				.add("roleName", roles.get(0)));
		if(names.size() > 1 && emails.size() > 1 && roles.size() > 1) {
			templateRoles.add(Json.createObjectBuilder()
					.add("name", names.get(1))
					.add("email", emails.get(1))
					.add("roleName", roles.get(1)));
		}
		
		String body = Json.createObjectBuilder()
				.add("emailSubject", "DocuSign API Call - Signature request from template")
				.add("templateId", templateId)
				.add("status", "sent")
				.add("templateRoles", templateRoles)
				.build().toString();
		
		String url = "https://demo.docusign.net/restapi/v2/accounts/1273564/envelopes";
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("X-DocuSign-Authentication", getAuthHeader());
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-Length", Integer.toString(body.length()));
		conn.setDoOutput(true);
		// write body of the POST request 
		DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
		dos.writeBytes(body); 
		dos.flush(); 
		dos.close();
		
		result.append("Sending signature request from template...\n");
		result.append(body + "\n\n");
		int status = conn.getResponseCode(); // triggers the request
		if( status != 201 )	// 201 = Created
		{
			result.append("\nError description:  \n" + errorParse(conn, status));
			return result.toString();
		}
		
		result.append("-- Signature Request response --\n\n" + getResponseBody(conn));
		
		return result.toString();
	}
	
	/**
	 * Send a document for signature via template
	 * 
	 * @param pdf the base document
	 * @param templateId The overlay template
	 * @param roleName valid template role name
	 * @param recipientName recipient (signer) name
	 * @param recipientEmail recipient (signer) email	
	 * @throws IOException
	 */
	public String requestSignatureCompositeTemplate(File pdf, String templateId, List<String> roles, 
			List<String> names, List<String> emails) throws IOException {
		
		final String body = Json.createObjectBuilder()
				.add("emailSubject", "Brandon - testing template overlay")
				.add("status", "sent")
				.add("compositeTemplates", Json.createArrayBuilder().add(Json.createObjectBuilder()
						.add("serverTemplates", Json.createArrayBuilder().add(Json.createObjectBuilder()
								.add("sequence", "1")
								.add("templateId", templateId)))
						.add("inlineTemplates", Json.createArrayBuilder().add(Json.createObjectBuilder()
								.add("sequence", "1")
								.add("recipients", Json.createObjectBuilder()
										.add("signers", Json.createArrayBuilder().add(Json.createObjectBuilder()
												.add("email", emails.get(0))
												.add("name", names.get(0))
												.add("recipientId", "1")
												.add("roleName", roles.get(0)))))))
						.add("document", Json.createObjectBuilder()
								.add("documentId", "1")
								.add("name", pdf.getName()))))
				.build().toString();
		
		return multipartRequest(body, pdf);
	}
	
	/**
	 * Send Acord app for signature
	 * 
	 * @param pdf the Acord doc
	 * @param roleName valid template role name
	 * @param recipientName recipient (signer) name
	 * @param recipientEmail recipient (signer) email	
	 * @throws IOException
	 */
	public String requestSignatureAcordAppTemplate(File pdf, List<String> roles, List<String> names, 
			List<String> emails) throws IOException {
		
		final String templateId = "1EDD0786-6313-4789-81BF-CA60CECA41CC";
		
		final String body = Json.createObjectBuilder()
				.add("emailSubject", "Brandon TEST - ACORD app")
				.add("status", "sent")
				.add("compositeTemplates", Json.createArrayBuilder().add(Json.createObjectBuilder()
						.add("serverTemplates", Json.createArrayBuilder().add(Json.createObjectBuilder()
								.add("sequence", "1")
								.add("templateId", templateId)))
						.add("inlineTemplates", Json.createArrayBuilder().add(Json.createObjectBuilder()
								.add("sequence", "1")
								.add("recipients", Json.createObjectBuilder()
										.add("signers", Json.createArrayBuilder().add(Json.createObjectBuilder()
												.add("email", emails.get(0))
												.add("name", names.get(0))
												.add("recipientId", "1")
												.add("roleName", roles.get(0))
												.add("tabs", Json.createObjectBuilder().
														add("signHereTabs", Json.createArrayBuilder().add(Json.createObjectBuilder()
																.add("anchorString", "STATUS OF TRANSACTION")
																.add("anchorXOffset", "0")
																.add("anchorYOffset", "0")
																.add("anchorIgnoreIfNotPresent", "false")
																.add("anchorUnits", "pixels")))))))))
						.add("document", Json.createObjectBuilder()
								.add("documentId", "1")
								.add("name", pdf.getName()))))
				.build().toString();
		
		return multipartRequest(body, pdf);
	}
	
	/**
	 * Send Acord Application for signature
	 * 
	 * @param pdf the Acord pdf
	 * @param name the name of the recipient
	 * @param email the address to send the request to
	 * @return A string outlining the request and response
	 */
	public String requestSignatureAcordApp(final File pdf, final String name, final String email) throws IOException {
		
		final String body = Json.createObjectBuilder()
				.add("emailSubject", "TEST - Acord App")
				.add("status", "sent")
				.add("documents", Json.createArrayBuilder().add(Json.createObjectBuilder()
						.add("documentId", "1")
						.add("name", pdf.getName())))
				.add("recipients", Json.createObjectBuilder()
						.add("signers", Json.createArrayBuilder().add(Json.createObjectBuilder()
								.add("recipientId", "1")
								.add("name", name)
								.add("email", email)
								.add("tabs", Json.createObjectBuilder()
										.add("signHereTabs", Json.createArrayBuilder().add(Json.createObjectBuilder()
												.add("documentId", "1")
												.add("anchorString", "APPLICANT'S SIGNATURE")
												.add("anchorXOffset", "110")
												.add("anchorYOffset", "31")
												.add("anchorUnits", "pixels")
												.add("scaleValue", "0.6")))
										.add("dateSignedTabs", Json.createArrayBuilder().add(Json.createObjectBuilder()
												.add("documentId", "1")
												.add("anchorString", "APPLICANT'S SIGNATURE")
												.add("anchorXOffset", "550")
												.add("anchorYOffset", "12")
												.add("anchorUnits", "pixels")))
										))))
				.build().toString();
		
		return multipartRequest(body, pdf);
	}
	
	public static Tabs createAcordTabs(String documentId) {
		final SignHereTab[] signHereTabs = {
				new SignHereTab(documentId, "APPLICANT'S SIGNATURE", "110", "31", "pixels", "0.6"),
				new SignHereTab(documentId, "INSURED NAME - SIGNATURE", "10", "-1", "pixels", "0.6")};
		
		final DateSignedTab[] dateSignedTabs = {
				new DateSignedTab(documentId, "APPLICANT'S SIGNATURE", "550", "12", "pixels"),
				new DateSignedTab(documentId, "DATE SIGNED", "0", "-11", "pixels")};
		
		final FullNameTab[] fullNameTabs = {
				new FullNameTab(documentId, "INSURED NAME - PRINT", "0", "-11", "pixels")};
		
		return new Tabs(signHereTabs, dateSignedTabs, fullNameTabs);
	}
	
	public String requestSignature(Envelope envelope, List<DocumentVO> documents) throws IOException {
		return multipartRequest(gson.toJson(envelope), documents);
	}
	
	private String multipartRequest(String body, List<DocumentVO> documents) throws IOException {
		StringBuilder result = new StringBuilder();
		
		String url = "https://demo.docusign.net/restapi/v2/accounts/1273564/envelopes";
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("X-DocuSign-Authentication", getAuthHeader());
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=BOUNDARY");
		conn.setDoOutput(true);
		
		// start constructing the multipart/form-data request...
		String requestBody = "\r\n\r\n--BOUNDARY\r\n" + "Content-Type: application/json\r\n"
				+ "Content-Disposition: form-data\r\n" + "\r\n" + body; 
		
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		dos.writeBytes(requestBody);
		for(DocumentVO document : documents) {
			dos.writeBytes("\r\n\r\n--BOUNDARY\r\n" + 
					"Content-Type: application/pdf \r\n" + 
					"Content-Disposition: file; filename=\"" + document.getName() + 
					"\"; documentid=" + document.getId() + "\r\n" + "\r\n");
			
			InputStream inputStream = new FileInputStream(document.getFile());
			byte[] bytes = new byte[(int) document.getFile().length()];
			inputStream.read(bytes);
			inputStream.close();
			dos.write(bytes);
		}
		dos.writeBytes("\r\n" + "--BOUNDARY--\r\n\r\n");
		dos.flush();
		dos.close();
		
		result.append("Sending signature request from template...\n");
		result.append(requestBody);
		
		result.append("\n\n-- Signature Request response --\n\n");
		int status = conn.getResponseCode(); // triggers the request
		if( status == 201 ) {	// 201 = Created
			result.append(gson.toJson(getResponseBody(conn)));
		}
		else {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(errorParse(conn, status)).getAsJsonObject();
			result.append(gson.toJson(json));
		}
		
		return result.toString();
	}
	
	
	/**
	 * Send a multipart signature request
	 * 
	 * @param body the body part of the request (JSON string)
	 * @param pdf the document part of the request
	 * @return A string outlining the request and response
	 */
	private String multipartRequest(String body, File pdf) throws MalformedURLException, IOException {
		StringBuilder result = new StringBuilder();
		
		String url = "https://demo.docusign.net/restapi/v2/accounts/1273564/envelopes";
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("X-DocuSign-Authentication", getAuthHeader());
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=BOUNDARY");
		conn.setDoOutput(true);
		
		// read document content into byte array
		InputStream inputStream = new FileInputStream(pdf);
		byte[] bytes = new byte[(int) pdf.length()];
		inputStream.read(bytes);
		inputStream.close();
		
		// start constructing the multipart/form-data request...
		String requestBody = "\r\n\r\n--BOUNDARY\r\n" + "Content-Type: application/json\r\n"
				+ "Content-Disposition: form-data\r\n" + "\r\n" + body + "\r\n\r\n--BOUNDARY\r\n" + 
				"Content-Type: application/pdf \r\n" + "Content-Disposition: file; filename=\"" + pdf.getName()
				+ "\"; documentid=1\r\n" + "\r\n";
		
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		dos.writeBytes(requestBody);
		dos.write(bytes);
		dos.writeBytes("\r\n" + "--BOUNDARY--\r\n\r\n");
		dos.flush();
		dos.close();
		
		result.append("Sending signature request from template...\n");
		result.append(requestBody);
		
		result.append("\n\n-- Signature Request response --\n\n");
		int status = conn.getResponseCode(); // triggers the request
		if( status == 201 ) {	// 201 = Created
			result.append(gson.toJson(getResponseBody(conn)));
		}
		else {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(errorParse(conn, status)).getAsJsonObject();
			result.append(gson.toJson(json));
		}
		
		return result.toString();
	}
	
	private String getAuthHeader() {
		return	"<DocuSignCredentials>" + 
					"<Username>" + username + "</Username>" +
					"<Password>" + password + "</Password>" + 
					"<IntegratorKey>" + integratorKey + "</IntegratorKey>" + 
				"</DocuSignCredentials>";
	}
	
	private HttpURLConnection initializeRequest(String url, String method, String body, String httpAuthHeader) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();

			conn.setRequestMethod(method);
			conn.setRequestProperty("X-DocuSign-Authentication", httpAuthHeader);
			conn.setRequestProperty("Accept", "application/xml");
			if (method.equalsIgnoreCase("POST")) {
				conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=BOUNDARY");
				conn.setDoOutput(true);
			} else {
				conn.setRequestProperty("Content-Type", "application/xml");
			}
			return conn;

		} catch (Exception e) {
			// lazy code
			throw new RuntimeException(e);
		}
	}

	private String parseXMLBody(String body, String searchToken) {
		String xPathExpression;
		try {
			// we use xPath to parse the XML formatted response body
			xPathExpression = String.format("//*[1]/*[local-name()='%s']", searchToken);
			XPath xPath = XPathFactory.newInstance().newXPath();
			return (xPath.evaluate(xPathExpression, new InputSource(new StringReader(body))));
		} catch (Exception e) {
			// lazy code
			throw new RuntimeException(e);
		}
	}

	private String getResponseBody(HttpURLConnection conn) {
		BufferedReader br = null;
		StringBuilder body = null;
		String line = "";
		try {
			// we use xPath to get the baseUrl and accountId from the XML response body
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			body = new StringBuilder();
			while ((line = br.readLine()) != null)
				body.append(line);
			return body.toString();
		} catch (Exception e) {
			// lazy code
			throw new RuntimeException(e);
		}
	}

	private String errorParse(HttpURLConnection conn, int status) {
		BufferedReader br;
		String line;
		StringBuilder responseError;
		try {
			LOGGER.error("API call failed, status returned was: " + status);
			InputStreamReader isr = new InputStreamReader(conn.getErrorStream());
			br = new BufferedReader(isr);
			responseError = new StringBuilder();
			line = null;
			while ((line = br.readLine()) != null)
				responseError.append(line);
			return responseError.toString();
		} catch (Exception e) {
			// lazy code
			throw new RuntimeException(e);
		}
	}

	private String prettyFormat(String input, int indent) {
		try {
			Source xmlInput = new StreamSource(new StringReader(input));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);
			return xmlOutput.getWriter().toString();
		} catch (Exception e) {
			// lazy code
			throw new RuntimeException(e);
		}
	}

}
