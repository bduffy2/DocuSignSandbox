package com.stillwaterinsurance.docusign.vo;

public final class CompositeTemplate {
	
	public final ServerTemplate serverTemplates[];
	public final InlineTemplate inlineTemplates[];
	public final Document document;

	public CompositeTemplate(ServerTemplate[] serverTemplates, InlineTemplate[] inlineTemplates,
			Document document) {
		this.serverTemplates = serverTemplates;
		this.inlineTemplates = inlineTemplates;
		this.document = document;
	}

	public static class ServerTemplate {
		public final String sequence;
		public final String templateId;

		public ServerTemplate(String sequence, String templateId) {
			this.sequence = sequence;
			this.templateId = templateId;
		}
	}
	
	public static class OlderHomeServerTemplate extends ServerTemplate {
		public OlderHomeServerTemplate(String sequence) {
			super(sequence, "AC626C30-21BF-4BF7-9774-46B5EEEEF93D");
		}
	}

	public static final class InlineTemplate {
		public final String sequence;
		public final Recipients recipients;

		public InlineTemplate(String sequence, Recipients recipients) {
			this.sequence = sequence;
			this.recipients = recipients;
		}
	}
	
}
