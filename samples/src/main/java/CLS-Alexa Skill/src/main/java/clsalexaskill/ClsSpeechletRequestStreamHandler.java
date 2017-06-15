package clsalexaskill;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class ClsSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
	private static final Set<String> supportedApplicationIds;

    static {
        supportedApplicationIds = new HashSet<String>();
        supportedApplicationIds.add("amzn1.ask.skill.064d9c8f-5f60-40c0-ae6a-1ead0366fa24");
    }

    public ClsSpeechletRequestStreamHandler() {
        super(new ClsSpeechlet(), supportedApplicationIds);
    }

    public ClsSpeechletRequestStreamHandler(Speechlet speechlet,
            Set<String> supportedApplicationIds) {
        super(speechlet, supportedApplicationIds);
    }

}
