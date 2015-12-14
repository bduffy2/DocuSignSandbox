package com.stillwaterinsurance.docusign.vo;

public final class Recipients {
	
	public final Signer signers[];

	public Recipients(Signer[] signers) {
		this.signers = signers;
	}
	
	public Recipients(Signer signer) {
		this.signers = new Signer[]{signer};
	}
	
	public static class Signer {
		
		public final String recipientId;
		public final String name;
		public final String email;
		public final String roleName;
		public final Tabs tabs;

		public Signer(String recipientId, String name, String email, String roleName, Tabs tabs) {
			this.recipientId = recipientId;
			this.name = name;
			this.email = email;
			this.roleName = roleName;
			this.tabs = tabs;
		}
		
		public Signer(String name, String email, String roleName, Tabs tabs) {
			this.recipientId = "1";
			this.name = name;
			this.email = email;
			this.roleName = roleName;
			this.tabs = tabs;
		}
		
	}
	
}
