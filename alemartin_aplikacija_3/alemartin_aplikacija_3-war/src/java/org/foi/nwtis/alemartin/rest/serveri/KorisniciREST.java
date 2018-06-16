/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.rest.serveri;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.alemartin.socket.SocketClient;
import sun.misc.BASE64Decoder;

/**
 * REST Web Service
 *
 * @author TOSHIBA
 */
@Path("korisnici")
public class KorisniciREST {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of KorisniciREST
     */
    public KorisniciREST() {
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.alemartin.rest.serveri.KorisniciREST
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String dohvatiSveKorisnike(@HeaderParam("authorization") String auth) {
        try {
            String userAuth = getUserAuthentication(auth);
            String username = userAuth.split(":")[0];
            String password = userAuth.split(":")[1];

            String request = createRequest(username, password, "LISTAJ");
            SocketClient socket = new SocketClient();
            String response = socket.sendRequest(request);

            if (response != null) {
                String code = response.split(";")[0];
                switch (code) {
                    case "ERR 11":
                        return createJson(response, true, "Neuspjela autentifikacija");
                    case "ERR 17":
                        return createJson(response, true, "Podaci ne postoje");
                    case "OK 10":
                        return createJson(response, false, null);
                    default:
                        return createJson(response, true, "Neispravna komanda");
                }
            } else {
                return createJson("", true, "Server nedostupan");
            }
        } catch (Exception ex) {
            return createJson("", true, "JSON pogreška");
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{korisnickoIme}")
    public String dohvatiKorisnika(@HeaderParam("authorization") String auth, @PathParam("korisnickoIme") String targetUser) {
        try {
            String userAuth = getUserAuthentication(auth);
            String username = userAuth.split(":")[0];
            String password = userAuth.split(":")[1];

            String request = createRequest(username, password, "LISTAJ");
            SocketClient socket = new SocketClient();
            String response = socket.sendRequest(request);

            if (response != null) {
                String code = response.split(";")[0];
                switch (code) {
                    case "ERR 11":
                        return createJson(response, true, "Neuspjela autentifikacija");
                    case "ERR 17":
                        return createJson(response, true, "Podaci ne postoje");
                    case "OK 10":
                        return createJson(response, false, null, targetUser);
                    default:
                        return createJson(response, true, "Neispravna komanda");
                }
            } else {
                return createJson(null, true, "Server nedostupan");
            }
        } catch (Exception ex) {
            return createJson("", true, "JSON pogreška");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("autentifikacija")
    public String authentication(@HeaderParam("authorization") String auth) {
        try {
            String userAuth = getUserAuthentication(auth);
            String username = userAuth.split(":")[0];
            String password = userAuth.split(":")[1];

            String request = createRequest(username, password, null);
            SocketClient socket = new SocketClient();
            String response = socket.sendRequest(request);

            if (response != null) {
                String code = response.split(";")[0];
                switch (code) {
                    case "ERR 11":
                        return createJson(response, true, "Neuspjela autentifikacija");
                    case "OK 10":
                        return createJson(response, false, null);
                    default:
                        return createJson(response, true, "Neispravna komanda");
                }
            } else {
                return createJson("", true, "Server nedostupan");
            }

        } catch (Exception ex) {
            return createJson("", true, "JSON Pogreška");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String dodajKorisnika(String content) {
        try {
            JsonReader jsonReader = Json.createReader((new StringReader(content)));
            JsonObject jsonObject = jsonReader.readObject();
            String firstname = jsonObject.getString("ime");
            String lastname = jsonObject.getString("prezime");
            String username = jsonObject.getString("korisnickoIme");
            String password = jsonObject.getString("lozinka");
            String subCommand = "DODAJ '" + firstname + "' '" + lastname + "'";

            String request = createRequest(username, password, subCommand);
            SocketClient socket = new SocketClient();
            String response = socket.sendRequest(request);

            if (response != null) {
                String code = response.split(";")[0];
                switch (code) {
                    case "ERR 10":
                        return createJson(response, true, "Korisnik već postoji");
                    case "OK 10":
                        return createJson(response, false, null);
                    default:
                        return createJson(response, true, "Neispravna komanda");
                }
            } else {
                return createJson("", true, "Server nedostupan");
            }

        } catch (Exception ex) {
            return createJson("", true, "JSON pogreška");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{korisnickoIme}")
    public String postJson(@PathParam("korisnickoIme") String username, String content) {
        return createJson("", true, "Nije dopušteno");
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{korisnickoIme}")
    public String azurirajKorisnika(@PathParam("korisnickoIme") String username, String content) {
        try {
            JsonReader jsonReader = Json.createReader((new StringReader(content)));
            JsonObject jsonObject = jsonReader.readObject();
            String firstname = jsonObject.getString("ime");
            String lastname = jsonObject.getString("prezime");
            String subCommand = "AZURIRAJ '" + firstname + "' '" + lastname + "'";

            String request = createRequest(username, "notNeeded", subCommand);
            SocketClient socket = new SocketClient();
            String response = socket.sendRequest(request);

            if (response != null) {
                String code = response.split(";")[0];
                switch (code) {
                    case "ERR 10":
                        return createJson(response, true, "Korisnik ne postoji");
                    case "OK 10":
                        return createJson(response, false, null);
                    default:
                        return createJson(response, true, "Neispravna komanda");
                }
            } else {
                return createJson("", true, "Server nedostupan");
            }

        } catch (Exception ex) {
            return createJson("", true, "JSON pogreška");
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJson(@HeaderParam("authorization") String auth, String content) {
        try {
            String userAuth = getUserAuthentication(auth);
            String username = userAuth.split(":")[0];
            String password = userAuth.split(":")[1];

            JsonReader jsonReader = Json.createReader((new StringReader(content)));
            JsonObject jsonObject = jsonReader.readObject();
            String firstname = jsonObject.getString("ime");
            String lastname = jsonObject.getString("prezime");
            String subCommand = "AZURIRAJ '" + firstname + "' '" + lastname + "'";

            String request = createRequest(username, password, subCommand);
            SocketClient socket = new SocketClient();
            String response = socket.sendRequest(request);

            if (response != null) {
                String code = response.split(";")[0];
                switch (code) {
                    case "ERR 10":
                        return createJson(response, true, "Korisnik ne postoji");
                    case "OK 10":
                        return createJson(response, false, null);
                    default:
                        return createJson(response, true, "Neispravna komanda");
                }
            } else {
                return createJson("", true, "Server nedostupan");
            }

        } catch (Exception ex) {
            return createJson("", true, "JSON pogreška");
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJson(@HeaderParam("authorization") String auth) {
        return createJson("", true, "Nije dopušteno");
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{korisnickoIme}")
    public String obrisiKorisnika(@HeaderParam("authentication") String auth, @PathParam("korisnickoIme") String username){
        return createJson("", true, "Nije dopušteno");
    }

    private String createRequest(String username, String password, String command) {
        String request = "KORISNIK " + username + "; LOZINKA " + password + ";";
        if (command != null) {
            request += " " + command + ";";
        }
        return request;
    }

    private String createJson(String response, boolean error, String message) {
        JsonObjectBuilder jsonObjectResponse = Json.createObjectBuilder();
        String[] responseArray = responseArray = response.split(";");
        if (responseArray.length >= 2) {
            String subResponse = responseArray[1];
            JsonReader jsonReader = Json.createReader(new StringReader(subResponse));
            JsonArray userArray = jsonReader.readArray();
            jsonReader.close();
            jsonObjectResponse.add("odgovor", userArray);
        } else {
            jsonObjectResponse.add("odgovor", Json.createArrayBuilder().build().toString());
        }
        if (!error) {
            jsonObjectResponse.add("status", "OK");
        } else {

            jsonObjectResponse.add("status", "ERR");
            jsonObjectResponse.add("poruka", message);
        }
        return jsonObjectResponse.build().toString();
    }

    private String createJson(String response, boolean error, String message, String targetUsername) {
        JsonObjectBuilder jsonObjectResponse = Json.createObjectBuilder();
        String[] responseArray = responseArray = response.split(";");
        if (responseArray.length >= 2) {
            String subResponse = responseArray[1];
            JsonReader jsonReader = Json.createReader(new StringReader(subResponse));
            JsonArray userArray = jsonReader.readArray();
            jsonReader.close();

            JsonObject userObject = null;
            for (int i = 0; i < userArray.size(); i++) {
                String username = userArray.getJsonObject(i).getString("ki");
                if (username.equals(targetUsername)) {
                    userObject = userArray.getJsonObject(i);
                    break;
                }
            }
            if (userObject != null) {
                JsonArrayBuilder jsonArrayUser = Json.createArrayBuilder();
                jsonArrayUser.add(userObject);
                jsonObjectResponse.add("odgovor", jsonArrayUser);
            } else {
                jsonObjectResponse.add("odgovor", "[]");
                error = true;
                message = "Korisnik ne postoji";
            }
        } else {
            jsonObjectResponse.add("odgovor", Json.createArrayBuilder().build().toString());
        }
        if (!error) {
            jsonObjectResponse.add("status", "OK");
        } else {

            jsonObjectResponse.add("status", "ERR");
            jsonObjectResponse.add("poruka", message);
        }
        return jsonObjectResponse.build().toString();
    }

    private String getUserAuthentication(String authString) {
        String decodedAuth = "";
        String[] authParts = authString.split("\\s+");
        String authInfo = authParts[1];
        //Decode the data back to original string
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(authInfo);
        } catch (IOException ex) {
            Logger.getLogger(KorisniciREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        decodedAuth = new String(bytes);
        return decodedAuth;
    }
}
