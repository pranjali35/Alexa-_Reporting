package alexaS3xml;


import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.Speechlet;


public class AlexaS3SpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

	 private static final Set<String> supportedApplicationIds;

	    static {
	        supportedApplicationIds = new HashSet<String>();
	        supportedApplicationIds.add("amzn1.ask.skill.40945e12-7fe0-433a-b681-e5f8ff5f3f19");
	    }

	    public AlexaS3SpeechletRequestStreamHandler() {
	        super(new alexas3xp(), supportedApplicationIds);
	    }

	    public AlexaS3SpeechletRequestStreamHandler(Speechlet speechlet,
	            Set<String> supportedApplicationIds) {
	        super(speechlet, supportedApplicationIds);
	    }

	
}

