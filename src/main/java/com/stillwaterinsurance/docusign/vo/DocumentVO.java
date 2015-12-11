package com.stillwaterinsurance.docusign.vo;

import java.io.File;
import java.io.Serializable;

public final class DocumentVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String id;
	private final File file;
	private final String name;
	
	public DocumentVO(String id, File file, String name) {
		this.id = id;
		this.file = file;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "DocumentVO [id=" + id + ", file=" + file + ", name=" + name + "]";
	}
	
	public String getId() {
		return id;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getName() {
		return name;
	}
	
	
}
