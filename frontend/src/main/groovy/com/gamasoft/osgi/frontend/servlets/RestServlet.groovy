package com.gamasoft.osgi.frontend.servlets
import com.gamasoft.osgi.frontend.tracker.ServiceProxy
import com.gamasoft.osgi.interfaces.frontend.TalksService
import groovy.json.JsonBuilder

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RestServlet extends HttpServlet {

    private final ServiceProxy<TalksService> talksService

    RestServlet(ServiceProxy<TalksService> talksService) {
        this.talksService = talksService
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        def parts = req.requestURI.toString().split('/')

        println " request ${req}"

        resp.writer.write "Welcome to conference ${parts} \n\n\n"


        talksService.call {

            if (parts[2] == "talks") {

                printResourceInJson(resp.writer, it.getTalks())


            } else {
                def talkId = parts[3]

                printResourceInJson(resp.writer, it.getTalkDetails(talkId))

            }

        }.orElse {

            resp.sendError 500, "The service is temporarily unavailable. Please try again later."
        }

//        /conferenceName/talks
//        /conferenceName/talk/talkName
//        /conferenceName/speakers
//        /conferenceName/speaker/speakerName
//        /conferenceName/schedule/yourName <= POST/PUT/DELETE
//        /conferenceName/schedule/yourName/talks


    }

    private void printResourceInJson(PrintWriter writer, Object resource) {
        def JsonBuilder jsonBuilder = new JsonBuilder()
        jsonBuilder(response: resource)
        writer.write(jsonBuilder.toPrettyString() + "\n\n\n\n end")
    }
}