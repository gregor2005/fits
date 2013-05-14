package edu.harvard.hul.ois.fits.consolidation.resolvconflicts;

public class Rule {

	private String toolName = "";
	private String toolVersion = "";
	private String field = "";
	private String mimetypeNeeded = "";
	private String fileVersionNeeded = "";

	public String getField() {
		return field;
	}

	public String getFileVersionNeeded() {
		return fileVersionNeeded;
	}

	public String getMimetypeNeeded() {
		return mimetypeNeeded;
	}

	public String getToolName() {
		return toolName;
	}

	public String getToolVersion() {
		return toolVersion;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setFileVersionNeeded(String fileVersionNeeded) {
		this.fileVersionNeeded = fileVersionNeeded;
	}

	public void setMimetypeNeeded(String mimetypeNeeded) {
		this.mimetypeNeeded = mimetypeNeeded;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public void setToolVersion(String toolVersion) {
		this.toolVersion = toolVersion;
	}
	
	@Override
	public String toString() {
		return mimetypeNeeded+","+fileVersionNeeded+","+field+","+toolName+","+toolVersion;
	}

}
