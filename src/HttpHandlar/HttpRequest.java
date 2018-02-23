package HttpHandlar;

/**
 * Created by Zacky Kharboutli on 2018-02-15.
 */
public class    HttpRequest {
    private  String imgToString;
    private String filename;
    private String requestType;
    private String[] temp;
    String uploadFileName;
    public HttpRequest(String method) {

        temp = method.split("\n");
        filename=""+temp[0].split(" ")[1];
        requestType=""+ temp[0]. split(" ")[0];
        for (int i = 10 ; i<temp.length ;i++){
            if (temp[i].contains("pic")){
                uploadFileName =temp[i].split("=")[1];
            }
            if(temp[i].contains("base64")){
                imgToString =temp[i].split(",")[1];
            }
        }
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

    public String getUploadFileName() {
        return uploadFileName;
    }

    enum HTTP_RequestType {
        GET,
        POST,
        PUT
    }


    public String getImgToString(){
        return imgToString;
    }

}