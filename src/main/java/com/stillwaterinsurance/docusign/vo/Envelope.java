package com.stillwaterinsurance.docusign.vo;

public final class Envelope {
	
	public final String emailSubject;
	public final String status;
	public final Document documents[];
	public final Recipients recipients;
	public final String templateId;
	public final TemplateRoles[] templateRoles;
	public final CompositeTemplate[] compositeTemplates;

	public Envelope(String emailSubject, String status, Document[] documents, Recipients recipients) {
		this.emailSubject = emailSubject;
		this.status = status;
		this.documents = documents;
		this.recipients = recipients;
		this.templateId = null;
		this.templateRoles = null;
		this.compositeTemplates = null;
	}
	
	public Envelope(String emailSubject, String status, String templateId, TemplateRoles[] templateRoles) {
		this.emailSubject = emailSubject;
		this.status = status;
		this.documents = null;
		this.recipients = null;
		this.templateId = templateId;
		this.templateRoles = templateRoles;
		this.compositeTemplates = null;
	}
	
	public Envelope(String emailSubject, String status, CompositeTemplate[] compositeTemplates) {
		this.emailSubject = emailSubject;
		this.status = status;
		this.documents = null;
		this.recipients = null;
		this.templateId = null;
		this.templateRoles = null;
		this.compositeTemplates = compositeTemplates;
	}
	
}
