package foursquare;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.Speechlet;


public class FourSquareSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

	 private static final Set<String> supportedApplicationIds;

	    static {
	        supportedApplicationIds = new HashSet<String>();
	        supportedApplicationIds.add("amzn1.ask.skill.d0820429-b600-4c14-a335-1a1e5b2095c6");
	    }

	    public FourSquareSpeechletRequestStreamHandler() {
	        super(new FoursquareVenuesSpeechlet(), supportedApplicationIds);
	    }

	    public FourSquareSpeechletRequestStreamHandler(Speechlet speechlet,
	            Set<String> supportedApplicationIds) {
	        super(speechlet, supportedApplicationIds);
	    }

	
}
