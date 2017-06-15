package foursquare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import com.amazonaws.util.json.JSONArray;
//import com.amazonaws.util.json.JSONException;
//import com.amazonaws.util.json.JSONObject;
//import com.amazonaws.util.json.JSONTokener;
import org.json.JSONTokener;







public class FoursquareVenuesSpeechlet implements Speechlet {

	private static final String SLOT_CITY = "City";
	private static final String SLOT_SEARCHKEY = "Search";
	private static final Logger log = LoggerFactory.getLogger(FoursquareVenuesSpeechlet.class);

	private static final String ENDPOINT = "https://api.foursquare.com/v2/venues/search";

	public void onSessionStarted(final SessionStartedRequest request, final Session session)
			throws SpeechletException {
		log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());

		// any initialization logic goes here
	}

	public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
			throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());

		return getWelcomeResponse();
	}

	public void onSessionEnded(final SessionEndedRequest request, final Session session)
			throws SpeechletException {
		log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());
	}

	private SpeechletResponse getWelcomeResponse() {
		String whichCityPrompt = "For which venue you would like to know the nearby restaurants ?";
		String speechOutput = "<speak>"
				+ "Welcome to Foursquare. "
				+ whichCityPrompt
				+ "</speak>"; 
		String repromptText =
				"I can lead you through providing a city and "
						+ "or you can simply open Foursquare and ask a question like, "
						+ "get the nearby restaurants available in Edison. "
						+ whichCityPrompt;

		return newAskResponse(speechOutput, true, repromptText, false);
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
	private SpeechletResponse handleHelpRequest() {
		String repromptText = "For which venue you would like to know the nearby restaurants?";
		String speechOutput =
				"I can lead you through providing a city and "
						+ "or you can simply open Foursquare and ask a question like, "
						+ "get the nearby restaurants available in Edison for pastry. "
						+ "Or you can say exit. " + repromptText;

		return newAskResponse(speechOutput, repromptText);
	}

	private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
		return newAskResponse(stringOutput, false, repromptText, false);
	}


	private SpeechletResponse makevenuerequest(CitySearchValues<String, String> citySearch) throws JSONException {
		//https://api.foursquare.com/v2/venues/search?near=edison&query=pastry&oauth_token=0KAKWZJ0R11DRVSY2JR0WVYRD55PARANMLEOYM45MLHJSCK3&v=20170607
		String queryString =
				String.format("?near=%s&query=%s&oauth_token=0KAKWZJ0R11DRVSY2JR0WVYRD55PARANMLEOYM45MLHJSCK3&v=20170607",citySearch.Near, citySearch.Query);


		String speechOutput = "";

		InputStreamReader inputStream = null;
		BufferedReader bufferedReader = null;
		StringBuilder builder = new StringBuilder();
		try {
			String line;
			URL url = new URL(ENDPOINT + queryString);
			log.info("URL formed is:", url);
			inputStream = new InputStreamReader(url.openStream(), Charset.forName("US-ASCII"));
			bufferedReader = new BufferedReader(inputStream);

			while ((line = bufferedReader.readLine()) != null) {

				builder.append(line);
				JSONObject  responseObject= new JSONObject(new JSONTokener(line.toString()));
				System.out.println("THIS IS MY OBJECT:" +responseObject);


				List<String> listOfVenues=new ArrayList<String>();
				listOfVenues=findNearByValues(responseObject);
				System.out.println("*******List of Venues is:******** "+listOfVenues);
				StringBuilder sb = new StringBuilder();
					
					for (String s : listOfVenues)
					{
					    sb.append(s);
					    sb.append(",");
					}
					String speechOutput1 = sb.toString();
					speechOutput = speechOutput1.replaceAll("&", "and");
					System.out.println("********speechOutput:******" +speechOutput);
					
					log.info("SpeechOut is:"+speechOutput);
					


			}

		} catch (IOException e) {
			// reset builder to a blank string
			builder.setLength(0);
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(bufferedReader);
		}
		if (builder.length() == 0) {
			speechOutput =
					"Sorry, the Foursquare service is experiencing a problem. "
							+ "Please try again later.";
		} else {
			try {
				JSONObject FoursqResponseObject = new JSONObject(new JSONTokener(builder.toString()));
			}
			catch(JSONException e)
			{
				  log.error("Exception occoured while parsing service response.", e);
			}
			finally
			{
				log.error("cz Eclipse is forcing me to put finally!!");
			}
		}
		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("FourSquare");
		card.setContent(speechOutput);

		// Create the plain text output
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speechOutput);

		return SpeechletResponse.newTellResponse(outputSpeech, card);
	}

	private static List<String> findNearByValues(JSONObject responseObject) {

		JSONArray venues;
		JSONObject response = null;
		List<String> returnList=new ArrayList<String>();
		
		try {

			response = responseObject.getJSONObject("response"); // skip if

			System.out.println("response is:" +response);													
			venues = response.getJSONArray("venues");
			System.out.println("venues:" +venues);

			JSONObject childJSONObject = null;
			String name = null;

			for (int i = 0; i < venues.length(); i++) {

				// This iterates over every object in the json
				childJSONObject = venues.getJSONObject(i);
				System.out.println("childJSONObject:" +childJSONObject);
				name = childJSONObject.getString("name");
				if(name!=null)
				returnList.add(name);
				log.info("Return List is:" +returnList);
			}
			

		} catch (Exception e) {
			 log.error("Exception occoured in find nearby values.", e); 
			e.printStackTrace();
		}
		return returnList;
	}

	public SpeechletResponse onIntent(final IntentRequest request, final Session session)
			throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());

		Intent intent = request.getIntent();
		String intentName = intent.getName();

		if ("OneshotFourSquareIntent".equals(intentName)) {
			return handleOneshotvenuesRequest(intent, session);
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

	private static class CitySearchValues<L, R> {
		private final L Near;
		private final R Query;

		public CitySearchValues(L Near, R Query) {
			this.Near = Near;
			this.Query = Query;
		}
	}

	private CitySearchValues<String, String> getCitySearchFromIntent(final Intent intent,
			final boolean assignDefault) throws Exception {
		Slot citySlot = intent.getSlot(SLOT_CITY);
		Slot searchSlot = intent.getSlot(SLOT_SEARCHKEY);
		CitySearchValues<String, String> citySearchObject = null;
		String cityName = citySlot.getValue();
		String searchKey = searchSlot.getValue();

		citySearchObject = new CitySearchValues<String, String>(cityName, searchKey);

		return citySearchObject;
	}

	private SpeechletResponse handleOneshotvenuesRequest(final Intent intent, final Session session) {

		CitySearchValues<String, String> citysearchObject = null;
		try {
			citysearchObject = getCitySearchFromIntent(intent, true);
		} catch (Exception e) {
			String speechOutput =
					"Seems like an unrecognized city or the keyword"
							+ "Please repeat again";

			return newAskResponse(speechOutput, speechOutput);
		}

		// all slots filled, either from the user or by default values. Move to final request
		return getFinalvenueResult(citysearchObject);
	}

	private SpeechletResponse getFinalvenueResult(CitySearchValues<String, String> citySearch) {
		try {
			System.out.println("makevenuerequest(citySearch):" +makevenuerequest(citySearch));
			return makevenuerequest(citySearch);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

