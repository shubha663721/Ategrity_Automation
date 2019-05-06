package com.majesco.itaf.rest.service;

//import iTAFSeleniumWeb.Automation;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.majesco.itaf.main.Config;

public class RestService {

	public static String getRestResponse(MediaType mediaType, HttpMethod httpMethod, String url, String input) {
		RestTemplate restTemplate = getRestTemplate();//new RestTemplate();
/*		//restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		List<HttpMessageConverter<?>> c = restTemplate.getMessageConverters();
        for(HttpMessageConverter<?> mc :c){
            if (mc instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter mcc = (StringHttpMessageConverter) mc;
                mcc.setWriteAcceptCharset(false);
            }
        }
*/		String response = null;
		
		HttpHeaders headers = new HttpHeaders();
		List<MediaType> accept = new ArrayList<MediaType>();
		accept.add(mediaType);
		headers.add("Accept-Encoding","gzip,deflate");
		
		headers.setAccept(accept);
		headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
		
		
		if(MediaType.APPLICATION_JSON.equals(mediaType)){
			headers.setContentType(MediaType.APPLICATION_JSON);
			
		}
		else if(MediaType.APPLICATION_XML.equals(mediaType)){
			headers.setContentType(MediaType.APPLICATION_XML);
		}
		else if(MediaType.TEXT_PLAIN.equals(mediaType)){
			headers.setContentType(MediaType.TEXT_PLAIN);
		}
		
		
		switch(httpMethod){
			case GET:
			{
				response = restTemplate.getForObject(url, String.class);
				break;
			}
			case POST:
			{
				URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();
				try {
					String inputJson = input.replaceAll(" ", "\\s");//remove this if the parser at server can accept spaces in the values.
					HttpEntity<String> request = new HttpEntity<String>(inputJson, headers);
					 response = restTemplate.postForObject(uri, request , String.class);
					 //response = restTemplate.exchange(url, HttpMethod.POST, request , String.class).getBody();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				break;
			}
			default:
				System.out.println("Invalid request type");
		}
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends RestTemplate> T getRestTemplate(){
		T t = null;
		Boolean oAuthEnabled = new Boolean(Config.authEnabled);
		String url = Config.authTokenURL;
		if(oAuthEnabled){
			AccessTokenRequest tokenRequest = new DefaultAccessTokenRequest();
			OAuth2RestTemplate template = null;
			
			try{
				ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
	
		        List<String> scopes = new ArrayList<String>(1);
		        scopes.add(Config.authScope);
		        resource.setUsername(Config.authTokenUsername);
		        resource.setPassword(Config.authTokenPassword);
		        resource.setClientId(Config.authTokenClientId);
		        resource.setClientSecret(Config.authTokenClientSecret);
		        resource.setGrantType("password");
		        resource.setAccessTokenUri(url);
		        resource.setClientAuthenticationScheme(AuthenticationScheme.header);
		        resource.setScope(scopes);
	
		        template = new OAuth2RestTemplate(resource, new DefaultOAuth2ClientContext(tokenRequest));
		        t = (T) template;
			}catch(Exception e){
				e.printStackTrace();				
			}
		}else{
			t = (T)new RestTemplate();
		}
		
		return t;
	}
	
	

}
