package clsalexaskill;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;


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
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;

//import TidePoolerSpeechlet.CityDateValues;
//import TidePoolerSpeechlet.HighTideValues;



public class ClsSpeechlet implements Speechlet {

	private static final Logger log = LoggerFactory.getLogger(ClsSpeechlet.class);
	private static final String SLOT_DATE = "Date";
	private static final String ENDPOINT = "https://s3.amazonaws.com/clsalexatest/2017-06-14T13%3A53%3A41.765";
	private static final int MONTH_TWO_DIGIT_THRESHOLD = 10;
	public SpeechletResponse onIntent(final IntentRequest request, final Session session)
			throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());

		Intent intent = request.getIntent();
		String intentName = intent.getName();

		if ("OneshotCLSGrossValueIntent".equals(intentName)) {
			System.out.println("****in intent");
			return handleOneshotCLSGrossValueRequest(intent, session);
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
	private static class DateValues<L> {
		private final L speechValue;
		public DateValues(L speechValue) {
			this.speechValue = speechValue;
		}
	}

	private DateValues<String> getDateFromIntent(final Intent intent) {
		Slot dateSlot = intent.getSlot(SLOT_DATE);
		DateValues<String> dateObject;

		// slots can be missing, or slots can be provided but with empty value.
		// must test for both
		if (dateSlot == null || dateSlot.getValue() == null) {
			// default to today
			dateObject = new DateValues<String>("Today");
			return dateObject;
		} else {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date;
			try {
				date = dateFormat.parse(dateSlot.getValue());
			} catch (ParseException e) {
				date = new Date();
			}

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int month = calendar.get(Calendar.MONTH) + 1;
			String monthString =
					month < MONTH_TWO_DIGIT_THRESHOLD ? "0" + Integer.toString(month) : Integer
							.toString(month);
					int day = calendar.get(Calendar.DATE);
					String dayString =
							day < MONTH_TWO_DIGIT_THRESHOLD ? "0" + Integer.toString(day) : Integer
									.toString(day);
							String requestDay =
									"begin_date=" + calendar.get(Calendar.YEAR) + monthString + dayString
									+ "&range=24";

							dateObject =
									new DateValues<String>(ClsDateUtil.getFormattedDate(date));
							return dateObject;
		}
	}

	private SpeechletResponse handleOneshotCLSGrossValueRequest(final Intent intent, final Session session) {
		// Determine city, using default if none provided

		// Determine custom date
		DateValues<String> dateObject = getDateFromIntent(intent);

		// all slots filled, either from the user or by default values. Move to final request
		return makeClsReportRequest(dateObject);
	}
	private SpeechletResponse makeClsReportRequest(DateValues<String> date) {

		String speechOutput = "";

		InputStreamReader inputStream = null;
		BufferedReader bufferedReader = null;
		StringBuilder builder = new StringBuilder();
		try {
			String line;
			URL url = new URL(ENDPOINT);
			URLConnection urlc = url.openConnection();
			log.info("hey I reached here!!");
			urlc.setDoOutput(true);
			urlc.setAllowUserInteraction(false);


			DocumentBuilderFactory dbFactory = 	DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = parseXML(urlc.getInputStream());
			doc.getDocumentElement().normalize();
			System.out.println("Root Element:" +doc.getDocumentElement().getNodeName());
			NodeList nList= doc.getElementsByTagName("DailyStats");

			for (int i=0 ; i < nList.getLength(); i++)
			{
				Node nNode= nList.item(i);
				System.out.println("Current Element:" +nNode.getNodeName());
				if(nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) nNode;
					System.out.println("First Name:" +eElement.getElementsByTagName("GrossValuesSettledUSD").item(0).getTextContent());
					speechOutput = 
							"The Gross settlement value done by CLS is "
									+ eElement.getElementsByTagName("GrossValuesSettledUSD").item(0).getTextContent();
				}
			}
		}

		catch (IOException e) {
			// reset builder to a blank string
			builder.setLength(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("CLS Daily Stats");
		card.setContent(speechOutput);

		// Create the plain text output
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speechOutput);

		return SpeechletResponse.newTellResponse(outputSpeech, card);
	}

	private Document parseXML(InputStream stream)
			throws Exception
	{
		DocumentBuilderFactory objDocumentBuilderFactory = null;
		DocumentBuilder objDocumentBuilder = null;
		Document doc = null;
		try
		{
			objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

			doc = objDocumentBuilder.parse(stream);
		}
		catch(Exception ex)
		{
			throw ex;
		}       

		return doc;
	}

	private SpeechletResponse handleHelpRequest() {
		String repromptText = "For which day you would like to know the CLS stats?";
		String speechOutput =
				"I can tell you CLS Gross settlement for the day. "
						+ "Or if you would like to know the number of sides CLS settled for the day."
						+ "Or you can just say exit. " + repromptText;
		return newAskResponse(speechOutput, repromptText);
	};
	public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
			throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());
		return getWelcomeResponse();
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

	private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
		return newAskResponse(stringOutput, false, repromptText, false);
	}

	private SpeechletResponse newAskResponse(String stringOutput, boolean isOutputSsml,
			String repromptText, boolean isRepromptSsml) {
		OutputSpeech outputSpeech, repromptOutputSpeech;
		if (isOutputSsml) {
			outputSpeech = new SsmlOutputSpeech();
			((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);

		} else {
			outputSpeech = new PlainTextOutputSpeech();
			((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
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

	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
		// TODO Auto-generated method stub

		log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());

	}

}
