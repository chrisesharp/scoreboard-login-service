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
import java.util.UUID;

import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GitHubAuth")
public class GitHubAuth extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    @ConfigProperty(name="GITHUB_APP_ID")
    private String key;

    @Inject
    @ConfigProperty(name="GITHUB_AUTH_CALLBACK_URL")
    private String gitHubAuthCallbackURL;

    private final static String githubURL = "https://github.com/login/oauth/authorize";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            UUID stateUUID = UUID.randomUUID();
            String state=stateUUID.toString();
            request.getSession().setAttribute("github", state);

            String githubAuthReqURL = githubURL 
                    + "?client_id="
                    + key
                    + "&redirect_url="
                    + gitHubAuthCallbackURL
                    + "&scope=user:email&state="
                    + state;
                    
            response.sendRedirect(githubAuthReqURL);

        } catch (Exception e) {
            throw new ServletException(e);
        }

    }

}
