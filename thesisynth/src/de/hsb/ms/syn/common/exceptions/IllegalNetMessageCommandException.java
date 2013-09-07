package de.hsb.ms.syn.common.exceptions;

public class IllegalNetMessageCommandException extends Exception {
	
	private static final long serialVersionUID = -5953361198415982848L;

	public IllegalNetMessageCommandException(String type) {
		super("This NetMessage command is not supported: " + type);
	}
}
