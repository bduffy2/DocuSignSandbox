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
import java.net.URL;
import java.util.List;

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

@Service
public class DocuSignService {
	
	private static final Logger LOGGER = Logger.getLogger(DocuSignService.class);
	
	@Value("${credentials.integratorKey}")
	private String integratorKey;
	
	@Value("${credentials.username}")
	private String username;
	
	@Value("${credentials.password}")
	private String password;

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
		
		// construct the DocuSign authentication header
		String authenticationHeader = 
				"<DocuSignCredentials>" + 
					"<Username>" + username + "</Username>" +
					"<Password>" + password + "</Password>" + 
					"<IntegratorKey>" + integratorKey + "</IntegratorKey>" + 
				"</DocuSignCredentials>";

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
		conn = initializeRequest(url, "GET", body, authenticationHeader);

		// send the request
		result.append("STEP 1:  Sending Login request...\n");
		status = conn.getResponseCode();
		if (status != 200) // 200 = OK
		{
			result.append(errorParse(conn, status));
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
		conn = initializeRequest(url, "POST", body, authenticationHeader);

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
		
		// first we construct the DocuSign authentication header (can be XML or JSON format)
		String authenticationHeader = 
					"<DocuSignCredentials>" + 
						"<Username>" + username + "</Username>" +
						"<Password>" + password + "</Password>" + 
						"<IntegratorKey>" + integratorKey + "</IntegratorKey>" + 
					"</DocuSignCredentials>";
		
		// additional variable declarations
		String baseURL = ""; // we will retrieve this through the Login API call
		String accountId = ""; // we will retrieve this through the Login API call
		String envelopeId = ""; // generated from signature request API call
		String url = ""; // end-point for each api call
		String body = ""; // request body
		String response = ""; // response body
		int status; // response status
		HttpURLConnection conn = null;	// connection object used for each request
		
		//============================================================================
		// STEP 1 - Make the Login API call to retrieve your baseUrl and accountId
		//============================================================================
		
		url = "https://demo.docusign.net/restapi/v2/login_information";
		body = "";	// no request body for the login call
		
		// create connection object, set request method, add request headers
		conn = initializeRequest(url, "GET", body, authenticationHeader);
		
		// send the request
		result.append("STEP 1:  Sending Login request...\n");
		status = conn.getResponseCode();
		if( status != 200 )	// 200 = OK
		{
			errorParse(conn, status);
			return result.toString();
		}
		
		// obtain baseUrl and accountId values from response body 
		response = getResponseBody(conn);
		baseURL = parseXMLBody(response, "baseUrl");
		accountId = parseXMLBody(response, "accountId");
		result.append("-- Login response --\n\n" + prettyFormat(response, 2) + "\n");
		
		//============================================================================
		// STEP 2 - Signature Request from Template API Call
		//============================================================================
		
		url = baseURL + "/envelopes";	// append "/envelopes" to baseUrl for signature request call
		
		// this example uses XML formatted requests, JSON format is also accepted
		body = "<envelopeDefinition xmlns=\"http://www.docusign.com/restapi\">" +
					"<accountId>" + accountId + "</accountId>" +
					"<status>sent</status>" + 	// "sent" to send envelope, "created" to save as draft envelope
					"<emailSubject>DocuSign API Call - Signature request from template</emailSubject>" +
					"<templateId>" + templateId + "</templateId>" + 
					"<templateRoles>" + 
						"<templateRole>" + 	
							"<name>" + names.get(0) + "</name>" +
							"<email>" + emails.get(0) + "</email>" +
							"<roleName>" + roles.get(0) + "</roleName>" + 
						"</templateRole>";
		if(names.size() > 1 && emails.size() > 1 && roles.size() > 1) {
			body += 	"<templateRole>" + 	
							"<name>" + names.get(1) + "</name>" +
							"<email>" + emails.get(1) + "</email>" +
							"<roleName>" + roles.get(1) + "</roleName>" + 
						"</templateRole>";
		}
		body += 	"</templateRoles>" + 
				"</envelopeDefinition>";
		
		conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("X-DocuSign-Authentication", authenticationHeader);
		conn.setRequestProperty("Content-Type", "application/xml");
		conn.setRequestProperty("Accept", "application/xml");
		conn.setRequestProperty("Content-Length", Integer.toString(body.length()));
		conn.setDoOutput(true);
		// write body of the POST request 
		DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
		dos.writeBytes(body); dos.flush(); dos.close();
		
		result.append("STEP 2:  Sending signature request from template...\n");
		status = conn.getResponseCode(); // triggers the request
		if( status != 201 )	// 201 = Created
		{
			errorParse(conn, status);
			return result.toString();
		}
		
		// obtain envelopeId value from response body 
		response = getResponseBody(conn);
		envelopeId = parseXMLBody(response, "envelopeId");
		result.append("-- Signature Request response --\n\n" + prettyFormat(response, 2));
		result.append("Signature request has been sent to " + emails  + "!\nEnvelopeId is:  " + envelopeId  + "\n");
		
		return result.toString();
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
			//TODO - this is terribly lazy code
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
			//TODO - this is terribly lazy code
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
			//TODO - this is terribly lazy code
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
			return "\nError description:  \n" + prettyFormat(responseError.toString(), 2);
		} catch (Exception e) {
			//TODO - this is terribly lazy code
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
			//TODO - this is terribly lazy code
			throw new RuntimeException(e);
		}
	}

}
