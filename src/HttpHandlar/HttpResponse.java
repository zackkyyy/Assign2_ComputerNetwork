package HttpHandlar;

/**
 * Created by Zacky Kharboutli on 2018-02-15.
 */

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class HttpResponse {
    private boolean isPost = false;
    private String imgString64;
    private byte[] imgData;
    private FileOutputStream fos;
    private String fileName;
    private FileInputStream fileStream = null;
    private File file = null;
    private boolean somethingWrong = false; // this to detect if there is an error to print it
    private boolean isImage = false;
    private String path = "";
    private String status = "";
    private String response = "";
    private String httpBody = "";
    private byte[] buf;
    private responseFactory responseFactory;

    public HttpResponse(HttpRequest req, byte[] buffer) throws IOException {
        //  setPath(req.getFilename()+ "/dir")  ;
        // take the requested path from the Http request class
        if (req.getMethodName().equals(HttpRequest.HTTP_RequestType.GET.toString())) {

            setPath(req.getFilename());

            if (path.endsWith("/")) {
                setPath(path.substring(0, (path.length() - 1)));
            }

            //to ignore the difference between .html or htm
            else if (path.endsWith(".htm")) {
                path = path + "l";
            }
            // path always starts by dir because it is the only folder so far
            this.file = new File("dir" + path);
            if (file.isDirectory()) {
                for (File t : file.listFiles()) {
                    if (t.getName().equals("index.html")) {
                        path = path + "/index.html";
                        this.file = new File("dir" + path);
                    } else if (t.getName().equals("index.html")) {
                        path = path + "/index.html";
                        this.file = new File("dir" + path);
                    }
                }
            }

            if (path.endsWith("html") || path.endsWith("htm") || path.endsWith("png")) {  // server only accept limited numbers of forms
                if (isFileExist(file) || !isAccessibleFile(file)) {
                    if (!isAccessibleFile(file)) {
                        //403 permission denied response if the file is not accessible
                        System.out.println("Permission denied: " + path);
                        createResponse(HttpHandlar.responseFactory.ResponseNr.forbidden403);
                    } else if (isFileCharged(file)) {
                        //404 file is not free source
                        createResponse(HttpHandlar.responseFactory.ResponseNr.payment402);

                    } else {
                        try {
                            //200 ok when everything is allrigt
                            responseFactory = new responseFactory(HttpHandlar.responseFactory.ResponseNr.OK200);
                            setStatus(responseFactory.getStatus());
                            setUpHeader(FileType(path), file.length());
                            Stream(file, buffer);

                        } catch (IOException e) {
                            e.getMessage();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                } else if (path.startsWith("/delete/")) {
                    deleteFile(path);
                }
                // 302 FOUND Redirect URL
                else if ((path.contains("test1.html"))) {
                    createResponse(HttpHandlar.responseFactory.ResponseNr.found302);

                } else if (!isFileExist(file)) {
                    //404 file not found response
                    responseFactory = new responseFactory(HttpHandlar.responseFactory.ResponseNr.noContent204);
                    setStatus(responseFactory.getStatus());
                    setUpHeader(FileType(path), file.length());
                    Stream(file, buffer);
                } else {
                    //500 internal sever error
                    createResponse(HttpHandlar.responseFactory.ResponseNr.internal500);

                }
            } else if (file.isDirectory()){
                createResponse(HttpHandlar.responseFactory.ResponseNr.noContent204);

            }else {
                createResponse(HttpHandlar.responseFactory.ResponseNr.unSupported415);

            }

        }

        // POST METHOD ******************************
        else if (req.getMethodName().equals(HttpRequest.HTTP_RequestType.POST.toString())) {
            isPost = true;
            fileName = req.getUploadFileName();
            fileName = fileName.substring(0, fileName.length() - 1);

            setPath("dir/subdir/" + fileName);
            imgString64 = req.getImgToString();
            imgData = DatatypeConverter.parseBase64Binary(imgString64);
            System.out.println(fileName + " is file name       " + path + " is path");
            isImage = true;

            //creating new file on server from data received from browser
            file = new File(path);
            fos = new FileOutputStream(file);
            fos.write(imgData);
            fos.close();
            System.out.println(file + " is file ");

            setStatus("HTTP/1.1 201 created " + "\r\n");
            setUpHeader(FileType(path), file.length());

        } else if (req.getMethodName().equals(HttpRequest.HTTP_RequestType.PUT.toString())) {
            System.out.println("method is put");
            fileName = req.getUploadFileName();
            fileName = fileName.substring(0, fileName.length() - 1);

            setPath("dir/subdir/" + fileName);
            File temp = new File(path);

            //            file.delete();
            if (!temp.exists()) {
                imgString64 = req.getImgToString();
                imgData = javax.xml.bind.DatatypeConverter.parseBase64Binary(imgString64);
                setStatus("HTTP/1.1 200 OK " + "\r\n");
                httpBody = ("<!DOCTYPE html>" +
                        "<html>" +
                        "<HEAD><TITLE>Put request</TITLE></HEAD>" +
                        "<BODY><h1> Put request success </h1>" +
                        "The file has been added to the server successfully" +
                        " <li>To see the picture you added <a href=subdir/" + fileName + ">press here!</a></li>" +
                        " <li>To DELETE the file you added <a href=delete/" + fileName + ">press here!</a></li>" +
                        " </BODY></html>"
                );
                setUpHeader("the file is html", httpBody.length());
                setResponse(httpBody);

                //creating new file on server from data received from browser
                file = new File(path);
                fos = new FileOutputStream(file);
                fos.write(imgData);
                fos.close();
            } else {
                setStatus("HTTP/1.1 200 OK " + "\r\n");
                httpBody = ("<!DOCTYPE html>" +
                        "<html>" +
                        "<HEAD><TITLE>Put request</TITLE></HEAD>" +
                        "<BODY><h1> Put request cancelled</h1>" +
                        "The server already contain this file," +
                        " you can't do twice the same PUT request.</BODY></html>"
                );


                setUpHeader("file type is html", httpBody.length());
                setResponse(httpBody);

            }

        }

    }

    /**
     * Method that forms the status and the http body and the headers
     * Relies on the responseFactory class.
     *
     * @param responseNr enum that tells which error to be presented
     */
    private void createResponse(HttpHandlar.responseFactory.ResponseNr responseNr) {
        responseFactory = new responseFactory(responseNr);
        setStatus(responseFactory.getStatus());
        httpBody = responseFactory.getHttpBody();
        setUpHeader(FileType(path), httpBody.length());
        somethingWrong = true;
    }

    /**
     * Method to test the exception 403 payment
     *
     * @param file the file to be checked
     * @return true or false
     */
    private boolean isFileCharged(File file) {
        String payedFile = "payment.html";
        try {
            if (path.contains(payedFile)) {
                file.setExecutable(false);
                file.setReadable(false);
                file.setReadable(false);
                return true;
            }

        } catch (Exception e) {
            System.out.println("File cannot be accessed");
            e.getMessage();
        }

        return false;

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
            System.out.println("file is not available");
            return false;
        }
    }

    /**
     * Method to delete the file by using PUT-Method
     *
     * @param path file's location
     * @throws IOException
     */
    public void deleteFile(String path) throws IOException {
        String fileToDelete = path.split("/")[2];
        path = "dir/subdir/" + fileToDelete;
        Path p = Paths.get(path);
        file = new File(path);
        if (Files.deleteIfExists(p)) {
            createResponse(HttpHandlar.responseFactory.ResponseNr.deleted200);

        } else {
            setStatus("HTTP/1.1 200 OK " + "\r\n");
            httpBody = ("<!DOCTYPE html>" +
                    "<html>" +
                    "<HEAD><TITLE>Failure </TITLE></HEAD>" +
                    "<BODY><h1> The server doesn't contain the file to delete</h1>" +
                    "Try uploading the file " + fileToDelete + " first.</BODY></html>"
            );
            setUpHeader("file type is html", httpBody.length());
            setResponse(httpBody);
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
            return "html";
        }
        if (file.substring(file.length() - 4, file.length()).compareTo(".png") == 0) {
            return "png";
        } else if (file.substring(file.length() - 4, file.length()).compareTo(".htm") == 0) {
            return "html";
        }
        return "Unsupported file";
    }

    /**
     * This method checks whether the file is Png or html and then stream it
     * to show it on the web page.
     * isImage is used to tell the client to stream the buf as it is an image
     *
     * @param file   requested file to be streamed
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

    public byte[] getimgData() {
        return imgData;
    }

    public boolean isPost() {
        return isPost;
    }
}
