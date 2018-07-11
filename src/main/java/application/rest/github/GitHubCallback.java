/*******************************************************************************
 * Copyright (c) 2015 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package application.rest.github;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayInputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import javax.json.JsonReader;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.Json;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;

import application.auth.JwtIssuerServlet;

@WebServlet("/GitHubAuth/callback")
public class GitHubCallback extends JwtIssuerServlet {
    private static final long serialVersionUID = 1L;

    @Resource(lookup = "gitHubOAuthKey")
    private String key;
    @Resource(lookup = "gitHubOAuthSecret")
    private String secret;
    
    @Resource(lookup = "authCallbackURLSuccess")
    String callbackSuccess;
    @Resource(lookup = "authCallbackURLFailure")
    String callbackFailure;
    
    @Resource(lookup = "gitHubAuthCallbackURL")
    private String gitHubAuthCallbackURL;
    
    private HttpRequestFactory requestFactory;

    public GitHubCallback() {
        super();
    }

    @PostConstruct
    private void verifyInit() {
        if (callbackSuccess == null) {
            System.err.println("Error finding webapp base URL; please set this in your environment variables!");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String githubAuthCode = request.getParameter("code");
        String state = (String) request.getSession().getAttribute("github");
        Map<String, String> claims;

        try {
          requestFactory = new NetHttpTransport.Builder().doNotValidateCertificate().build().createRequestFactory();

          GenericUrl accessRequestURL = new GenericUrl("https://github.com/login/oauth/access_token");
          accessRequestURL.put("client_id", key);
          accessRequestURL.put("client_secret", secret);
          accessRequestURL.put("code", githubAuthCode);
          accessRequestURL.put("redirect_uri", gitHubAuthCallbackURL);
          accessRequestURL.put("state", state);

          HttpResponse githubResponse = requestFactory.buildGetRequest(accessRequestURL).execute();
          
          if(githubResponse.isSuccessStatusCode()){
            String accessToken = getAccessTokenFromResponse(githubResponse);
            claims = makeClaimsForIdentity(accessToken);
            response.sendRedirect(createJwtWithClaims(claims));
          } else {
            response.sendRedirect(callbackFailure);
          }
        } catch (GeneralSecurityException e) {
            throw new ServletException(e);
        }
    }
    
    private String getAccessTokenFromResponse(HttpResponse githubResponse) throws IOException {
      List<NameValuePair> parameters = URLEncodedUtils.parse(githubResponse.parseAsString(), Charset.forName("UTF-8"));
      String token = null;
      for(NameValuePair param : parameters){
          if("access_token".equals(param.getName())){
              token = param.getValue();
          }
      }
      return token;
    }
    
    private Map<String, String> makeClaimsForIdentity(String token) throws IOException {
      Map<String, String> claims = null;
      if(token!=null){
          HttpResponse userProfile = sendRequestWithToken(token, "https://api.github.com/user");
          
          if(userProfile.isSuccessStatusCode()) {
              JsonObject profile = readJsonObject(userProfile.parseAsString());

              claims = new HashMap<String,String>();
              claims.put("valid", "true");
              claims.put("id", "github:" + profile.get("id").toString());
              claims.put("name", profile.get("login").toString());

              HttpResponse emails = sendRequestWithToken(token, "https://api.github.com/user/emails");
              
              if(emails.isSuccessStatusCode()){
                JsonArray emailArray = readJsonArray(emails.parseAsString());
                if(emailArray != null){
                  for (JsonObject email: emailArray.toArray(new JsonObject[0])) {
                    Boolean primary = Boolean.valueOf(email.getBoolean("primary"));
                    if (primary) {
                      claims.put("email", email.getString("email"));
                      claims.put("upn", email.getString("email"));
                    }
                  }
                } else {
                  claims.put("email","unknown");
                }
              }
              claims.put("groups","player");
          }
      }
      return claims;
    }

    private String createJwtWithClaims(Map<String,String> claims) throws IOException {
      if (claims != null) {
        String jwt = createJwt(claims);
        System.out.println("New User Authed: " + claims.get("name"));
        return (callbackSuccess + "/" + jwt);
      }
      return callbackFailure;
    }
    
    private JsonObject readJsonObject(String json) {
      JsonReader jsonReader = Json.createReader(
        new ByteArrayInputStream(json.getBytes())
        );
      JsonObject object = jsonReader.readObject();
      jsonReader.close();
      return object;
    }
    
    private JsonArray readJsonArray(String json) {
      JsonReader jsonReader = Json.createReader(
        new ByteArrayInputStream(json.getBytes())
        );
      JsonArray array = jsonReader.readArray();
      jsonReader.close();
      return array;
    }
    
    private HttpResponse sendRequestWithToken(String token, String url) throws IOException{
      GenericUrl query = new GenericUrl(url);
      query.put("access_token", token);
      return requestFactory.buildGetRequest(query).execute();
    }
}
