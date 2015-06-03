# EmployeeDataV2
import static com.jayway.restassured.RestAssured.expect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.jayway.restassured.filter.log.ErrorLoggingFilter;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class HealthCheck_API {

                static Response r;
                // Pick from a config File
                static Properties configProp;
                static String previousBuildnumber;

                public static void main(String[] args) throws Exception {

                                configProp = PropertiesReader.loadPropertyFile(Const.PROP_CONFIG);
                                previousBuildnumber = configProp.getProperty("previousBuildnumber");
                                HealthCheck_API hcheck = new HealthCheck_API();
                                String currentbuildNumber = hcheck.getBuildNumber();
                                if (!previousBuildnumber.trim().equals(currentbuildNumber.trim())) {
                                                // Batch file 1
                                                Process pr = Runtime.getRuntime().exec("cmd /c start /wait C:\\Users\\rshar262\\Documents\\MI_API_Automation\\trunk\\Execute.bat");
                                                pr.waitFor();
                                                // also set the property file with latest build number.
                                                updateLatestBuildNumber(currentbuildNumber.trim());
                                                                byte[] decoded = Base64.decodeBase64("UGFzc3dvcmQjODg=");
                                                String Password = new String(decoded, "UTF-8");
                                                SendHTMLEmail1("rshar262",Password, "LPQIU523.TRCW.US.AEXP.COM",
                                                                                "rahul.sharma19@aexp.com", "rahul.sharma19@aexp.com,Sanjoe.jacob@aexp.com",
                                                                                "Disputes API BVT test results for Build No: " + currentbuildNumber.trim()
                                                                                + " Hosted on 421 ENV", "<br>PFA the BVT results for the latest Build Hosted on E1 Environment.<br><br><b>"+getresults()+"</b><br><br>Thanks",
                                                                                displayDirectoryContents(), "BVT test Results.xls");
                                } 
                }
                String getBuildNumber() {
                                String currentBuildNumber;
                                Response r = expect()
                                .statusCode(200)
                                .given()
                                .relaxedHTTPSValidation()
                                .filter(new ErrorLoggingFilter())
                                .redirects()
                                .follow(true)
                                .with()
                                .headers("sm_universalid", "24e870219671276b7af2ec3986142534",
                                                                "Content-Type", "application/json")
                                                                .when()
                                                                .get("https://dwww421.app.aexp.com/merchant/services/disputes/healthCheck/stats");
                                // get("https://qwww209.americanexpress.com/merchant/services/disputes/healthCheck/stats");
                                String json = r.asString();
                                JsonPath jp = new JsonPath(json);
                                currentBuildNumber = jp.get("buildStats.buildNbr").toString();
                                return currentBuildNumber;
                }
                public static String displayDirectoryContents() throws IOException {
                                File choiceFile = null;
                                File currentDir = new File(
                                "C:\\Users\\rshar262\\Documents\\MI_API_Automation\\trunk\\Test_Output_files");
                                File[] files = currentDir.listFiles();
                                long lastMod = Long.MIN_VALUE;
                                for (File file : files) {
                                                if (file.lastModified() > lastMod) {
                                                                choiceFile = file;
                                                                lastMod = file.lastModified();
                                                }
                                }
                                
                                return choiceFile.getCanonicalFile()
                                + "\\API_Details_Disputes_Report.xlsx";
                }
                public static void updateLatestBuildNumber(String BuildNumber)
                throws FileNotFoundException, IOException {
                                // set latest build number to the config file
                                configProp.setProperty("previousBuildnumber", BuildNumber);
                                configProp.store(new FileOutputStream(Const.PROP_CONFIG), null);
                }
                public static String SendHTMLEmail1(final String username,
                                                final String password, String host, String from, String to,
                                                String Subject, String mailBody, String attachmentPath,
                                                String fileName) throws Exception {

                                // Set SMTP properties like Host and Port
                                Properties props = new Properties();
                                props.put("mail.smtp.auth", "true");
                                props.put("mail.smtp.starttls.enable", "true");
                                props.put("mail.smtp.host", host);
                                props.put("mail.smtp.port", "25");
                                // Create session with provided User name Password
                                Session session = Session.getInstance(props,
                                                                new javax.mail.Authenticator() {
                                                protected PasswordAuthentication getPasswordAuthentication() {
                                                                return new PasswordAuthentication(username, password);
                                                }
                                });
                                // Create Message
                                Message message = new MimeMessage(session);
                                // Set sender and recipients to message
                                message.setFrom(new InternetAddress(from));
                                String[] to_address = to.split(",");
                                InternetAddress[] addressTo = new InternetAddress[to_address.length];
                                for (int len = 0; len < to_address.length; len++) {
                                                addressTo[len] = new InternetAddress(to_address[len]);
                                }
                                message.setRecipients(Message.RecipientType.TO, addressTo);
                                // Set Message subject
                                message.setSubject(Subject);

                                // message.
                                BodyPart messageBodyPart = new MimeBodyPart();
                                messageBodyPart.setContent(mailBody,"text/html");
                                Multipart multipart = new MimeMultipart();
                                multipart.addBodyPart(messageBodyPart);

                                // Part two is attachment
                                messageBodyPart = new MimeBodyPart();
                                if (!(attachmentPath.isEmpty())) {
                                                DataSource source = new FileDataSource(attachmentPath);
                                                messageBodyPart.setDataHandler(new DataHandler(source));
                                                messageBodyPart.setFileName(fileName);
                                                multipart.addBodyPart(messageBodyPart);
                                }
                                // Send the complete message parts
                                message.setContent(multipart);
                                Transport.send(message);
                                return "Message Sent!!";
                }
                                public static String getresults() throws ParserConfigurationException, SAXException, IOException{
                                
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                
    String suitexml ="C:\\Users\\rshar262\\Documents\\MI_API_Automation\\trunk\\test-output\\testng-results.xml";
                dbFactory.setValidating(false);
    dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(suitexml);
    doc.getDocumentElement().normalize();
    Element suiteElem = (Element) doc.getElementsByTagName("testng-results").item(0);
    String Total = suiteElem.getAttribute("total");
    String passed = suiteElem.getAttribute("passed");
    String failed = suiteElem.getAttribute("failed");
                return "Total Test Cases : "+ Total + "<br>Total Passed : <font color=\"green\">" + passed + "</font>" + "<br>Total failed : <font color=\"red\">"+failed+"</font>";
                } 
}
