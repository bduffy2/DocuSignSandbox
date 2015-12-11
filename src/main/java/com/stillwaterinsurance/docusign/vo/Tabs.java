package com.stillwaterinsurance.docusign.vo;

public final class Tabs {
	public final SignHereTab signHereTabs[];
	public final DateSignedTab dateSignedTabs[];
	public final FullNameTab fullNameTabs[];

	public Tabs(SignHereTab[] signHereTabs, DateSignedTab[] dateSignedTabs, FullNameTab[] fullNameTabs) {
		this.signHereTabs = signHereTabs;
		this.dateSignedTabs = dateSignedTabs;
		this.fullNameTabs = fullNameTabs;
	}

	public static final class SignHereTab {
		public final String documentId;
		public final String anchorString;
		public final String anchorXOffset;
		public final String anchorYOffset;
		public final String anchorUnits;
		public final String scaleValue;

		public SignHereTab(String documentId, String anchorString, String anchorXOffset,
				String anchorYOffset, String anchorUnits, String scaleValue) {
			this.documentId = documentId;
			this.anchorString = anchorString;
			this.anchorXOffset = anchorXOffset;
			this.anchorYOffset = anchorYOffset;
			this.anchorUnits = anchorUnits;
			this.scaleValue = scaleValue;
		}
	}

	public static final class DateSignedTab {
		public final String documentId;
		public final String anchorString;
		public final String anchorXOffset;
		public final String anchorYOffset;
		public final String anchorUnits;

		public DateSignedTab(String documentId, String anchorString, String anchorXOffset,
				String anchorYOffset, String anchorUnits) {
			this.documentId = documentId;
			this.anchorString = anchorString;
			this.anchorXOffset = anchorXOffset;
			this.anchorYOffset = anchorYOffset;
			this.anchorUnits = anchorUnits;
		}
	}

	public static final class FullNameTab {
		public final String documentId;
		public final String anchorString;
		public final String anchorXOffset;
		public final String anchorYOffset;
		public final String anchorUnits;

		public FullNameTab(String documentId, String anchorString, String anchorXOffset,
				String anchorYOffset, String anchorUnits) {
			this.documentId = documentId;
			this.anchorString = anchorString;
			this.anchorXOffset = anchorXOffset;
			this.anchorYOffset = anchorYOffset;
			this.anchorUnits = anchorUnits;
		}
	}
}
