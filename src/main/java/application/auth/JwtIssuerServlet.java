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

import application.auth.JwtIssuer;
import java.io.FileInputStream;
import java.io.IOException;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;

public abstract class JwtIssuerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Resource(lookup = "jwtKeyStore")
    protected String keyStore;
    @Resource(lookup = "jwtKeyStorePassword")
    protected String keyStorePW;
    @Resource(lookup = "jwtKeyStoreAlias")
    protected String keyStoreAlias;

    protected static Key signingKey = null;


    private synchronized void getSigningKey() throws IOException {
        try {
            FileInputStream is = new FileInputStream(keyStore);
            KeyStore signingKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
            signingKeystore.load(is, keyStorePW.toCharArray());
            signingKey = signingKeystore.getKey(keyStoreAlias, keyStorePW.toCharArray());
        } catch (KeyStoreException e) {
            throw new IOException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } catch (CertificateException e) {
            throw new IOException(e);
        } catch (UnrecoverableKeyException e) {
            throw new IOException(e);
        }

    }

    protected String createJwt(Map<String, String> claims) throws IOException {
        if (signingKey == null) {
            getSigningKey();
        }
        return JwtIssuer.createSignedJwt(signingKey, claims);
    }

}
