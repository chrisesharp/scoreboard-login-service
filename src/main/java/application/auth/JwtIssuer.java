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
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtIssuer {

    public static String createSignedJwt(Key signingKey, Map<String, String> claims) throws IOException {

        Claims onwardsClaims = Jwts.claims();
        onwardsClaims.putAll(claims);
        onwardsClaims.setSubject(claims.get("id"));
        onwardsClaims.setAudience("scoreboard");
        onwardsClaims.setIssuer("http://openliberty.io");
        onwardsClaims.setIssuedAt(getTimeRelativeToNow(-24));
        onwardsClaims.setExpiration(getTimeRelativeToNow(24));

        return Jwts.builder()
                .setClaims(onwardsClaims)
                .signWith(SignatureAlgorithm.RS256, signingKey)
                .compact();
    }
    
    private static Date getTimeRelativeToNow(int hours) {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.HOUR, hours);
      return calendar.getTime();
    }
}
