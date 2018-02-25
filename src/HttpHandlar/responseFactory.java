package HttpHandlar;

/**
 * Created by Zacky Kharboutli on 2018-02-24.
 */
public class responseFactory {
    private String httpBody;
    private String status;
    private String str;
    public enum ResponseNr {
        OK200, NotFound404, forbidden403, found302, payment402, internal500
    }
    public responseFactory(ResponseNr responseNr) {

        switch (responseNr) {  //
            case OK200:
                str = "HTTP/1.1 200 OK " + "\r\n";
                setStatus(str);
                break;
            case NotFound404:
                str = "HTTP/1.1 404 NOT FOUND " + "\r\n";
                httpBody = "<!DOCTYPE html>" +
                        "<HTML>" +
                        "<HEAD><TITLE>404 Not Found</TITLE></HEAD>" +
                        "<BODY><h1> 404 NOT FOUND</h1>" +
                        "Requested page is not found!</BODY></HTML>";
                setStatus(str);
                setHttpBody(httpBody);
                break;
            case forbidden403:
                str = "HTTP/1.1 403 Permission Denied " + "\r\n";
                httpBody = "<!DOCTYPE html>" +
                        "<HTML>" +
                        "<HEAD><TITLE>403 Forbidden: Permission Denied</TITLE></HEAD>" +
                        "<BODY><h1> 403 Permission Denied</h1>" +
                        "You do not have permission to access this directory or page </BODY></HTML>";
                setStatus(str);
                setHttpBody(httpBody);

                break;
            case found302:
                str = "HTTP/1.1 302  FOUND " + "\r\n";
                httpBody = "<!DOCTYPE html>" +
                        "<HTML>" +
                        "<HEAD><TITLE>302  Found</TITLE></HEAD>" +
                        "<BODY><h1> 302 FOUND</h1>" +
                        "\nThe file you requested has been moved " +
                        "<a href=\"/test.html\">HERE!</a>\n</BODY></HTML>";
                setHttpBody(httpBody);
                setStatus(str);
                break;
            case payment402:
                str = "HTTP/1.1 402 Payment required " + "\r\n";
                httpBody = "<!DOCTYPE html>" +
                        "<HTML>" +
                        "<HEAD><TITLE>402 Payment required </TITLE></HEAD>" +
                        "<BODY><h1> 402 Payment required for this request</h1>" +
                        "You do have to subscribe or pay to get access to this page </BODY></HTML>";
                setHttpBody(httpBody);
                setStatus(str);
                break;
            case internal500:
                str = "HTTP/1.1 500 Internal Server Error " + "\r\n";
                httpBody = "<!DOCTYPE html>" +
                        "<HTML>" +
                        "<HEAD><TITLE>500 Internal Server Error</TITLE></HEAD>" +
                        "<BODY><h1> 500 Internal Server Error</h1>" +
                        "The server encountered an internal error and was unable to complete your request</BODY></HTML>";
                setStatus(str);
                setHttpBody(httpBody);
                break;
        }
    }
    //Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String str) {
        status = str;
    }

    public String getHttpBody() {
        return httpBody;
    }

    public void setHttpBody(String str) {
        httpBody = str;
    }


}
