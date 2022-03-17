package org.services;

import org.UserDAO.UserImpl;
import org.UserDAO.UserDAO;
import org.glassfish.grizzly.http.server.Request;

import org.models.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.utils.Utility;



import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.List;

//API Return codes: 200, 201, 400, 404, 409, 500
//@ApiResponse(code = 200, message = "Successful")
//@ApiResponse(code = 201, message = "Successfully Created")
//@ApiResponse(code = 400, message = "Bad Request (Error in input parameters' format)")
//@ApiResponse(code = 404, message = "Not Found")
//@ApiResponse(code = 409, message = "Conflict (Resource already existing. User, for example)")
//@ApiResponse(code = 500, message = "Internal Server Error")

@Api(value = "/usermanager")  // "Users/Players Manager service"
@Path("/usermanager")

public class UserManagerService {

    private UserDAO us;



    public UserManagerService() {
        this.us = UserImpl.getInstance();
    }



    @POST
    @ApiOperation(value = "Login", notes = " ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful log", response = Users.class),
            @ApiResponse(code = 404, message = "Incorrect credentials") ,
            @ApiResponse(code = 402, message = "Incorrect password") ,
            @ApiResponse(code = 409, message = "User not validated after registry"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Users usrin) {
        Users usrout = this.us.login(usrin);
        String res = usrout.getUsername();
        switch (res){
            case "404": return Response.status(404).build();
            case "402": return Response.status(402).build();
            case "409": return Response.status(409).build();
            case "500": return Response.status(500).build();
            default: return Response.status(200).entity(usrout).build();
        }
    }



    @POST
    @ApiOperation(value = "Create a new user (Register)", notes = " ")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User Successfully Created"),
            @ApiResponse(code = 400, message = "Bad Request (Error in input parameters' format)"),
            @ApiResponse(code = 409, message = "Conflict (User already exists)"),
            @ApiResponse(code = 401, message = "Conflict (Email already registered)"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(Users usr, @Context Request request)  {
        int res = this.us.register(usr.getUsername(), usr.getEmail(), usr.getPassword(), Utility.getSiteURL(request));
        switch (res){
            case 201:
                return Response.status(200).build();
            case 400:
                return Response.status(400).build();
            case 409:
                return Response.status(409).build();
            case 401:
                return Response.status(401).build();
            default:
                return Response.status(500).build();
        }
    }

    @GET
    @ApiOperation(value = "Get the Url for verification registry", notes = " ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful verified"),
            @ApiResponse(code = 404, message = "User Not Found or already "),
            @ApiResponse(code = 409, message = "Conflict (User already verified)"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })


    @Path("/verify/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyUser(@PathParam("code") String code) {
        int res = this.us.verify(code);
        switch (res){
            case 404:
                return Response.status(404).build();
            case 500:
                return Response.status(500).build();
            case 409:
                return Response.status(409).build();
            default:
                return Response.status(200).build();
        }
    }



    @GET
    @ApiOperation(value = "Get the list of all Users", notes = " ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Users.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        List<Users> usrlist = this.us.getUsers();
        GenericEntity<List<Users>> listentity = new GenericEntity<List<Users>>(usrlist){};
        switch (usrlist.get(0).getUsername()){
            case "500":
                return Response.status(500).build();
            default:
                return Response.status(200).entity(listentity).build();
        }
    }



    @GET
    @ApiOperation(value = "Get User stats (User class) given its name", notes = " ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Users.class),
            @ApiResponse(code = 404, message = "User Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })


    @Path("/users/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("name") String name) {
        Users usr = this.us.getUser(name);
        switch (usr.getUsername()){
            case "404":
                return Response.status(404).build();
            case "500":
                return Response.status(500).build();
            default:
                return Response.status(200).entity(usr).build();
        }
    }

    @POST
    @ApiOperation(value = "Delete an existing User", notes = " ")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully deleted"),
            @ApiResponse(code = 404, message = "User doesn't exist"),
            @ApiResponse(code = 402, message = "Incorrect Password"),
            @ApiResponse(code = 405, message = "Bad URL request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })


    @Path("/users/{name}/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(Users us, @PathParam("name") String name) {
        int res = this.us.deleteUser(us.getUsername(), us.getEmail(), us.getPassword(), name);
        switch (res) {
            case 201:
                return Response.status(201).build();
            case 404:
                return Response.status(404).build();
            case 402:
                return Response.status(402).build();
            case 405:
                return Response.status(405).build();
            default:
                return Response.status(500).build();
        }
    }




}
