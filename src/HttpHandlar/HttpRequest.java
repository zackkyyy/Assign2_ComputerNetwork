package HttpHandlar;

/**
 * Created by Zacky Kharboutli on 2018-02-15.
 */
public class    HttpRequest {
    private String filename;
    private String requestType;
    private String[] temp;
    private String request="";
    public HttpRequest(String method) {
        this.request=method;
        temp = method.split("\n");
        filename=""+temp[0].split(" ")[1];

        requestType=""+ temp[0]. split(" ")[0];
        }

    public String getFilename() {
        return filename;
    }

    public String getMethodName(){

        for(Object o : HTTP_RequestType.values())
        {
            if(requestType.contains(o.toString().toLowerCase()))
            {
                 requestType= o.toString();
            }
        }
        return requestType;
    }

    enum HTTP_RequestType {
        GET,
        POST,
        PUT
    }

    public String[] getTemp(){
        return temp;
    }

    public String getRequest(){
        return request;
    }
}