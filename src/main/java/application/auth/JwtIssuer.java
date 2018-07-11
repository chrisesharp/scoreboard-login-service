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
package application.auth;

import java.io.IOException;
import java.security.Key;

import java.util.Calendar;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtIssuer {

    public static String createSignedJwt(Key signingKey, Map<String, String> claims) throws IOException {

        Claims onwardsClaims = Jwts.claims();
        onwardsClaims.putAll(claims);
        onwardsClaims.setSubject(claims.get("id"));

        // We'll use this claim to know this is a user token
        onwardsClaims.setAudience("scoreboard");
        
        onwardsClaims.setIssuer("http://openliberty.io");

        // we set creation time to 24hrs ago, to avoid timezone issues in the
        // browser verification of the jwt.
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.HOUR, -24);
        onwardsClaims.setIssuedAt(calendar1.getTime());

        // client JWT has 24 hrs validity from now.
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.HOUR, 24);
        onwardsClaims.setExpiration(calendar2.getTime());

        return Jwts.builder()
                .setClaims(onwardsClaims)
                .signWith(SignatureAlgorithm.RS256, signingKey)
                .compact();
    }
}
