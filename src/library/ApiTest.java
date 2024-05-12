package library;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.json.XML;



public class ApiTest {
	
	public static void main(String[] args) throws Exception{
		String uri = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=" + URLEncoder.encode("bib.isbn all \"9782266274289\"", StandardCharsets.UTF_8) + "&recordSchema=dublincore";
		HttpRequest getRequest = HttpRequest.newBuilder()
				.uri(new URI(uri))
				.GET()
				.build();
		HttpClient httpclient = HttpClient.newHttpClient();
		HttpResponse<String> getResponse = httpclient.send(getRequest, BodyHandlers.ofString());		
		JSONObject obj = XML.toJSONObject(getResponse.body());
		String jsonPrettyPrintString = obj.toString(5);
        System.out.println(jsonPrettyPrintString);
	}
}
