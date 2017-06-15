package alexaS3xml;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import java.net.URL;
import java.net.URLConnection;



public class alexas3xp implements Speechlet {

	private static final Logger log = LoggerFactory.getLogger(alexas3xp.class);

	private static final String ENDPOINT = "https://s3.amazonaws.com/pcttestb8914c57-d3f1-4b03-bdd8-7c3b7a4b742a/Hello.txt";

	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
		// TODO Auto-generated method stub
		
		log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());
		
	}

	public SpeechletResponse onIntent(final IntentRequest request, final Session session)
			throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());

		Intent intent = request.getIntent();
		String intentName = intent.getName();

		if ("Oneshotgetclsnews".equals(intentName)) {
			System.out.println("****in intent");
			return handleOneshotalexas3Request(intent, session);
		}else if ("AMAZON.HelpIntent".equals(intentName)) {
			return handleHelpRequest();
		} else if ("AMAZON.StopIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Goodbye");
			return SpeechletResponse.newTellResponse(outputSpeech);
		} else if ("AMAZON.CancelIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Goodbye");

			return SpeechletResponse.newTellResponse(outputSpeech);
		} else {
			throw new SpeechletException("Invalid Intent");
		}
	}


	private SpeechletResponse handleOneshotalexas3Request(final Intent intent, final Session session) {

		
		try {
			return makefilereadrequest();
		} catch (Exception e) {
			String speechOutput =
					"Seems like the file is not present"
							+ "Please repeat again";

			return newAskResponse(speechOutput, speechOutput);
		}

	}
	
	

	

	private SpeechletResponse makefilereadrequest(){
	
		String speechOutput = "";

		System.out.println("*******Hello!!");
		StringBuilder builder = new StringBuilder();
		try {
			System.out.println("Endpoint is:" +ENDPOINT);
			URL url = new URL(ENDPOINT);
			URLConnection urlc = url.openConnection();
			log.info("hey I reached here!!");
			urlc.setDoOutput(true);
			urlc.setAllowUserInteraction(false);
//			Scanner s = new Scanner(url.openStream());
//			System.out.println("Scanner output is:" +s);
//			log.info("Scanner output is:" +s);
		    BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
		        String l = null;
		        while ((l=br.readLine())!=null) {
				        System.out.println("text is"+l);
				        speechOutput =l;
		        }
//		        System.out.println("text is"+l);
//		        in.close();
//				    speechOutput =l;
//					System.out.println("********speechOutput:******" +speechOutput);
					
//					log.info("SpeechOut is:"+speechOutput);
				

			}

		 catch (IOException e) {
			// reset builder to a blank string
			builder.setLength(0);
		} 
		
		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("CLSOutput");
		card.setContent(speechOutput);

		// Create the plain text output
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speechOutput);

		return SpeechletResponse.newTellResponse(outputSpeech, card);
	}




	

	private SpeechletResponse getWelcomeResponse() {
		String speechOutput = "<speak>"
				+ "Welcome to CLS News Feed. "
				+ "</speak>";  
		String repromptText =
				
				"CLS Group (CLS), a leading provider of risk management and operational services for the global foreign exchange (FX) market," 
			  + "has announced its intent to release a payment netting service, CLS Netting, for buy-side and sell-side institutions’ FX trades that are settled outside the CLS settlement service."
				;

		return newAskResponse(speechOutput, true, repromptText, false);
	}
	
	private SpeechletResponse handleHelpRequest() {
		String repromptText = "For which venue you would like to know the nearby restaurants?";
		String speechOutput =
				"I can lead you through providing a city and "
						+ "or you can simply open Foursquare and ask a question like, "
						+ "get the nearby restaurants available in Edison for pastry. "
						+ "Or you can say exit. " + repromptText;
		 	return newAskResponse(speechOutput, repromptText);
	};
	
	public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
			throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());
		return getWelcomeResponse();
	}
	private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
		return newAskResponse(stringOutput, false, repromptText, false);
	}
	
	private SpeechletResponse newAskResponse(String stringOutput, boolean isOutputSsml,
			String repromptText, boolean isRepromptSsml) {
		OutputSpeech outputSpeech, repromptOutputSpeech;
		if (isOutputSsml) {
			outputSpeech = new SsmlOutputSpeech();
			((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);
//			System.out.println("********************SSML111111111111*************************);
			
		} else {
			outputSpeech = new PlainTextOutputSpeech();
			((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
//			System.out.println("********************SSML222222222222*************************);
		}

		if (isRepromptSsml) {
			repromptOutputSpeech = new SsmlOutputSpeech();
			((SsmlOutputSpeech) repromptOutputSpeech).setSsml(stringOutput);
		} else {
			repromptOutputSpeech = new PlainTextOutputSpeech();
			((PlainTextOutputSpeech) repromptOutputSpeech).setText(repromptText);
		}

		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(repromptOutputSpeech);
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
	}

		
	public void onSessionEnded(final SessionEndedRequest request, final Session session)
			throws SpeechletException {
		log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());
	}

	

}
