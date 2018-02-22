package HttpHandlar;

/**
 * Created by Zacky Kharboutli on 2018-02-15.
 */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

public class HttpResponse {
    private BufferedReader bufferedReader;
    private FileInputStream fileStream = null;
    private File file = null;
    private boolean somethingWrong = false; // this to detect if there is an error to print it
    private boolean isImage = false;
    private String path = "";
    private String status = "";
    private String response = "";
    private String httpBody = "";
    private byte[] buf;
    private boolean isPost=false;

    private String fileName="";
    private String imgString64="";
    private byte[] imgData;
    FileOutputStream fos;

    public HttpResponse(HttpRequest req, byte[] buffer) throws IOException {
      //  setPath(req.getFilename()+ "/dir")  ;
        // take the requested path from the Http request class
        if (req.getMethodName().equals(HttpRequest.HTTP_RequestType.GET.toString())) {

            setPath(req.getFilename());

            if (path.endsWith("/")) {
                setPath(path.substring(0, (path.length() - 1)));
            }
            if (path.isEmpty()){
                setPath(path+"/main.html");
            }
            //to ignore the difference between .html or htm
            else if (path.endsWith(".htm")) {
                path = path + "l";
            }
            // path always starts by dir because it is the only folder so far
            this.file = new File("dir" + path);

            //isAccessibleFile(file);  // check if the file is accessible
            if (isFileExist(file) || !isAccessibleFile(file)) {
                if (!isAccessibleFile(file)) {
                    //403 permission denied response if the file is not accessible
                    System.out.println("Permission denied: " + path);
                    setStatus("HTTP/1.1 403 Permission Denied " + "\r\n");
                    httpBody = ("<!DOCTYPE html>" +
                            "<HTML>" +
                            "<HEAD><TITLE>403 Forbidden: Permission Denied</TITLE></HEAD>" +
                            "<BODY><h1> 403 Permission Denied</h1>" +
                            "You do not have permission to access this directory or page </BODY></HTML>");
                    setUpHeader(FileType(path), httpBody.length());

                    somethingWrong = true;

                } else {
                    try {
                        //202 ok when everything is allrigt
                        setStatus("HTTP/1.1 200 OK " + "\r\n");
                        setUpHeader(FileType(path), file.length());
                        Stream(file, buffer);

                    } catch (IOException e) {
                        e.getMessage();
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }

            }
            // 302 FOUND Redirect URL
            else if ((path.contains("test1.html"))) {
                setStatus("HTTP/1.1 302  FOUND " + "\r\n");
                httpBody = "<!DOCTYPE html>" +
                        "<HTML>" +
                        "<HEAD><TITLE>302  Found</TITLE></HEAD>" +
                        "<BODY><h1> 302 FOUND</h1>" +
                        "\nThe file you requested has been moved " +
                        "<a href=\"/test.html\">HERE!</a>\n</BODY></HTML>";

                setUpHeader(FileType(path), httpBody.length());
                somethingWrong = true; // when error occur

                System.out.println(path);


            } else if (!isFileExist(file)) {
                //404 file not found response
                //this to be decided by the method isFIleExist
                setStatus("HTTP/1.1 404 NOT FOUND " + "\r\n");
                httpBody = "<!DOCTYPE html>" +
                        "<HTML>" +
                        "<HEAD><TITLE>404 Not Found</TITLE></HEAD>" +
                        "<BODY><h1> 404 NOT FOUND</h1>" +
                        "Requested page is not found!</BODY></HTML>";

                setUpHeader(FileType(path), httpBody.length());
                somethingWrong = true; // when error occur
            } else {
                //500 internal sever error
                setStatus("HTTP/1.1 500 Internal Server Error " + "\r\n");

                httpBody = (
                        "<!DOCTYPE html>" +
                                "<HTML>" +
                                "<HEAD><TITLE>500 Internal Server Error</TITLE></HEAD>" +
                                "<BODY><h1> 500 Internal Server Error</h1>" +
                                "The server encountered an internal error and was unable to complete your request</BODY></HTML>"
                );
                setUpHeader(FileType(path), httpBody.length());
                somethingWrong = true;
            }
            // POST METHOD ******************************
        } else if (req.getMethodName().equals(HttpRequest.HTTP_RequestType.POST.toString())) {

            fileName=req.getTemp()[14].split("=")[1];
            fileName=fileName.substring(0,fileName.length()-1);

            setPath("dir/subdir/"+fileName);
            imgString64=req.getTemp()[15].split(",")[1];
            imgData=javax.xml.bind.DatatypeConverter.parseBase64Binary(imgString64);

            setStatus("HTTP/1.1 200 OK " + "\r\n");
            setUpHeader(FileType(path), imgData.length);

            isImage=true;

            /***creating new file on server from data received from browser***/
            file= new File(path);
            fos=new FileOutputStream(file);
            fos.write(imgData);
            fos.close();
            //TODO implement post methodddddddddddddddddddddddddddddddddddddddddddddddddddddddddd

        }
    }



    /**
     * Method that checks if the path navigate to an exist file
     * This method guarantee as well that only png and html are available
     *
     * @param file
     * @return true or false
     */
    private boolean isFileExist(File file) {
        this.file = file;
        try {
            fileStream = new FileInputStream(file);

            if (path.endsWith(".png") ||
                    path.endsWith(".html")) {
                return true;

            } else {
                return false;
            }

        } catch (FileNotFoundException e) {
            System.out.println("File is not exist");
            return false;
        }
    }

    /**
     * Method to write the header details
     *
     * @param content
     * @param length
     */

    private void setUpHeader(String content, long length) {
        setResponse("Server Name: test Server \r\n"
                + "Connection:  Running \r\n"
                + "Content-Type: " + content + "\r\n"
                + "Content-Length: " + length + "\r\n"
                + "Date : " + new Date().toString() + "\r\n\r\n");

    }

    /**
     * Method to check if the requested folder is restricted
     * or cannot be accessed by the client
     * A directory called secret has been added to check the method
     *
     * @param file
     * @return true or false
     */
    private boolean isAccessibleFile(File file) {
        String secretFolder = "/secret/";
        try {
            if (path.contains(secretFolder)) {
                file.setExecutable(false);
                file.setReadable(false);
                file.setReadable(false);
                return false;
            }
        } catch (Exception e) {
            System.out.println("File cannot be accessed");
            e.getMessage();
        }
        return true;

    }

    /**
     * Method to check the type of the file
     *
     * @param file
     * @return String
     */
    private String FileType(String file) {
        if (file.substring(file.length() - 5, file.length()).compareTo(".html") == 0) {
            return "file type is Html";
        }
        if (file.substring(file.length() - 4, file.length()).compareTo(".png") == 0) {
            return "file type is png";
        } else if (file.substring(file.length() - 4, file.length()).compareTo(".htm") == 0) {
            return "file type is Html";
        }
        return "File is undefined";
    }

    /**
     * This method checks whether the file is Png or HTML and then stream it
     * to show it on the web page.
     * isImage is used to tell the client to stream the buf as it is an image
     *
     * @param file requested file to be streamed
     * @param buffer
     * @throws IOException
     */

    public void Stream(File file, byte[] buffer) throws IOException {
        this.file = file;
        int byteRead = 0;
        // first check if it is a picture
        if (path.endsWith(".png")) {
            buf = new byte[(int) file.length()];
            fileStream.read(buf);
            isImage = true;
        } else {
            do {
                // do this loop until the whole stream is over
                setResponse(new String(buffer, 0, byteRead));
            } while ((byteRead = fileStream.read(buffer)) != -1);

        }

    }


    private void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    private void setResponse(String response) {
        this.response += response;
    }

    public String getHttpBody() {
        return this.httpBody;
    }

    public boolean isSomethingWrong() {
        return somethingWrong;
    }

    public boolean isImage() {
        return isImage;
    }

    public byte[] getBuf() {
        return buf;
    }

    public byte[] getImgData(){
        return imgData;
    }

}
