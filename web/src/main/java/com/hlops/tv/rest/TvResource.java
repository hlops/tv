package com.hlops.tv.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Created by tom on 3/27/15.
 */
@Path("hi")
public class TvResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public String hi() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://www.cn.ru/data/tv/playlist.m3u");
        return target.request(MediaType.TEXT_PLAIN_TYPE).get(String.class);
    }
}
