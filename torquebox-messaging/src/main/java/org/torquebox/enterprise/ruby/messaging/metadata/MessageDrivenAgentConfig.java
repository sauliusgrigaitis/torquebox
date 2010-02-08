package org.torquebox.enterprise.ruby.messaging.metadata;

public class MessageDrivenAgentConfig {
	
	private String rubyClassName;
	private String destinationName;

	public MessageDrivenAgentConfig() {
	}
	
	public void setRubyClassName(String rubyClassName) {
		this.rubyClassName = rubyClassName;
	}
	
	public String getRubyClassName() {
		return this.rubyClassName;
	}
	
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	
	public String getDestinationName() {
		return this.destinationName;
	}

}
