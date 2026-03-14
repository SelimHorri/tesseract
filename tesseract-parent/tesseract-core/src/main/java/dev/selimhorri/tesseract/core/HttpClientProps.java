package dev.selimhorri.tesseract.core;

import java.time.Duration;

public interface HttpClientProps {
	
	Duration connectTimeout();
	
	Duration readTimeout();
	
	int httpVersion();
	
	String sslBundleName();
	
	boolean equals(Object obj);
	
	int hashCode();
	
	String toString();
	
}

